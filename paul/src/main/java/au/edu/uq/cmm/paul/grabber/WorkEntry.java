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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.eccles.FacilitySession;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.PaulException;
import au.edu.uq.cmm.paul.queue.QueueManager;
import au.edu.uq.cmm.paul.status.DatafileTemplate;
import au.edu.uq.cmm.paul.status.Facility;
import au.edu.uq.cmm.paul.status.FacilityStatusManager;
import au.edu.uq.cmm.paul.status.FacilityStatusManager.FacilityStatus;
import au.edu.uq.cmm.paul.watcher.FileWatcherEvent;

/**
 * This class represents a unit of work for the {@link FileGrabber} executor;
 * i.e. a dataset to be "grabbed".  This class does most of the work of grabbing
 * and creation of the queue entries.
 * 
 * @author scrawley
 */
class WorkEntry implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(WorkEntry.class);
    private static final long DEFAULT_GRABBER_TIMEOUT = 600000; // milliseconds == 10 minutes
    
    private final long grabberTimeout;

    private final FileGrabber fileGrabber;
    private final QueueManager queueManager;
    private final FacilityStatusManager statusManager;
    private final BlockingDeque<FileWatcherEvent> events;
    private final File baseFile;
    private final String instrumentBasePath;
    private final Map<File, GrabbedFile> files;
    private final Facility facility;
    private final Date timestamp;
    private long latestFileTimestamp = 0L;
    private final boolean holdDatasetsWithNoUser;
    private final boolean catchup;
    
    
    public WorkEntry(Paul services, FileWatcherEvent event, File baseFile) {
        this.facility = (Facility) event.getFacility();
        this.timestamp = new Date(event.getTimestamp());
        this.statusManager = services.getFacilityStatusManager();
        this.fileGrabber = statusManager.getStatus(facility).getFileGrabber();
        this.queueManager = services.getQueueManager();
        this.baseFile = baseFile;
        this.instrumentBasePath = mapToInstrumentPath(facility, baseFile);
        this.files = new ConcurrentHashMap<File, GrabbedFile>();
        this.events = new LinkedBlockingDeque<FileWatcherEvent>();
        this.holdDatasetsWithNoUser = 
                services.getConfiguration().isHoldDatasetsWithNoUser();
        long timeout = services.getConfiguration().getGrabberTimeout();
        this.grabberTimeout = timeout == 0 ? DEFAULT_GRABBER_TIMEOUT : timeout;
        this.catchup = event.isCatchup();
        addEvent(event);
    }
    
    private String mapToInstrumentPath(Facility facility, File file) {
        String filePath = file.getAbsolutePath();
        FacilityStatus status = statusManager.getStatus(facility);
        String directoryPath = status.getLocalDirectory().getAbsolutePath();
        if (!filePath.startsWith(directoryPath)) {
            throw new PaulException("Bad path base: '" + filePath +
                    "' does not start with '" + directoryPath);
        }
        // This is a hack, but I can't use `File` to generate a Windows-style
        // pathname on Unix / Linux.
        filePath = filePath.substring(directoryPath.length());
        filePath = filePath.replaceAll("/", "\\\\");
        return facility.getDriveName() + ":" + filePath;
    }

    public void addEvent(FileWatcherEvent event) {
        events.add(event);
        // FIXME - events that arrive too late possibly won't be grabbed
        // because they aren't guaranteed to be in the set that the
        // grabFiles method is iterating.
        File file = event.getFile();
        LOG.debug("Processing event for file " + file);
        boolean matched = false;
        List<DatafileTemplate> templates = facility.getDatafileTemplates();
        if (templates.isEmpty()) {
            if (!files.containsKey(file)) {
                files.put(file, new GrabbedFile(file, file, null));
                LOG.debug("Added file " + file + " to map for grabbing");
            } else {
                LOG.debug("File " + file + " already in map for grabbing");
            }
            updateLatestFileTimestamp(file);
        } else {
            for (DatafileTemplate template : templates) {
                Pattern pattern = template.getCompiledFilePattern(
                        facility.isCaseInsensitive());
                Matcher matcher = pattern.matcher(file.getAbsolutePath());
                if (matcher.matches()) {
                    if (!files.containsKey(file)) {
                        files.put(file, new GrabbedFile(
                                new File(matcher.group(1)), file, template));
                        LOG.debug("Added file " + file + " to map for grabbing");
                    } else {
                        LOG.debug("File " + file + " already in map for grabbing");
                    }
                    updateLatestFileTimestamp(file);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                LOG.debug("File " + file + " didn't match any template - ignoring");
            }
        }
    }

    private void updateLatestFileTimestamp(File file) {
        long timestamp = file.lastModified();
        if (timestamp > latestFileTimestamp) {
            latestFileTimestamp = timestamp;
        }
    }

    public long getLatestFileTimestamp() {
        return latestFileTimestamp;
    }

    public File getBaseFile() {
        return baseFile;
    }

    @Override
    public void run() {
        LOG.debug("Processing workEntry for " + baseFile);
        try {
            grabFiles();
        } catch (InterruptedException ex) {
            LOG.debug("interrupted");
        } catch (RuntimeException ex) {
            LOG.error("unexpected exception", ex);
            throw ex;
        } catch (Error ex) {
            LOG.error("unexpected error", ex);
            throw ex;
        }
        synchronized (fileGrabber) {
            fileGrabber.remove(baseFile);
        }
        LOG.debug("Finished processing workEntry for " + baseFile);
    }

    private void grabFiles() throws InterruptedException {
        if (!datasetCompleted()) {
            return;
        }
        // Prepare for grabbing
        Date now = new Date();
        FacilitySession session = fileGrabber.getStatusManager().
                getLoginDetails(facility.getFacilityName(), timestamp.getTime());
        // Optionally lock the files, then grab them.
        // FIXME - note that we may not see all of the files ... see above.
        for (GrabbedFile file : files.values()) {
            try (FileInputStream is = new FileInputStream(file.getFile())) {
                if (facility.isUseFileLocks()) {
                    LOG.debug("acquiring lock on " + file);
                    try (FileLock lock = is.getChannel().lock(0, Long.MAX_VALUE, true)) {
                        LOG.debug("locked " + file);
                        doGrabFile(file, is);
                    }
                    LOG.debug("unlocked " + file);
                } else {
                    doGrabFile(file, is);
                }
            } catch (IOException ex) {
                LOG.error("Unexpected IO Error", ex);
            }
        }
        try {
            saveMetadata(now, session);
        } catch (JsonGenerationException ex) {
            LOG.error("Unexpected JSON Error", ex);
        } catch (IOException ex) {
            LOG.error("Unexpected IO Error", ex);
        }
    }

    private boolean datasetCompleted() throws InterruptedException {
        // Wait until the dataset is completed.
        boolean incomplete = true;
        long limit = grabberTimeout < 0 ? Long.MAX_VALUE :
            System.currentTimeMillis() + grabberTimeout;
        do {
            if (!catchup) {
                int settling = facility.getFileSettlingTime();
                if (settling <= 0) {
                    settling = FileGrabber.DEFAULT_FILE_SETTLING_TIME;
                }
                // Wait for the file modification events stop arriving ... plus
                // the settling time.
                while (events.poll(settling, TimeUnit.MILLISECONDS) != null) {
                    LOG.debug("poll");
                }
            }
            // Check that the dataset's non-optional files are all present,
            // and that they meet the minimum size requirements.
            incomplete = false;
            for (DatafileTemplate template : facility.getDatafileTemplates()) {
                if (template.isOptional()) {
                    continue;
                }
                Pattern pattern = template.getCompiledFilePattern(
                        facility.isCaseInsensitive());
                boolean satisfied = false;
                for (File file : files.keySet()) {
                    Matcher matcher = pattern.matcher(file.getAbsolutePath());
                    if (matcher.matches()) {
                        if (file.length() >= template.getMinimumSize()) {
                            satisfied = true;
                        } else {
                            LOG.debug("Datafile " + file + " isn't big enough yet");
                        }
                        break;
                    }
                }
                if (!satisfied) {
                    LOG.debug("Datafile for template " + template.getFilePattern() + " isn't ready");
                    incomplete = true;
                    break;
                }
            } 
        } while (incomplete && !catchup && limit > System.currentTimeMillis());
        if (incomplete) {
            LOG.info("Dataset for baseFile " + baseFile + 
                    (catchup ? " is incomplete" : " did not complete within timeout") +
                    ".  Dropping it.");
            return false;
        }
        
        // Prune any files that don't meet their minimum size requirement.
        for (Iterator<Entry<File, GrabbedFile>> it = files.entrySet().iterator();
                it.hasNext(); /* */) {
            Entry<File, GrabbedFile> entry = it.next();
            long length = entry.getKey().length();
            if (length < entry.getValue().getTemplate().getMinimumSize()) {
                LOG.info("Dropping datafile " + entry.getKey() + " with size " + length);
                it.remove();
            }
        }
        
        // Avoid creating an empty Dataset (containing just an admin metadata file)
        if (files.isEmpty()) {
            LOG.info("Dropping empty dataset for baseFile " + baseFile);
            return false;
        }
        return true;
    }

    private void doGrabFile(GrabbedFile file, FileInputStream is) 
            throws InterruptedException, IOException {
        LOG.debug("Start file grabbing");
        Date now = new Date();
        Date fileTimestamp = new Date(file.getFile().lastModified());
        String suffix = (file.getTemplate() == null) ?
                ".data" : file.getTemplate().getSuffix();
        File copiedFile = copyFile(is, file.getFile(), suffix);
        file.setCopiedFile(copiedFile);
        file.setFileTimestamp(fileTimestamp);
        file.setCopyTimestamp(now);
        LOG.debug("Done grabbing");
    }

    private void saveMetadata(Date now,FacilitySession session)
            throws IOException, JsonGenerationException {
        if (session == null && !holdDatasetsWithNoUser) {
            session = FacilitySession.makeDummySession(facility.getFacilityName(), now);
        }
        String userName = session == null ? null : session.getUserName();
        String account = session == null ? null : session.getAccount();
        String sessionUuid = session == null ? null : session.getSessionUuid();
        String emailAddress = session == null ? null : session.getEmailAddress();
        Date loginTime = session == null ? null : session.getLoginTime();
        File metadataFile = generateUniqueFile(".admin");
        List<DatafileMetadata> list = new ArrayList<DatafileMetadata>(files.size());
        for (GrabbedFile g : files.values()) {
            String mimeType = (g.getTemplate() == null) ? 
                    "application/octet-stream" : g.getTemplate().getMimeType();
            DatafileMetadata d = new DatafileMetadata(
                    g.getFile().getAbsolutePath(), 
                    mapToInstrumentPath(facility, g.getFile()),
                    g.getCopiedFile().getAbsolutePath(), 
                    g.getFileTimestamp(), g.getCopyTimestamp(), mimeType,
                    g.getCopiedFile().length(), null);
            d.updateDatafileHash();
            list.add(d);
        }
        DatasetMetadata metadata = new DatasetMetadata(baseFile.getAbsolutePath(), 
                instrumentBasePath, metadataFile.getAbsolutePath(), 
                userName, facility.getFacilityName(), facility.getId(), 
                account, emailAddress, now, sessionUuid, loginTime, list);
        metadata.updateDatasetHash();
        queueManager.addEntry(metadata, metadataFile);
    }

    private File copyFile(FileInputStream is, File source, String suffix) 
            throws IOException {
        // TODO - if the time taken to copy files is a problem, we could 
        // potentially improve this by using NIO or memory mapped files.
        File target = generateUniqueFile(suffix);
        long size = source.length();
        try (FileOutputStream os = new FileOutputStream(target)) {
            byte[] buffer = new byte[(int) Math.min(size, 8192)];
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