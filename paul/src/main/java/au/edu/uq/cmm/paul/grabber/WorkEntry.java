package au.edu.uq.cmm.paul.grabber;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Date;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;

import au.edu.uq.cmm.aclslib.server.FacilityConfig;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.PaulException;
import au.edu.uq.cmm.paul.queue.QueueManager;
import au.edu.uq.cmm.paul.status.FacilitySession;
import au.edu.uq.cmm.paul.watcher.FileWatcherEvent;

/**
 * This class represents a unit of work for the {@link FileGrabber} executor;
 * i.e. a file to be "grabbed".
 * 
 * @author scrawley
 */
class WorkEntry implements Runnable {
    private static final Logger LOG = Logger.getLogger(WorkEntry.class);

    private final FileGrabber fileGrabber;
    private final QueueManager queueManager;
    private final BlockingDeque<FileWatcherEvent> events;
    private final File file;
    private final FacilityConfig facility;
    private final Date timestamp;
    
    public WorkEntry(Paul services, FileWatcherEvent event, File file) {
        this.facility = event.getFacility();
        this.timestamp = new Date(event.getTimestamp());
        this.fileGrabber = services.getFileGrabber();
        this.queueManager = services.getQueueManager();
        this.file = file;
        this.events = new LinkedBlockingDeque<FileWatcherEvent>();
        this.events.add(event);
    }

    public void addEvent(FileWatcherEvent event) {
        events.add(event);
    }

    @Override
    public void run() {
        FileGrabber.LOG.debug("Processing a workEntry");
        try {
            grabFile();
        } catch (InterruptedException ex) {
            FileGrabber.LOG.debug("interrupted");
        }
        synchronized (fileGrabber) {
            // FIXME - temporary hack
            this.events.clear();
            fileGrabber.remove(this.file);
        }
        FileGrabber.LOG.debug("Finished processing workEntry");
    }

    private void grabFile() throws InterruptedException {
        int settling = facility.getFileSettlingTime();
        if (settling <= 0) {
            settling = FileGrabber.DEFAULT_FILE_SETTLING_TIME;
        }
        // Wait until the file modification events stop arriving ... plus
        // the settling time.
        while (events.poll(settling, TimeUnit.MILLISECONDS) != null) {
            LOG.debug("poll");
        }
        // Optionally lock the file, then grab it.
        try (FileInputStream is = new FileInputStream(file)) {
            if (facility.isUseFileLocks()) {
                LOG.debug("acquiring lock on " + file);
                try (FileLock lock = is.getChannel().lock(0, Long.MAX_VALUE, true)) {
                    LOG.debug("locked " + file);
                    doGrabFile(is);
                }
                LOG.debug("unlocked " + file);
            } else {
                doGrabFile(is);
            }
        } catch (IOException ex) {
            LOG.error("Unexpected IO Error", ex);
        }
    }

    private void doGrabFile(FileInputStream is) 
            throws InterruptedException, IOException {
        LOG.debug("Start file grabbing");
        Date now = new Date();
        FacilitySession session = fileGrabber.getStatusManager().
                getLoginDetails(facility.getFacilityId(), timestamp.getTime());
        File copiedFile = copyFile(is, file);
        saveMetadata(now, session, copiedFile);
        LOG.debug("Done grabbing");
    }

    private void saveMetadata(Date now,
            FacilitySession session, File copiedFile)
            throws IOException, JsonGenerationException {
        String userName = session != null ? session.getUserName() : "unknown";
        String account = session != null ? session.getAccount() : "unknown";
        long sessionId = session != null ? session.getSessionId() : -1L;
        Date sessionTimestamp = session != null ? session.getLoginTime() : new Date(0L);
        File metadataFile = new File(copiedFile.getPath().replace(".data", ".admin"));
        AdminMetadata metadata = new AdminMetadata(
                file.getAbsolutePath(), copiedFile.getAbsolutePath(),
                userName, facility.getFacilityId(), 
                account, now, timestamp, sessionId, sessionTimestamp);
        queueManager.addEntry(metadata, metadataFile);
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
            File file = new File(fileGrabber.getSafeDirectory(), name);
            if (!file.exists()) {
                return file;
            }
        }
        throw new PaulException("Can't generate a unique filename!");
    }
}