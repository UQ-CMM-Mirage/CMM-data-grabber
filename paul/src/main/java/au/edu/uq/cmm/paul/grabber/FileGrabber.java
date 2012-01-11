package au.edu.uq.cmm.paul.grabber;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;

import au.edu.uq.cmm.aclslib.service.CompositeServiceBase;
import au.edu.uq.cmm.paul.PaulException;
import au.edu.uq.cmm.paul.status.FacilityStatusManager;
import au.edu.uq.cmm.paul.watcher.FileWatcher;
import au.edu.uq.cmm.paul.watcher.FileWatcherEvent;
import au.edu.uq.cmm.paul.watcher.FileWatcherEventListener;

/**
 * The FileGrabber service is registered as a listener for FileWatcher events.
 * The first event for a file generates WorkEntry object that is enqueued.  
 * Subsequent events are added to the WorkEntry.
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
    static final Logger LOG = Logger.getLogger(FileGrabber.class);
    static final int DEFAULT_FILE_SETTLING_TIME = 2000;  // 2 seconds
    
    private final BlockingQueue<Runnable> work = new LinkedBlockingDeque<Runnable>();
    private final HashMap<File, WorkEntry> workMap = new HashMap<File, WorkEntry>();
    private final FacilityStatusManager statusManager;
    private File safeDirectory = new File("/tmp/safe");
    private ExecutorService executor;
    private EntityManagerFactory entityManagerFactory;
    
    public FileGrabber(EntityManagerFactory entityManagerFactory, 
            FileWatcher watcher, FacilityStatusManager statusManager) {
        watcher.addListener(this);
        this.statusManager = statusManager;
        if (!safeDirectory.exists() || !safeDirectory.isDirectory()) {
            throw new PaulException("The grabber's safe directory doesn't exist");
        }
        this.entityManagerFactory = entityManagerFactory;
    }

    public File getSafeDirectory() {
        return safeDirectory;
    }

    public FacilityStatusManager getStatusManager() {
        return statusManager;
    }

    public void remove(File file) {
        workMap.remove(file);
    }

    @Override
    protected void doShutdown() throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(20, TimeUnit.SECONDS)) {
            LOG.warn("FileGrabber's executor didn't shut down cleanly");
        }
    }

    @Override
    protected void doStartup() {
        executor = new ThreadPoolExecutor(5, 5, 999, TimeUnit.SECONDS, work);
    }

    @Override
    public void eventOccurred(FileWatcherEvent event) {
        File file = event.getFile();
        LOG.debug("FileWatcherEvent received : " + 
                event.getFacility().getFacilityId() + "," + 
                file + "," + event.isCreate());
        synchronized (this) {
           WorkEntry workEntry = workMap.get(file);
           if (workEntry == null) {
               workEntry = new WorkEntry(this, event, file);
               workMap.put(file, workEntry);
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
