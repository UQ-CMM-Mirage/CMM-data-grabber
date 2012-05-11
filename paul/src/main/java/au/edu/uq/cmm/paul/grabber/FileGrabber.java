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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
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
import au.edu.uq.cmm.paul.DatafileTemplateConfig;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.PaulException;
import au.edu.uq.cmm.paul.status.Facility;
import au.edu.uq.cmm.paul.status.FacilityStatusManager;
import au.edu.uq.cmm.paul.status.FacilityStatusManager.FacilityStatus;
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
    
    private final BlockingQueue<Runnable> work = new LinkedBlockingDeque<Runnable>();
    private final HashMap<File, WorkEntry> workMap = new HashMap<File, WorkEntry>();
    private final FacilityStatusManager statusManager;
    private final Facility facility;
    private File safeDirectory;
    private ExecutorService executor;
    private final EntityManagerFactory entityManagerFactory;
    private final Paul services;
    private boolean hold;
    private List<FileWatcherEvent> heldEvents;
    
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
        synchronized (this) {
            hold = true;
            heldEvents = new ArrayList<FileWatcherEvent>();
        }
        executor = new ThreadPoolExecutor(0, 1, 999, TimeUnit.SECONDS, work);
        FacilityStatus status = services.getFacilityStatusManager().getStatus(facility);
        doCatchup(status.getLocalDirectory(), determineCatchupTime(facility));
        List<FileWatcherEvent> tmp;
        synchronized (this) {
            hold = false;
            tmp = heldEvents;
            heldEvents = null;
        }
        for (FileWatcherEvent event : tmp) {
            processEvent(event);
        }
    }
    
    private long determineCatchupTime(Facility facility) {
        EntityManager em = entityManagerFactory.createEntityManager();
        long res;
        try {
            TypedQuery<Date> query = em.createQuery(
                    "SELECT MAX(d.captureTimestamp) FROM DatasetMetadata d " +
                    "GROUP BY d.facilityId HAVING d.facilityId = :id", 
                    Date.class);
            query.setParameter("id", facility.getId());
            res = query.getSingleResult().getTime();
        } catch (NoResultException ex) {
            res = 0L;
        } finally {
            em.close();
        }
        LOG.info("determineCatchupTime(" + facility.getFacilityName() + ") -> " + res);
        return res;
    }

    private void doCatchup(File directory, long after) {
        for (File member : directory.listFiles()) {
            long lastModified;
            if (member.isDirectory()) {
                doCatchup(member, after);
            } else if (member.isFile() && 
                    (lastModified = member.lastModified()) > after) {
                FileWatcherEvent event = new FileWatcherEvent(
                        facility, member, true, lastModified);
                processEvent(event);
            }
        }
    }

    @Override
    public void eventOccurred(FileWatcherEvent event) {
        synchronized (this) {
            if (hold) {
                if (heldEvents != null) {
                    heldEvents.add(event);
                }
                return;
            }
        }
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
