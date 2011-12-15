package au.edu.uq.cmm.paul.grabber;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import au.edu.uq.cmm.aclslib.server.Facility;
import au.edu.uq.cmm.aclslib.service.MonitoredThreadServiceBase;
import au.edu.uq.cmm.paul.PaulException;
import au.edu.uq.cmm.paul.status.FacilityStatusManager;
import au.edu.uq.cmm.paul.status.FacilitySession;
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
 * 
 * @author scrawley
 */
public class FileGrabber extends MonitoredThreadServiceBase 
        implements FileWatcherEventListener {
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
    private final FacilityStatusManager statusManager;
    private File safeDirectory = new File("/tmp/safe");
    
    public FileGrabber(FileWatcher watcher, FacilityStatusManager statusManager) {
        watcher.addListener(this);
        this.statusManager = statusManager;
        if (!safeDirectory.exists() || !safeDirectory.isDirectory()) {
            throw new PaulException("The grabber's safe directory doesn't exist");
        }
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
            throws InterruptedException, IOException {
        LOG.debug("State grabbing");
        Facility facility = workEntry.facility;
        long now = System.currentTimeMillis();
        FacilitySession login = statusManager.getLoginDetails(facility, workEntry.timestamp);
        String userName = login != null ? login.getUserName() : "unknown";
        String account = login != null ? login.getAccount() : "unknown";
        File copiedFile = copyFile(is, workEntry.file);
        File metadataFile = new File(copiedFile.getPath().replace(".data", ".admin"));
        AdminMetadata metadata = new AdminMetadata(
                workEntry.file.getAbsolutePath(), copiedFile.getAbsolutePath(),
                userName, workEntry.facility.getFacilityId(), 
                account, now, workEntry.timestamp);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(metadataFile))) {
            ObjectMapper mapper = new ObjectMapper();
            JsonFactory jf = new JsonFactory();
            JsonGenerator jg = jf.createJsonGenerator(bw);
            jg.useDefaultPrettyPrinter();
            mapper.writeValue(jg, metadata);
            LOG.info("Saved admin metadata to " + metadataFile);
        } catch (JsonParseException ex) {
            throw new PaulException(ex);
        } catch (JsonMappingException ex) {
            throw new PaulException(ex);
        }
        LOG.debug("Done grabbing");
    }

    private File copyFile(FileInputStream is, File source) throws IOException {
        // TODO - if the time taken to copy files is a problem, we could 
        // potentially improve this by using NIO or memory mapped files.
        File target = generateUniqueFile(".data");
        long size = source.length();
        try (FileOutputStream os = new FileOutputStream(target)) {
            byte[] buffer = new byte[(int)Math.min(size, 8192)];
            int nosRead;
            long totalRead = 0;
            while ((nosRead = is.read(buffer, 0, buffer.length)) > 0) {
                os.write(buffer, 0, nosRead);
                totalRead += nosRead;
            }
            if (totalRead != size) {
                // If this happen's there is something wrong with our locking
                // and / or file settling heuristics.
                LOG.error("Copied file size discrepancy - initial file size was " + size +
                        "bytes but we copied " + totalRead + " bytes");
                        
            }
            LOG.info("Copied " + totalRead + " bytes from " + source + " to " + target);
        }
        return target;
    }

    private File generateUniqueFile(String suffix) {
        long now = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            String name = String.format("file-%d-%d-%d%s", 
                    now, Thread.currentThread().getId(), i, suffix);
            File file = new File(safeDirectory, name);
            if (!file.exists()) {
                return file;
            }
        }
        throw new PaulException("Can't generate a unique filename!");
    }
}
