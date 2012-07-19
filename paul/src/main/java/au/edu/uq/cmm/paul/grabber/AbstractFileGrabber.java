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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.paul.DatafileTemplateConfig;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.status.Facility;
import au.edu.uq.cmm.paul.watcher.FileWatcherEvent;
import au.edu.uq.cmm.paul.watcher.FileWatcherEventListener;

/**
 * This is the base class for the FileGrabber service, and the special grabber
 * that we use when figuring out what could potentially be grabbed.
 * 
 * @author scrawley
 */
public abstract class AbstractFileGrabber implements FileWatcherEventListener {
    static final Logger LOG = LoggerFactory.getLogger(AbstractFileGrabber.class);
    static final int DEFAULT_FILE_SETTLING_TIME = 2000;  // 2 seconds
    
    private final HashMap<File, WorkEntry> workMap = new HashMap<File, WorkEntry>();
    private final Facility facility;
    private final Paul services;
    private volatile boolean shuttingDown;
    
    public AbstractFileGrabber(Paul services, Facility facility) {
        this.services = services;
        this.facility = facility;
    }

    public final synchronized void remove(File file) {
        workMap.remove(file);
    }

    public final synchronized void reorderQueue(BlockingQueue<Runnable> queue) {
        LOG.info("Reordering a FileGrabber work queue (contains " + 
                queue.size() + " potential datasets)");
        List<Runnable> workList = new ArrayList<Runnable>(queue.size());
        queue.drainTo(workList);
        Collections.sort(workList, new Comparator<Runnable>() {
            @Override
            public int compare(Runnable o1, Runnable o2) {
                WorkEntry w1 = (WorkEntry) o1;
                WorkEntry w2 = (WorkEntry) o2;
                return Long.compare(w1.getLatestFileTimestamp(), w2.getLatestFileTimestamp());
            }
        });
        queue.addAll(workList);
    }

    public final synchronized int analyseTree(File directory, long after, long before) {
        int count = 0;
        for (File member : directory.listFiles()) {
            long lastModified;
            if (member.isDirectory()) {
                count += analyseTree(member, after, before);
            } else if (member.isFile() && 
                    (lastModified = member.lastModified()) > after &&
                    lastModified < before) {
                FileWatcherEvent event = new FileWatcherEvent(
                        facility, member, true, lastModified, true);
                processEvent(event);
                count++;
            }
        }
        return count;
    }

    @Override
    public final void eventOccurred(FileWatcherEvent event) {
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
        if (baseFile != null) {
            synchronized (this) {
                // If we are shutting down, we only deal with events for
                // files in datasets we've already started grabbing.
                if (!shuttingDown) {
                    WorkEntry workEntry = workMap.get(baseFile);
                    if (workEntry == null) {
                        workEntry = new WorkEntry(services, event, baseFile);
                        workMap.put(baseFile, workEntry);
                        enqueueWorkEntry(workEntry);
                        LOG.debug("Added a workEntry");
                    } else {
                        workEntry.addEvent(event);
                    }
                }
            }
        } else {
            LOG.debug("FileWatcherEvent doesn't match any template : " + 
                facility.getFacilityName() + "," + file + "," + event.isCreate());
        }
    }
    
    protected final boolean isShuttingDown() {
        return shuttingDown;
    }

    protected final void setShuttingDown(boolean shuttingDown) {
        this.shuttingDown = shuttingDown;
    }

    protected abstract void enqueueWorkEntry(WorkEntry entry);

    protected final Facility getFacility() {
        return facility;
    }
    
    protected final Paul getServices() {
        return services;
    }
}
