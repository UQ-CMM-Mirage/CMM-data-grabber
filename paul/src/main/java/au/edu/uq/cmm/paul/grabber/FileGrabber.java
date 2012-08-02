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
import java.util.Date;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.aclslib.service.ServiceException;
import au.edu.uq.cmm.aclslib.service.SimpleService;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.PaulException;
import au.edu.uq.cmm.paul.queue.QueueManager;
import au.edu.uq.cmm.paul.queue.QueueManager.DateRange;
import au.edu.uq.cmm.paul.status.Facility;
import au.edu.uq.cmm.paul.status.FacilityStatus;
import au.edu.uq.cmm.paul.status.FacilityStatusManager;
import au.edu.uq.cmm.paul.status.FacilityStatusManager.Status;

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
public class FileGrabber extends AbstractFileGrabber implements SimpleService {
    static final Logger LOG = LoggerFactory.getLogger(FileGrabber.class);
    static final int DEFAULT_FILE_SETTLING_TIME = 2000;  // 2 seconds
    
    private final PausableQueue<Runnable> work = new PausableQueue<Runnable>();
    private final FacilityStatusManager statusManager;
    private File safeDirectory;
    private ThreadPoolExecutor executor;
    private final EntityManagerFactory entityManagerFactory;
    private final QueueManager queueManager;
    private final boolean testMode;
    
    public FileGrabber(Paul services, Facility facility) {
        this(services, facility, false);
    }

    public FileGrabber(Paul services, Facility facility, boolean testMode) {
        super(services, facility);
        this.testMode = testMode;
        statusManager = services.getFacilityStatusManager();
        queueManager = services.getQueueManager();
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
    
    @Override
    public void shutdown() throws InterruptedException {
        if (executor != null) {
            executor.shutdown();
            if (executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS)) {
                LOG.info("FileGrabber's executor shut down");
            } else {
                LOG.warn("FileGrabber's executor didn't shut down cleanly");
            }
            executor = null;
        }
    }

    @Override
    public void startup() {
        if (testMode) {
            // In the unit tests we just want to start the executor and ignore
            // the niceties of the HWM and catchup processing.
            createExecutor();
            return;
        }
        FacilityStatus status = statusManager.getStatus(getFacility());
        DateRange range = queueManager.getQueueDateRange(getFacility());
        Date lwm = status.getGrabberLWMTimestamp();
        Date hwm = status.getGrabberHWMTimestamp();
        LOG.debug("Catchup from = " + range + ", lwm = " + lwm + ", hwm = " + hwm);
        if (hwm == null) {
            hwm = lwm;
        }
        if (hwm != null && (range == null || hwm.getTime() <= range.getToDate().getTime())) {
            createExecutor();
            // We do "catchup" event generation with the executor paused, so that the worker
            // thread doesn't jump the gun and start processing work entries before all events
            // have been accumulated.
            // Note: datasets grabbed in catchup will contain all files whose names match,
            // irrespective of the file timestamps.  This is the best we can do in the circumstances.
            work.pause();
            long start = Math.max(hwm.getTime(), 
                    range == null ? Long.MIN_VALUE : range.getToDate().getTime());
            LOG.info("Commencing catchup treewalk for " + status.getLocalDirectory());
            int count = analyseTree(status.getLocalDirectory(), start, Long.MAX_VALUE);
            LOG.info("Catchup treewalk found " + count + " files");
            // This ensures that the "caught-up" datasets get ingested in roughly the order
            // that the original files were saved rather than a seemingly random order, for
            // a better Mirage user experience ...
            reorderQueue(work);
            LOG.info("Resuming the worker thread");
            work.resume();
        } else {
            status.setStatus(Status.OFF);
            status.setMessage("Grabber LWM / HWM need attention");
            throw new ServiceException(status.getMessage());
        }
    }

    private void createExecutor() {
        executor = new ThreadPoolExecutor(0, 1, 999, TimeUnit.SECONDS, work);
    }

    @Override
    protected boolean isShutDown() {
        return executor == null;
    }

    @Override
    protected void enqueueWorkEntry(WorkEntry entry) {
        if (executor == null) {
            LOG.info("Dropping work entry as there is currently no executor");
        } else {
            try {
                executor.execute(entry);
                LOG.debug("Enqueued work entry");
            } catch (RejectedExecutionException ex) {
                if (executor.isShutdown()) {
                    LOG.info("Dropping work entry as the executor is shut down");
                } else {
                    throw ex;
                }
            }
        }
    }

    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}
