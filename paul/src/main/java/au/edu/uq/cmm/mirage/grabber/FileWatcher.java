package au.edu.uq.cmm.mirage.grabber;

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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import au.edu.uq.cmm.aclslib.server.Configuration;
import au.edu.uq.cmm.aclslib.server.Facility;
import au.edu.uq.cmm.aclslib.service.MonitoredThreadServiceBase;
import au.edu.uq.cmm.aclslib.service.ThreadServiceBase;

public class FileWatcher extends ThreadServiceBase {
    private static class WatcherEntry {
        private final Path dir;
        private final Facility facility;
        
        public WatcherEntry(Path dir, Facility facility) {
            super();
            this.dir = dir;
            this.facility = facility;
        }
    }
    
    private static final Logger LOG = Logger.getLogger(FileWatcher.class);
    
    private Configuration config;
    private Map<WatchKey, WatcherEntry> watchMap = new HashMap<WatchKey, WatcherEntry>();
    private UncPathnameMapper uncNameMapper;
    private WatchService watcher;
    
    public FileWatcher(Configuration config, UncPathnameMapper uncNameMapper) 
            throws UnknownHostException {
        this.config = config;
        this.uncNameMapper = uncNameMapper;
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
            throw new GrabberException("Unexpected IO error", ex);
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
        LOG.debug("Event for facility " + entry.facility.getFacilityId());
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
            LOG.debug("Created - " + path);
            File file = path.toFile();
            if (file.isDirectory()) {
                addKeys(entry.facility, file);
            }
        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            LOG.debug("Modified - " + path);
        } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
            LOG.debug("Deleted - " + path);
        }
    }

    private void configureWatcher() throws IOException {
        FileSystem fs = FileSystems.getDefault();
        watcher = fs.newWatchService();
        for (Facility facility : config.getFacilities().values()) {
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
            addKeys(facility, local);
        }
    }

    private void addKeys(Facility facility, File local) 
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
                " for facility " + facility);
        watchMap.put(key, new WatcherEntry(dir, facility));
        // Recursively add keys for nested directories.
        for (File child : local.listFiles()) {
            if (child.isDirectory()) {
                addKeys(facility, child);
            }
        }
    }
}
