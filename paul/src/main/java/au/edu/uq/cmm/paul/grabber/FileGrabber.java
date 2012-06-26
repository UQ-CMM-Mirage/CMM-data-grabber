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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.aclslib.service.CompositeServiceBase;
import au.edu.uq.cmm.aclslib.service.Service;
import au.edu.uq.cmm.aclslib.service.ServiceException;
import au.edu.uq.cmm.paul.DatafileTemplateConfig;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.PaulException;
import au.edu.uq.cmm.paul.status.Facility;
import au.edu.uq.cmm.paul.status.FacilityStatus;
import au.edu.uq.cmm.paul.status.FacilityStatusManager;
import au.edu.uq.cmm.paul.status.FacilityStatusManager.Status;
import au.edu.uq.cmm.paul.watcher.FileWatcherEvent;
import au.edu.uq.cmm.paul.watcher.FileWatcherEventListener;

/**
 * A FileGrabber service is registered as a listener for FileWatcher events
 * for a particular Facility.  The first event for a file generates WorkEntry 
 * object that is enqueued.  Subsequent events are added to the WorkEntry.
 * <p>
 * The FileGrabber's thread pulls WorkEntry objects from the queue and processed 
 * them as follows:
 * <ul>
 * <li>It waits until the file events stop arriving.</li>
 * <li>It optionally locks the file.</li>
 * <li>It captures the user / account from the ACLS status manager.</li>
 * <li>It copies the file to another directory.</li>
 * <li>It records the file's administrative metadata.</li>
 * <li>It releases the lock.</li>
 * </ul>
 * <p>
 * The functionality is mostly implemented in the {@link WorkEntry} class.
 * 
 * @author scrawley
 */
public class FileGrabber extends CompositeServiceBase 
        implements FileWatcherEventListener {
    static final Logger LOG = LoggerFactory.getLogger(FileGrabber.class);
    static final int DEFAULT_FILE_SETTLING_TIME = 2000;  // 2 seconds
    
    private final PausableQueue<Runnable> work = new PausableQueue<Runnable>();
    private final HashMap<File, WorkEntry> workMap = new HashMap<File, WorkEntry>();
    private final FacilityStatusManager statusManager;
    private final Facility facility;
    private File safeDirectory;
    private ThreadPoolExecutor executor;
    private final EntityManagerFactory entityManagerFactory;
    private final Paul services;
    
    public FileGrabber(Paul services, Facility facility) {
        this.services = services;
        this.facility = facility;
        statusManager = services.getFacilityStatusManager();
        FacilityStatus status = statusManager.getStatus(facility);
        status.setFileGrabber(this);
        safeDirectory = new File(
                services.getConfiguration().getCaptureDirectory());
        if (!safeDirectory.exists() || !safeDirectory.isDirectory()) {
            throw new PaulException(
                    "The grabber's safe directory doesn't exist: " + 
                            safeDirectory);
        }
        entityManagerFactory = services.getEntityManagerFactory();
    }

    public File getSafeDirectory() {
        return safeDirectory;
    }

    public FacilityStatusManager getStatusManager() {
        return statusManager;
    }

    public synchronized void remove(File file) {
        workMap.remove(file);
    }

    @Override
    protected void doShutdown() throws InterruptedException {
        executor.shutdown();
        if (executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS)) {
            LOG.info("FileGrabber's executor shut down");
        } else {
            LOG.warn("FileGrabber's executor didn't shut down cleanly");
        }
    }

    @Override
    protected void doStartup() {
        FacilityStatus status = services.getFacilityStatusManager().getStatus(facility);
        Date catchupFrom = determineCatchupTime(facility);
        Date hwm = status.getGrabberHWMTimestamp();
        LOG.debug("Catchup from = " + catchupFrom + ", hwm = " + hwm);
        if (hwm != null && (catchupFrom == null || hwm.getTime() < catchupFrom.getTime())) {
            executor = new ThreadPoolExecutor(0, 1, 999, TimeUnit.SECONDS, work);
            // We do "catchup" event generation with the executor paused, so that the worker
            // thread doesn't jump the gun and start processing work entries before all events
            // have been accumulated.
            // Note: datasets grabbed in catchup will contain all files whose names match,
            // irrespective of the file timestamps.  This is the best we can do in the circumstances.
            work.pause();
            LOG.info("Commencing catchup treewalk for " + status.getLocalDirectory());
            int count = doCatchup(status.getLocalDirectory(), catchupFrom.getTime());
            LOG.info("Catchup treewalk found " + count + " files");
            // This ensures that the "caught-up" datasets get ingested in roughly the order
            // that the original files were saved rather than a seemingly random order, for
            // a better Mirage user experience ...
            reorderWorkQueue();
            LOG.info("Resuming the worker thread");
            work.resume();
        } else {
            status.setStatus(Status.OFF);
            status.setMessage("Grabber HWM needs attention");
            throw new ServiceException(status.getMessage());
        }
    }
    
    private void reorderWorkQueue() {
        LOG.info("Reordering the FileGrabber work queue (contains " + 
                work.size() + " potential datasets)");
        List<Runnable> workList = new ArrayList<Runnable>(work.size());
        work.drainTo(workList);
        Collections.sort(workList, new Comparator<Runnable>() {
            @Override
            public int compare(Runnable o1, Runnable o2) {
                WorkEntry w1 = (WorkEntry) o1;
                WorkEntry w2 = (WorkEntry) o2;
                return Long.compare(w1.getLatestFileTimestamp(), w2.getLatestFileTimestamp());
            }
        });
        for (Runnable r : workList) {
            WorkEntry w = (WorkEntry) r;
            LOG.debug("Entry for " + w.getBaseFile() + 
                    " has latest file stamp " + w.getLatestFileTimestamp());
        }
        work.addAll(workList);
    }

    private Date determineCatchupTime(Facility facility) {
        EntityManager em = entityManagerFactory.createEntityManager();
        Date res;
        try {
            TypedQuery<Date> query = em.createQuery(
                    "SELECT MAX(d.captureTimestamp) FROM DatasetMetadata d " +
                    "GROUP BY d.facilityId HAVING d.facilityId = :id", 
                    Date.class);
            query.setParameter("id", facility.getId());
            res = query.getSingleResult();
        } catch (NoResultException ex) {
            res = null;
        } finally {
            em.close();
        }
        LOG.info("determineCatchupTime(" + facility.getFacilityName() + ") -> " + res);
        return res;
    }

    private int doCatchup(File directory, long after) {
        int count = 0;
        for (File member : directory.listFiles()) {
            long lastModified;
            if (member.isDirectory()) {
                count += doCatchup(member, after);
            } else if (member.isFile() && 
                    (lastModified = member.lastModified()) > after) {
                FileWatcherEvent event = new FileWatcherEvent(
                        facility, member, true, lastModified, true);
                processEvent(event);
                count++;
            }
        }
        return count;
    }

    @Override
    public void eventOccurred(FileWatcherEvent event) {
        processEvent(event);
    }

    private void processEvent(FileWatcherEvent event) {
        File file = event.getFile();
        Facility facility = (Facility) event.getFacility();
        LOG.debug("FileWatcherEvent received : " + 
                facility.getFacilityName() + "," + file + "," + event.isCreate());
        File baseFile = null;
        for (DatafileTemplateConfig datafile : facility.getDatafileTemplates()) {
            Pattern pattern = Pattern.compile(datafile.getFilePattern());
            Matcher matcher = pattern.matcher(file.getAbsolutePath());
            if (matcher.matches()) {
                baseFile = new File(matcher.group(1));
                break;
            }
        }
        synchronized (this) {
           if (baseFile == null) {
               // If we are shutting down, we only deal with events for
               // files in datasets we've already started grabbing.
               if (getState() != Service.State.STARTED) {
                   return;
               }
           }
           WorkEntry workEntry = workMap.get(baseFile);
           if (workEntry == null) {
               workEntry = new WorkEntry(services, event, baseFile);
               workMap.put(baseFile, workEntry);
               executor.execute(workEntry);
               LOG.debug("Added a workEntry");
           } else {
               workEntry.addEvent(event);
           }
        }
    }

    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}
