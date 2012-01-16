package au.edu.uq.cmm.paul.watcher;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import au.edu.uq.cmm.aclslib.server.FacilityConfig;
import au.edu.uq.cmm.aclslib.service.MonitoredThreadServiceBase;
import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.PaulConfiguration;
import au.edu.uq.cmm.paul.PaulException;

public class FileWatcher extends MonitoredThreadServiceBase {
    private static class WatcherEntry {
        private final Path dir;
        private final FacilityConfig facility;
        private final WatchKey key;
        private final WatcherEntry parent;
        private final Set<WatcherEntry> children;
        
        public WatcherEntry(WatchKey key, WatcherEntry parent, 
                Path dir, FacilityConfig facility) {
            super();
            this.dir = dir;
            this.facility = facility;
            this.key = key;
            this.parent = parent;
            this.children = new HashSet<WatcherEntry>();
            if (parent != null) {
                parent.children.add(this);
            }
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            return key.equals(((WatcherEntry) obj).key);
        }
    }
    
    private static final Logger LOG = Logger.getLogger(FileWatcher.class);
    
    private PaulConfiguration config;
    private Map<WatchKey, WatcherEntry> watchMap = 
            new HashMap<WatchKey, WatcherEntry>();
    private UncPathnameMapper uncNameMapper;
    private WatchService watcher;

    private List<FileWatcherEventListener> listeners = 
            new ArrayList<FileWatcherEventListener>();
    
    
    public FileWatcher(Paul services) 
            throws UnknownHostException {
        this.config = services.getConfiguration();
        this.uncNameMapper = services.getUncNameMapper();
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    @Override
    public void run() {
        watcher = null;
        try {
            configureWatcher();
            while (true) {
                WatchKey key = watcher.take();
                WatcherEntry entry = watchMap.get(key);
                if (entry != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        processWatchEvent(entry, event);
                    }
                }
                key.reset();
            }
        } catch (IOException ex) {
            throw new PaulException("Unexpected IO error", ex);
        } catch (InterruptedException ex) {
            LOG.debug("Interrupted ... we're done");
        } finally {
            if (watcher != null) {
                try {
                    watcher.close();
                } catch (IOException ex) {
                    LOG.debug("Exception in watcher close", ex);
                }
            }
        }
    }

    private void processWatchEvent(WatcherEntry entry, WatchEvent<?> event) 
            throws IOException {
        Kind<?> kind = event.kind();
        if (kind == StandardWatchEventKinds.OVERFLOW) {
            LOG.error("Event overflow!");
            return;
        } 
        WatchEvent<Path> ev = cast(event);
        Path path = entry.dir.resolve(ev.context());
        File file = path.toFile();
        LOG.debug("Event for facility " + entry.facility.getFacilityId());
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
            LOG.debug("Created - " + path);
            if (file.isDirectory()) {
                addKeys(entry.facility, file, entry);
            } else {
                notifyEvent(entry.facility, file, true);
            }
        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            LOG.debug("Modified - " + path);
            if (!file.isDirectory()) {
                notifyEvent(entry.facility, file, false);
            }
        } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
            LOG.debug("Deleted - " + path);
            removeKeyForPath(entry, path, false);
        }
    }
    
    private void notifyEvent(FacilityConfig facility, File file, boolean create) {
        long now = System.currentTimeMillis();
        for (FileWatcherEventListener listener : listeners) {
            listener.eventOccurred(new FileWatcherEvent(facility, file, create, now));
        }
    }

    private void configureWatcher() throws IOException {
        FileSystem fs = FileSystems.getDefault();
        watcher = fs.newWatchService();
        for (FacilityConfig facility : config.getFacilities()) {
            if (facility.getDriveName() == null) {
                continue;
            }
            String name = facility.getFolderName();
            File local = uncNameMapper.mapUncPathname(name);
            if (local == null) {
                LOG.info("Facility folder name '" + name + "' does not map to a local path");
                continue;
            }
            if (!local.exists()) {
                LOG.info("Facility folder name '" + name + 
                        "' maps to non-existent local path '" + local + "'");
                continue;
            }
            addKeys(facility, local, null);
        }
    }

    private void addKeys(FacilityConfig facility, File local, WatcherEntry parent) 
            throws IOException {
        // If a directory is created while we are recursively adding
        // watcher keys, we may possibly miss it.  However, I think 
        // that we should get an event for the creation ... which would
        // allow us to add the key in the event processing code.
        Path dir = Paths.get(local.toURI());
        WatchKey key = dir.register(watcher, 
                StandardWatchEventKinds.ENTRY_CREATE, 
                StandardWatchEventKinds.ENTRY_MODIFY, 
                StandardWatchEventKinds.ENTRY_DELETE, 
                StandardWatchEventKinds.OVERFLOW);
        LOG.debug("Added directory watcher for " + local + 
                " for facility " + facility.getFacilityId());
        WatcherEntry entry = new WatcherEntry(key, parent, dir, facility);
        watchMap.put(key, entry);
        // Recursively add keys for nested directories.
        for (File child : local.listFiles()) {
            if (child.isDirectory()) {
                addKeys(facility, child, entry);
            }
        }
    }

    private void removeKeyForPath(WatcherEntry entry, Path path, boolean force) {
        // Potentially inefficient ... if directory corresponding to 'entry'
        // has many subdirectories.
        for (WatcherEntry child : entry.children) {
            if (child.dir.equals(path)) {
                removeKey(child, force);
                break;
            }
        }
    }
    
    private void removeKey(WatcherEntry entry, boolean force) {
        // (We don't want to cancel the watch key for a facility's
        // folder just because it has disappeared.  I don't think 
        // we'll get an event for that ... but this is just in case.)
        if (entry.parent != null || force) {
            watchMap.remove(entry.key);
            entry.key.cancel();
            LOG.debug("Cancelled directory watcher for " + entry.dir + 
                    " for facility " + entry.facility.getFacilityId());
            if (entry.parent != null) {
                entry.parent.children.remove(entry);
            }
        }
        for (WatcherEntry child : entry.children) {
            removeKey(child, true);
        }
    }

    public void addListener(FileWatcherEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(FileWatcherEventListener listener) {
        listeners.remove(listener);
    }
}
