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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import au.edu.uq.cmm.eccles.FacilitySession;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.status.Facility;
import au.edu.uq.cmm.paul.status.FacilityStatus;
import au.edu.uq.cmm.paul.status.FacilityStatusManager;
import au.edu.uq.cmm.paul.watcher.UncPathnameMapper;

/**
 * This variation on the DataGrabber gathers DatasetMetadata records all files
 * in a facility's directory tree, and compares them against the records in the DB. 
 * 
 * @author scrawley
 */
public class CatchupAnalyser extends AbstractFileGrabber {
    
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
    private int totalDatasets;
    private FacilityStatusManager fsm;
    private EntityManagerFactory emf;
    private int totalUngrabbed;
    private int totalGrabbed;
    private UncPathnameMapper uncNameMapper;
    
    public CatchupAnalyser(Paul services, Facility facility) {
        super(services, facility);
        fsm = services.getFacilityStatusManager();
        uncNameMapper = services.getUncNameMapper();
        emf = services.getEntityManagerFactory();
    }
    
    public CatchupAnalyser analyse() {
        String folderName = getFacility().getFolderName();
        if (folderName == null) {
            return null;
        }
        File localDir = uncNameMapper.mapUncPathname(folderName);
        if (localDir == null) {
            return null;
        }
        fsm.getStatus(getFacility()).setLocalDirectory(localDir);
        this.totalUngrabbed = 0;
        this.totalGrabbed = 0;
        this.totalDatasets = analyseTree(localDir, Long.MIN_VALUE, Long.MAX_VALUE);
        for (Runnable runnable : queue) {
            WorkEntry entry = (WorkEntry) runnable;
            FacilitySession session = fsm.getLoginDetails(
                    getFacility().getFacilityName(), entry.getTimestamp().getTime());
            entry.pretendToGrabFiles();
            DatasetMetadata metadata = entry.assembleMetadata(null, session, new File(""));
            int nosHits = 0;
            EntityManager em = emf.createEntityManager();
            try {
                TypedQuery<DatasetMetadata> query = em.createQuery(
                        "from DatasetMetadata m where m.facilityFilePathnameBase = :pathname", 
                        DatasetMetadata.class);
                query.setParameter("pathname", metadata.getFacilityFilePathnameBase());
                for (DatasetMetadata m : query.getResultList()) {
                    if (metadata.getIndicativeFileTimestamp() == m.getIndicativeFileTimestamp()) {
                        nosHits++;
                    }
                }
            } finally {
                em.close();
            }
            if (nosHits > 1) {
                LOG.warn("We have multiple DatasetMetadata records for " +
                        metadata.getFacilityFilePathnameBase() + " at " + 
                        metadata.getIndicativeFileTimestamp());
            }
            if (nosHits == 0) {
                this.totalUngrabbed++;
            } else {
                this.totalGrabbed++;
            }
        }
        return this;
    }

    @Override
    protected void enqueueWorkEntry(WorkEntry entry) {
        queue.add(entry);
    }

    public final int getTotalDatasets() {
        return totalDatasets;
    }

    public final int getTotalUngrabbed() {
        return totalUngrabbed;
    }

    public final int getTotalGrabbed() {
        return totalGrabbed;
    }

}
