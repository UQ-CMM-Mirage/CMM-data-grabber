package au.edu.uq.cmm.paul.grabber;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import au.edu.uq.cmm.aclslib.server.Facility;
import au.edu.uq.cmm.aclslib.service.ThreadServiceBase;
import au.edu.uq.cmm.paul.watcher.FileWatcher;
import au.edu.uq.cmm.paul.watcher.FileWatcherEvent;
import au.edu.uq.cmm.paul.watcher.FileWatcherEventListener;

public class FileGrabber extends ThreadServiceBase implements FileWatcherEventListener {
    private static final Logger LOG = Logger.getLogger(FileGrabber.class);
    private static final int DEFAULT_FILE_SETTLING_TIME = 2000;  // 2 seconds
    
    private static class WorkEntry {
        private final BlockingDeque<FileWatcherEvent> events;
        private final File file;
        private final Facility facility;
        private final long timestamp;
        
        public WorkEntry(FileWatcherEvent event, File file, 
                Facility facility, long timestamp) {
            this.file = file;
            this.events = new LinkedBlockingDeque<FileWatcherEvent>();
            this.events.add(event);
            this.timestamp = timestamp;
            this.facility = facility;
        }

        public void addEvent(FileWatcherEvent event) {
            events.add(event);
        }
    }
    
    private final BlockingDeque<WorkEntry> work = new LinkedBlockingDeque<WorkEntry>();
    private final HashMap<File, WorkEntry> workMap = new HashMap<File, WorkEntry>();
    
    public FileGrabber(FileWatcher watcher) {
        watcher.addListener(this);
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
               workEntry = new WorkEntry(
                       event, file, event.getFacility(), event.getTimestamp());
               workMap.put(file, workEntry);
               work.add(workEntry);
               LOG.debug("Added a workEntry");
           } else {
               workEntry.addEvent(event);
           }
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                WorkEntry workEntry = work.takeFirst();
                LOG.debug("Got a workEntry");
                grabFile(workEntry); 
                synchronized (this) {
                    // FIXME - temporary hack
                    workEntry.events.clear();
                    workMap.remove(workEntry.file);
                }
            }
        } catch (InterruptedException ex) {
            // We're out of here ...
        }
    }

    private void grabFile(WorkEntry workEntry) throws InterruptedException {
        int settling = workEntry.facility.getFileSettlingTime();
        if (settling <= 0) {
            settling = DEFAULT_FILE_SETTLING_TIME;
        }
        // Wait until the file modification events stop arriving ... plus
        // the settling time.
        while (workEntry.events.poll(settling, TimeUnit.MILLISECONDS) != null) {
            LOG.debug("poll");
        }
        // Optionally lock the file, then grab it.
        try (FileInputStream is = new FileInputStream(workEntry.file)) {
            if (workEntry.facility.isUseFileLocks()) {
                LOG.debug("acquiring lock on " + workEntry.file);
                try (FileLock lock = is.getChannel().lock(0, Long.MAX_VALUE, true)) {
                    LOG.debug("locked " + workEntry.file);
                    doGrabFile(workEntry, is);
                }
                LOG.debug("unlocked " + workEntry.file);
            } else {
                doGrabFile(workEntry, is);
            }
        } catch (IOException ex) {
            LOG.error("Unexpected IO Error", ex);
        }
    }

    private void doGrabFile(WorkEntry workEntry, FileInputStream is) 
            throws InterruptedException {
        LOG.debug("Grabbing");
        Thread.sleep(10000);
        LOG.debug("Grabbed");
    }

}
