/*
* Copyright 2012, CMM, University of Queensland.
*
* This file is part of Paul.
*
* Paul is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Paul is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Paul. If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.uq.cmm.paul.grabber;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

import au.edu.uq.cmm.eccles.FacilitySession;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.status.Facility;
import au.edu.uq.cmm.paul.status.FacilityStatusManager;
import au.edu.uq.cmm.paul.watcher.UncPathnameMapper;


/**
 * This variation on the DataGrabber gathers DatasetMetadata records all files
 * in a facility's directory tree, and compares them against the records in the DB.
 * The analyser also performs some basic integrity checks on the queue.
 * 
 * @author scrawley
 */
public class Analyser extends AbstractFileGrabber {
    
    public static class Statistics {
        private int totalInFolder;
        private int multipleInFolder;
        private int totalInDatabase;
        private int multipleInDatabase;
        private int totalMatching;

        public final int getTotalInFolder() {
            return totalInFolder;
        }

        public final int getMultipleInFolder() {
            return multipleInFolder;
        }

        public final int getTotalInDatabase() {
            return totalInDatabase;
        }

        public final int getMultipleInDatabase() {
            return multipleInDatabase;
        }

        public final int getTotalMatching() {
            return totalMatching;
        }
    }
    
    public enum ProblemType {
        METADATA_MISSING, METADATA_SIZE,
        FILE_MISSING, FILE_SIZE, FILE_SIZE_2;
    }
    
    public static class Problem {
        private final DatasetMetadata dataset;
        private final DatafileMetadata datafile;
        private final String details;
        private final ProblemType type;
        
        public Problem(DatasetMetadata dataset, DatafileMetadata datafile, 
                ProblemType type, String details) {
            super();
            this.dataset = dataset;
            this.datafile = datafile;
            this.details = details;
            this.type = type;
        }
        
        public final DatasetMetadata getDataset() {
            return dataset;
        }
        
        public final DatafileMetadata getDatafile() {
            return datafile;
        }
        
        public final String getDetails() {
            return details;
        }

        public final ProblemType getType() {
            return type;
        }
    }
    
    public static class Problems {
        private final List<Problem> problems;

        public Problems(List<Problem> problem) {
            this.problems = problem;
        }

        public int getNosProblems() {
            return problems.size();
        }

        public final int getFileSize2() {
            return count(ProblemType.FILE_SIZE_2);
        }

        public final int getFileSize() {
            return count(ProblemType.FILE_SIZE);
        }

        public final int getFileMissing() {
            return count(ProblemType.FILE_MISSING);
        }

        public final int getMetadataSize() {
            return count(ProblemType.METADATA_SIZE);
        }

        public final int getMetadataMissing() {
            return count(ProblemType.METADATA_MISSING);
        }

        private int count(ProblemType type) {
            int count = 0;
            for (Problem problem : problems) {
                if (problem.getType() == type) {
                    count++;
                }
            }
            return count;
        }

        public final List<Problem> getProblems() {
            return problems;
        }
    }
    
    private static final Comparator<DatasetMetadata> ORDER_BY_BASE_PATH_AND_TIME =
            new Comparator<DatasetMetadata>() {
                @Override
                public int compare(DatasetMetadata o1, DatasetMetadata o2) {
                    int res = o1.getFacilityFilePathnameBase().compareTo(
                            o2.getFacilityFilePathnameBase());
                    if (res == 0) {
                        res = Long.compare(
                                o1.getIndicativeFileTimestamp().getTime(), 
                                o2.getIndicativeFileTimestamp().getTime());
                    }
                    return res;
                }
    };
            
    private static final Comparator<DatasetMetadata> ORDER_BY_BASE_PATH_AND_TIME_WITH_NULLS =
            new Comparator<DatasetMetadata>() {
                @Override
                public int compare(DatasetMetadata o1, DatasetMetadata o2) {
                    if (o1 == o2) {
                        return 0;
                    } else if (o1 == null) {
                        return -1;
                    } else if (o2 == null) {
                        return 1;
                    } else {
                        return ORDER_BY_BASE_PATH_AND_TIME.compare(o1, o2);
                    }
                }
    };
    
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
    private FacilityStatusManager fsm;
    private EntityManagerFactory emf;
    private UncPathnameMapper uncNameMapper;
    private Statistics all;
    private Statistics beforeHWM;
    private Statistics afterHWM;
    private Problems problems;
    private Statistics beforeQEnd;
    private Statistics afterQEnd;
    
    
    public Analyser(Paul services, Facility facility) {
        super(services, facility);
        fsm = services.getFacilityStatusManager();
        uncNameMapper = services.getUncNameMapper();
        emf = services.getEntityManagerFactory();
    }
    
    public Analyser analyse(Date hwmTimestamp, Date queueEndTimestamp) {
        SortedSet<DatasetMetadata> inFolder = buildInFolderMetadata();
        SortedSet<DatasetMetadata> inDatabase = buildInDatabaseMetadata();
        all = gatherStats(inFolder, inDatabase, PredicateUtils.truePredicate());
        if (hwmTimestamp == null) {
            beforeHWM = null;
            afterHWM = null;
        } else {
            final long hwm = hwmTimestamp.getTime();
            beforeHWM = gatherStats(inFolder, inDatabase, new Predicate() {
                public boolean evaluate(Object metadata) {
                    return ((DatasetMetadata) metadata).getIndicativeFileTimestamp().getTime() <= hwm;
                }
            });
            afterHWM = gatherStats(inFolder, inDatabase, new Predicate() {
                public boolean evaluate(Object metadata) {
                    return ((DatasetMetadata) metadata).getIndicativeFileTimestamp().getTime() > hwm;
                }
            });
        }
        if (queueEndTimestamp == null) {
            beforeQEnd = null;
            afterQEnd = null;
        } else {
            final long qEnd = queueEndTimestamp.getTime();
            beforeQEnd = gatherStats(inFolder, inDatabase, new Predicate() {
                public boolean evaluate(Object metadata) {
                    return ((DatasetMetadata) metadata).getIndicativeFileTimestamp().getTime() <= qEnd;
                }
            });
            afterQEnd = gatherStats(inFolder, inDatabase, new Predicate() {
                public boolean evaluate(Object metadata) {
                    return ((DatasetMetadata) metadata).getIndicativeFileTimestamp().getTime() > qEnd;
                }
            });
        }
        problems = integrityCheck(inDatabase);
        return this;
    }
    
    private Problems integrityCheck(SortedSet<DatasetMetadata> inDatabase) {
        List<Problem> problems = new ArrayList<Problem>();
        for (DatasetMetadata dataset : inDatabase) {
            File adminFile = new File(dataset.getMetadataFilePathname());
            if (!adminFile.exists()) {
                logProblem(dataset, null, ProblemType.METADATA_MISSING, problems, 
                        "Metadata file missing: " + adminFile);
            } else if (adminFile.length() == 0) {
                logProblem(dataset, null, ProblemType.METADATA_SIZE, problems, 
                        "Metadata file empty: " + adminFile);
            }
            for (DatafileMetadata datafile : dataset.getDatafiles()) {
                File file = new File(datafile.getCapturedFilePathname());
                if (!file.exists()) {
                    logProblem(dataset, datafile, ProblemType.FILE_MISSING, problems, 
                            "Data file missing: " + file);
                } else if (file.length() != datafile.getFileSize()) {
                    logProblem(dataset, datafile, ProblemType.FILE_SIZE, problems,
                            "Data file size mismatch: " + file + 
                            ": admin metadata says " + datafile.getFileSize() + 
                            " but actual captured file size is " + file.length());
                }
                File source = new File(datafile.getSourceFilePathname());
                if (source.exists()) {
                    if (source.length() != file.length()) {
                        logProblem(dataset, datafile, ProblemType.FILE_SIZE_2, problems, 
                                "Data file size mismatch: " + file + 
                                ": original file size is " + source.length() + 
                                " but actual captured file size is " + file.length());
                    }
                }
            }
        }
        LOG.info("Queue integrity check for '" + getFacility().getFacilityName() + 
                 "' found " + problems.size() + " problems (listed above)");
        return new Problems(problems);
    }
    
    private void logProblem(DatasetMetadata dataset, DatafileMetadata datafile, ProblemType type,
            List<Problem> list, String details) {
        LOG.info("Problem in dataset #" + dataset.getId() + ": " + details);
        list.add(new Problem(dataset, datafile, type, details));
    }

    private Statistics gatherStats(
            Collection<DatasetMetadata> inFolder,
            Collection<DatasetMetadata> inDatabase,
            Predicate predicate) {
        Statistics stats = new Statistics();
        @SuppressWarnings("unchecked")
        Iterator<DatasetMetadata> fit = 
                IteratorUtils.filteredIterator(inFolder.iterator(), predicate);
        @SuppressWarnings("unchecked")
        Iterator<DatasetMetadata> dit = 
                IteratorUtils.filteredIterator(inDatabase.iterator(), predicate);
        DatasetMetadata f = null;
        DatasetMetadata fPrev = null;
        DatasetMetadata d = null;
        DatasetMetadata dPrev = null;
        if (fit.hasNext()) {
            f = fit.next();
            stats.totalInFolder++;
        }
        if (dit.hasNext()) {
            d = dit.next();
            stats.totalInDatabase++;
        }
        while (fit.hasNext() || dit.hasNext()) {
            boolean skipping = !(fit.hasNext() && dit.hasNext());
            int test = ORDER_BY_BASE_PATH_AND_TIME_WITH_NULLS.compare(f, d);
            if (test == 0) {
                stats.totalMatching++;
            }
            if ((test <= 0 || skipping) && fit.hasNext()) {
                fPrev = f;
                f = fit.next();
                stats.totalInFolder++;
                if (fPrev != null && 
                        fPrev.getFacilityFilePathnameBase().equals(f.getFacilityFilePathnameBase())) {
                    stats.multipleInFolder++;
                }
            }
            if ((test >= 0 || skipping) && dit.hasNext()) {
                dPrev = d;
                d = dit.next();
                stats.totalInDatabase++;
                if (dPrev != null && 
                        dPrev.getFacilityFilePathnameBase().equals(d.getFacilityFilePathnameBase())) {
                    stats.multipleInDatabase++;
                }
            }
        }
        return stats;
    }

    private SortedSet<DatasetMetadata> buildInDatabaseMetadata() {
        TreeSet<DatasetMetadata> inDatabase =  new TreeSet<DatasetMetadata>(ORDER_BY_BASE_PATH_AND_TIME);
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<DatasetMetadata> query = em.createQuery(
                    "from DatasetMetadata m where m.facilityName = :name", 
                    DatasetMetadata.class);
            query.setParameter("name", getFacility().getFacilityName());
            inDatabase.addAll(query.getResultList());
        } finally {
            em.close();
        }
        return inDatabase;
    }

    private SortedSet<DatasetMetadata> buildInFolderMetadata() {
        TreeSet<DatasetMetadata> inFolder = new TreeSet<DatasetMetadata>(ORDER_BY_BASE_PATH_AND_TIME);
        String folderName = getFacility().getFolderName();
        if (folderName == null) {
            return inFolder;
        }
        File localDir = uncNameMapper.mapUncPathname(folderName);
        if (localDir == null) {
            return inFolder;
        }
        fsm.getStatus(getFacility()).setLocalDirectory(localDir);
        analyseTree(localDir, Long.MIN_VALUE, Long.MAX_VALUE);
        for (Runnable runnable : queue) {
            WorkEntry entry = (WorkEntry) runnable;
            FacilitySession session = fsm.getLoginDetails(
                    getFacility().getFacilityName(), entry.getTimestamp().getTime());
            entry.pretendToGrabFiles();
            inFolder.add(entry.assembleMetadata(null, session, new File("")));
        }
        return inFolder;
    }

    @Override
    protected void enqueueWorkEntry(WorkEntry entry) {
        queue.add(entry);
    }

    public final Statistics getAll() {
        return all;
    }

    public final Statistics getBeforeHWM() {
        return beforeHWM;
    }

    public final Statistics getAfterHWM() {
        return afterHWM;
    }

    public final Statistics getBeforeQEnd() {
        return beforeQEnd;
    }

    public final Statistics getAfterQEnd() {
        return afterQEnd;
    }

    public final Problems getProblems() {
        return problems;
    }

    public final void setProblems(Problems problems) {
        this.problems = problems;
    }
    
}
