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
import java.io.IOException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    private static Logger LOG = LoggerFactory.getLogger(AbstractFileGrabber.class);
    
    public static class Statistics {
        private final int totalInFolder;
        private final int multipleInFolder;
        private final int totalInDatabase;
        private final int multipleInDatabase;
        private final int totalMatching;
        private final BeanSetWrapper<DatasetMetadata> missingFromDatabase;
        private final BeanSetWrapper<DatasetMetadata> missingFromFolder;
        private final BeanSetWrapper<DatasetMetadata> missingFromDatabaseInRange;
        private final BeanSetWrapper<Group> missingFromFolderGrouped;

        public Statistics(int totalInFolder, int multipleInFolder,
                int totalInDatabase, int multipleInDatabase, int totalMatching,
                SortedSet<DatasetMetadata> missingFromDatabase,
                SortedSet<DatasetMetadata> missingFromFolder,
                SortedSet<DatasetMetadata> missingFromDatabaseInRange,
                SortedSet<Group> missingFromFolderTimeOrdered) {
            super();
            this.totalInFolder = totalInFolder;
            this.multipleInFolder = multipleInFolder;
            this.totalInDatabase = totalInDatabase;
            this.multipleInDatabase = multipleInDatabase;
            this.totalMatching = totalMatching;
            this.missingFromDatabase =
                    new BeanSetWrapper<DatasetMetadata>(missingFromDatabase);
            this.missingFromFolder = 
                    new BeanSetWrapper<DatasetMetadata>(missingFromFolder);
            this.missingFromDatabaseInRange = 
                    new BeanSetWrapper<DatasetMetadata>(missingFromDatabaseInRange);
            this.missingFromFolderGrouped =
                    new BeanSetWrapper<Group>(missingFromFolderTimeOrdered);
        }

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
        
        public final int getTotalMissingFromDatabase() {
            return missingFromDatabase.size();
        }
        
        public final int getTotalMissingFromFolder() {
            return missingFromFolder.size();
        }

        public final BeanSetWrapper<DatasetMetadata> getMissingFromDatabase() {
            return missingFromDatabase;
        }

        public final BeanSetWrapper<DatasetMetadata> getMissingFromFolder() {
            return missingFromFolder;
        }

        public final BeanSetWrapper<DatasetMetadata> getMissingFromDatabaseInRange() {
            return missingFromDatabaseInRange;
        }

        public final BeanSetWrapper<Group> getMissingFromFolderGrouped() {
            return missingFromFolderGrouped;
        }
    }
    
    public enum ProblemType {
        METADATA_MISSING, METADATA_SIZE,
        FILE_MISSING, FILE_SIZE, FILE_SIZE_2,
        FILE_HASH, FILE_HASH_2, IO_ERROR;
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

        public final int getIoError() {
            return count(ProblemType.IO_ERROR);
        }

        public final int getFileSize2() {
            return count(ProblemType.FILE_SIZE_2);
        }

        public final int getFileSize() {
            return count(ProblemType.FILE_SIZE);
        }

        public final int getFileHash2() {
            return count(ProblemType.FILE_HASH_2);
        }

        public final int getFileHash() {
            return count(ProblemType.FILE_HASH);
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
    
    public static class Group implements Comparable<Group> {
        private final String basePathname;
        private DatasetMetadata inFolder;
        private DatasetMetadata matched;
        private List<DatasetMetadata> allInDatabase = new ArrayList<DatasetMetadata>();
        
        public Group(String basePathname) {
            super();
            this.basePathname = basePathname;
        }

        public final String getBasePathname() {
            return basePathname;
        }

        public final DatasetMetadata getInFolder() {
            return inFolder;
        }

        public final DatasetMetadata getMatched() {
            return matched;
        }

        public final List<DatasetMetadata> getAllInDatabase() {
            return allInDatabase;
        }

        public final void setInFolder(DatasetMetadata inFolder) {
            this.inFolder = inFolder;
        }

        public final void setMatched(DatasetMetadata matched) {
            this.matched = matched;
        }
        
        public final void addInDatabase(DatasetMetadata inDatabase) {
            this.allInDatabase.add(inDatabase);
        }

        @Override
        public int compareTo(Group o) {
            return basePathname.compareTo(o.getBasePathname());
        }
    }
    
    private static final Comparator<DatasetMetadata> ORDER_BY_ID =
            new Comparator<DatasetMetadata>() {
                @Override
                public int compare(DatasetMetadata o1, DatasetMetadata o2) {
                    return o1.getId().compareTo(o2.getId());
                }
    };
    
    private static final Comparator<DatasetMetadata> ORDER_BY_TIME_AND_BASE_PATH =
            new Comparator<DatasetMetadata>() {
                @Override
                public int compare(DatasetMetadata o1, DatasetMetadata o2) {
                    int res = Long.compare(
                            o1.getLastFileTimestamp().getTime(), 
                            o2.getLastFileTimestamp().getTime());
                    if (res == 0) {
                        res = o1.getFacilityFilePathnameBase().compareTo(
                                o2.getFacilityFilePathnameBase());
                    }
                    return res;
                }
    };
    
    private static final Comparator<DatasetMetadata> ORDER_BY_BASE_PATH_AND_TIME =
            new Comparator<DatasetMetadata>() {
                @Override
                public int compare(DatasetMetadata o1, DatasetMetadata o2) {
                    int res = o1.getFacilityFilePathnameBase().compareTo(
                            o2.getFacilityFilePathnameBase());
                    if (res == 0) {
                        res = Long.compare(
                                o1.getLastFileTimestamp().getTime(), 
                                o2.getLastFileTimestamp().getTime());
                    }
                    return res;
                }
    };
    
    private static final Comparator<DatasetMetadata> ORDER_BY_BASE_PATH_AND_TIME_AND_ID =
            new Comparator<DatasetMetadata>() {
                @Override
                public int compare(DatasetMetadata o1, DatasetMetadata o2) {
                    int res = o1.getFacilityFilePathnameBase().compareTo(
                            o2.getFacilityFilePathnameBase());
                    if (res == 0) {
                        res = Long.compare(
                                o1.getLastFileTimestamp().getTime(), 
                                o2.getLastFileTimestamp().getTime());
                    }
                    if (res == 0) {
                        res = o1.getId().compareTo(o2.getId());
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
    private Statistics beforeLWM;
    private Statistics afterLWM;
    private Statistics beforeHWM;
    private Statistics afterHWM;
    private Problems problems;
    private Statistics beforeQEnd;
    private Statistics afterQEnd;

    private Date lwm;
    private Date hwm;
    private Date qEnd;
    
    
    public Analyser(Paul services, Facility facility) {
        super(services, facility);
        fsm = services.getFacilityStatusManager();
        uncNameMapper = services.getUncNameMapper();
        emf = services.getEntityManagerFactory();
    }
    
    public Analyser analyse(Date lwmTimestamp, Date hwmTimestamp, Date queueEndTimestamp) {
        this.lwm = lwmTimestamp;
        this.hwm = hwmTimestamp;
        this.qEnd = queueEndTimestamp;
        LOG.info("Analysing queues and folders for " + getFacility().getFacilityName());
        SortedSet<DatasetMetadata> inFolder = buildInFolderMetadata();
        SortedSet<DatasetMetadata> inDatabase = buildInDatabaseMetadata();
        LOG.info("Gathering statistics for " + getFacility().getFacilityName());
        all = gatherStats(inFolder, inDatabase, PredicateUtils.truePredicate());
        if (lwmTimestamp == null) {
            beforeLWM = null;
            afterLWM = null;
        } else {
            final long lwm = lwmTimestamp.getTime();
            beforeLWM = gatherStats(inFolder, inDatabase, new Predicate() {
                public boolean evaluate(Object metadata) {
                    return ((DatasetMetadata) metadata).getLastFileTimestamp().getTime() <= lwm;
                }
            });
            afterLWM = gatherStats(inFolder, inDatabase, new Predicate() {
                public boolean evaluate(Object metadata) {
                    return ((DatasetMetadata) metadata).getLastFileTimestamp().getTime() > lwm;
                }
            });
        }
        if (hwmTimestamp == null) {
            beforeHWM = null;
            afterHWM = null;
        } else {
            final long hwm = hwmTimestamp.getTime();
            beforeHWM = gatherStats(inFolder, inDatabase, new Predicate() {
                public boolean evaluate(Object metadata) {
                    return ((DatasetMetadata) metadata).getLastFileTimestamp().getTime() <= hwm;
                }
            });
            afterHWM = gatherStats(inFolder, inDatabase, new Predicate() {
                public boolean evaluate(Object metadata) {
                    return ((DatasetMetadata) metadata).getLastFileTimestamp().getTime() > hwm;
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
                    return ((DatasetMetadata) metadata).getLastFileTimestamp().getTime() <= qEnd;
                }
            });
            afterQEnd = gatherStats(inFolder, inDatabase, new Predicate() {
                public boolean evaluate(Object metadata) {
                    return ((DatasetMetadata) metadata).getLastFileTimestamp().getTime() > qEnd;
                }
            });
        }
        LOG.info("Performing queue entry integrity checks for " + getFacility().getFacilityName());
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
                try {
                    LOG.error("stored hash - " + datafile.getDatafileHash());
                    File file = new File(datafile.getCapturedFilePathname());
                    if (!file.exists()) {
                        logProblem(dataset, datafile, ProblemType.FILE_MISSING, problems, 
                                "Data file missing: " + file);
                    } else if (file.length() != datafile.getFileSize()) {
                        logProblem(dataset, datafile, ProblemType.FILE_SIZE, problems,
                                "Data file size mismatch: " + file + 
                                ": admin metadata says " + datafile.getFileSize() + 
                                " but actual captured file size is " + file.length());
                    } else if (!datafile.getDatafileHash().equals(HashUtils.fileHash(file))) {
                        logProblem(dataset, datafile, ProblemType.FILE_HASH, problems,
                                "Data file hash mismatch between metadata and " + file);
                    } else {
                        LOG.error("captured hash - " + HashUtils.fileHash(file));
                    }
                    File source = new File(datafile.getSourceFilePathname());
                    if (source.exists()) {
                        if (source.length() != file.length()) {
                            logProblem(dataset, datafile, ProblemType.FILE_SIZE_2, problems, 
                                    "Data file size mismatch: " + file + 
                                    ": original file size is " + source.length() + 
                                    " but actual captured file size is " + file.length());
                        } else if (!datafile.getDatafileHash().equals(HashUtils.fileHash(source))) {
                            logProblem(dataset, datafile, ProblemType.FILE_HASH_2, problems,
                                    "Data file hash mismatch between metadata and " + source);
                        } else {
                            LOG.error("source hash - " + HashUtils.fileHash(source));
                        }
                    }
                } catch (IOException ex) {
                    LOG.error("Unexpected IOException while checking hashes", ex);
                    logProblem(dataset, datafile, ProblemType.IO_ERROR, problems,
                            "IO error while checking file hashes - see logs");

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
        // datasets in the database that match a dataset in the folder (via filtered views)
        TreeSet<DatasetMetadata> matchedInDatabase = 
                new TreeSet<DatasetMetadata>(ORDER_BY_ID);
        // datasets in the folder that match a dataset in the database (via filtered views)
        TreeSet<DatasetMetadata> matchedInFolder = 
                new TreeSet<DatasetMetadata>(ORDER_BY_BASE_PATH_AND_TIME);
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
        int totalInFolder = 0;
        int totalInDatabase = 0;
        int totalMatching = 0;
        int multipleInFolder = 0;
        int multipleInDatabase = 0;
        if (fit.hasNext()) {
            f = fit.next();
            totalInFolder++;
        }
        if (dit.hasNext()) {
            d = dit.next();
            totalInDatabase++;
        }
        while (f != null || d != null) {
            final boolean skipping = f == null || d == null;
            final int test = ORDER_BY_BASE_PATH_AND_TIME_WITH_NULLS.compare(f, d);
            if (test == 0) {
                totalMatching++;
                matchedInDatabase.add(d);
                matchedInFolder.add(f);
            }
            check(test, f, d, inFolder, inDatabase);
            if (test <= 0 || skipping) {
                if (fit.hasNext()) {
                    fPrev = f;
                    f = fit.next();
                    totalInFolder++;
                    if (fPrev != null && 
                            fPrev.getFacilityFilePathnameBase().equals(f.getFacilityFilePathnameBase())) {
                        // We shouldn't see any of these ...
                        multipleInFolder++;
                    }
                } else {
                    f = null;
                }
            }
            if (test >= 0 || skipping) {
                if (dit.hasNext()) {
                    dPrev = d;
                    d = dit.next();
                    totalInDatabase++;
                    if (dPrev != null && 
                            dPrev.getFacilityFilePathnameBase().equals(d.getFacilityFilePathnameBase())) {
                        multipleInDatabase++;
                    }
                } else {
                    d = null;
                }
            } 
        }
        TreeSet<DatasetMetadata> missingFromDatabase = buildRemainderSet(
                ORDER_BY_BASE_PATH_AND_TIME, predicate, inFolder, matchedInFolder);
        TreeSet<DatasetMetadata> missingFromFolder = buildRemainderSet(
                ORDER_BY_BASE_PATH_AND_TIME, predicate, inDatabase, matchedInDatabase);
        
        TreeSet<DatasetMetadata> missingFromDatabaseInRange = rangeFilter(missingFromDatabase);
        TreeSet<Group> missingFromFolderGrouped = groupDatasets(
                missingFromFolder, inFolder, inDatabase);

        Statistics stats = new Statistics(totalInFolder, multipleInFolder, 
                totalInDatabase, multipleInDatabase, totalMatching, 
                missingFromDatabase, missingFromFolder, 
                missingFromDatabaseInRange, missingFromFolderGrouped);
        return stats;
    }
    
    private TreeSet<DatasetMetadata> rangeFilter(TreeSet<DatasetMetadata> missingFromDatabase) {
        TreeSet<DatasetMetadata>res = new TreeSet<DatasetMetadata>(ORDER_BY_TIME_AND_BASE_PATH);
        for (DatasetMetadata d : missingFromDatabase) {
            if ((lwm != null && d.getFirstFileTimestamp().getTime() < lwm.getTime()) ||
                (hwm != null && d.getLastFileTimestamp().getTime() > hwm.getTime())) {
                continue;
            }
            res.add(d);
        }
        return res;
    }

    private TreeSet<Group> groupDatasets(
            TreeSet<DatasetMetadata> missingFromFolder,
            Collection<DatasetMetadata> inFolder,
            Collection<DatasetMetadata> inDatabase) {
        TreeSet<Group> res = new TreeSet<Group>();
        PushbackIterator<DatasetMetadata> dit = 
                new PushbackIterator<DatasetMetadata>(inDatabase.iterator());
        PushbackIterator<DatasetMetadata> fit = 
                new PushbackIterator<DatasetMetadata>(inFolder.iterator());
        Group t = null;
        for (DatasetMetadata d : missingFromFolder) {
            if (t == null || !t.getBasePathname().equals(d.getFacilityFilePathnameBase())) {
                t = new Group(d.getFacilityFilePathnameBase());
                res.add(t);
            }
            t.addInDatabase(d);
            t.setInFolder(getMatching(fit, t.getBasePathname()));
            if (t.getInFolder() != null) { 
                t.setMatched(getMatching(dit, t.getBasePathname(),
                        t.getInFolder().getLastFileTimestamp()));
            }
            if (t.getMatched() != null) {
                t.addInDatabase(t.getMatched());
            }
        }
        return res;
    }

    private DatasetMetadata getMatching(PushbackIterator<DatasetMetadata> it,
            String basePathname) {
        while (it.hasNext()) {
            DatasetMetadata d = it.next();
            int cmp = d.getFacilityFilePathnameBase().compareTo(basePathname);
            if (cmp == 0) {
                return d;
            } else if (cmp > 0) {
                it.pushback(d);
                return null;
            }
        }
        return null;
    }

    private DatasetMetadata getMatching(PushbackIterator<DatasetMetadata> it,
            String basePathname, Date timestamp) {
        while (it.hasNext()) {
            DatasetMetadata d = it.next();
            int cmp = d.getFacilityFilePathnameBase().compareTo(basePathname);
            if (cmp == 0) {
                int cmp2 = Long.compare(d.getCaptureTimestamp().getTime(), timestamp.getTime());
                if (cmp2 == 0) {
                    return d;
                } else if (cmp2 > 0) {
                    return null;
                }
            } else if (cmp > 0) {
                it.pushback(d);
                return null;
            }
        }
        return null;
    }

    private void check(int test, DatasetMetadata f, DatasetMetadata d, 
            Collection<DatasetMetadata> inFolder, Collection<DatasetMetadata> inDatabase) {
//        LOG.error("f is " + f + ", d is " + d + ", test is " + test);
//        if (f == null || d == null) {
//            return;
//        }
//        if (test == 0) {
//            if (!inFolder.contains(f)) {
//                LOG.error("f is not in source");
//            }
//            if (!inDatabase.contains(d)) {
//                LOG.error("d is not in source");
//            }
//        } else {
//            if (inFolder.contains(f) && inDatabase.contains(d)) {
//                LOG.error("f & d are both in sources");
//            }
//        }
    }

    private TreeSet<DatasetMetadata> buildRemainderSet(
            Comparator<DatasetMetadata> comparator, Predicate predicate,
            Collection<DatasetMetadata> include, Collection<DatasetMetadata> exclude) {
        TreeSet<DatasetMetadata> res = new TreeSet<DatasetMetadata>(comparator);
        for (@SuppressWarnings("unchecked") Iterator<DatasetMetadata> it = 
                IteratorUtils.filteredIterator(include.iterator(), predicate);
                it.hasNext(); ) {
            res.add(it.next());
        }
        for (@SuppressWarnings("unchecked") Iterator<DatasetMetadata> it = 
                IteratorUtils.filteredIterator(exclude.iterator(), predicate);
                it.hasNext(); ) {
            res.remove(it.next());
        }
        return res;
    }

    private SortedSet<DatasetMetadata> buildInDatabaseMetadata() {
        TreeSet<DatasetMetadata> inDatabase =  new TreeSet<DatasetMetadata>(ORDER_BY_BASE_PATH_AND_TIME_AND_ID);
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
            inFolder.add(entry.assembleDatasetMetadata(null, session, new File("")));
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

    public final Statistics getBeforeLWM() {
        return beforeLWM;
    }

    public final Statistics getAfterLWM() {
        return afterLWM;
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

    public final Date getLWM() {
        return lwm;
    }

    public final Date getHWM() {
        return hwm;
    }

    public final Date getQEnd() {
        return qEnd;
    }

    public final void setProblems(Problems problems) {
        this.problems = problems;
    }
    
}
