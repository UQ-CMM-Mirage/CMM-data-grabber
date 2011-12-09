package au.edu.uq.cmm.mirage.grabber;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import au.edu.uq.cmm.aclslib.server.Configuration;
import au.edu.uq.cmm.aclslib.server.Facility;
import au.edu.uq.cmm.aclslib.service.MonitoredThreadServiceBase;

public class FileWatcher extends MonitoredThreadServiceBase {
    private static class WatcherEntry {
        private final Path dir;
        private final WatchKey key;
        private final Facility facility;
        
        public WatcherEntry(Path dir, WatchKey key, Facility facility) {
            super();
            this.dir = dir;
            this.key = key;
            this.facility = facility;
        }
    }
    
    private static final Logger LOG = Logger.getLogger(FileWatcher.class);
    private final Set<String> hostNames;
    
    private Configuration config;
    private Map<WatchKey, WatcherEntry> watchMap = new HashMap<WatchKey, WatcherEntry>();
    private Map<String, String> shareMap;
    
    public FileWatcher(Configuration config, Map<String, String> shareMap) 
            throws UnknownHostException {
        this.config = config;
        hostNames = new HashSet<String>();
        InetAddress host = InetAddress.getLocalHost();
        String hostAddr = host.getHostAddress();
        String hostName = host.getHostName();
        String canonicalHostName = host.getCanonicalHostName();
        hostNames.add(hostAddr);
        if (hostNames.add(hostName)) {
            int firstDot = hostName.indexOf(".");
            if (firstDot > 0) {
                hostNames.add(hostName.substring(0, firstDot));
            }
        }
        if (hostNames.add(canonicalHostName)) {
            int firstDot = canonicalHostName.indexOf(".");
            if (firstDot > 0) {
                hostNames.add(canonicalHostName.substring(0, firstDot));
            }
        }
        this.shareMap = shareMap;
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    @Override
    public void run() {
        WatchService watcher = null;
        try {
            watcher = configureWatcher();
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

    private void processWatchEvent(WatcherEntry entry, WatchEvent<?> event) {
        Kind<?> kind = event.kind();
        if (kind == StandardWatchEventKinds.OVERFLOW) {
            LOG.error("Event overflow!");
            return;
        } 
        WatchEvent<Path> ev = cast(event);
        Path file = entry.dir.resolve(ev.context());
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
            LOG.debug("Created - " + file);
        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            LOG.debug("Modified - " + file);
        } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
            LOG.debug("Deleted - " + file);
        }
    }

    private WatchService configureWatcher() throws IOException {
        FileSystem fs = FileSystems.getDefault();
        WatchService watcher = fs.newWatchService();
        for (Facility facility : config.getFacilities().values()) {
            if (facility.getDriveName() == null) {
                continue;
            }
            String name = facility.getFolderName();
            File local = mapUncPathToLocalPath(name);
            if (local == null) {
                LOG.info("Facility folder name '" + name + "' does not map to a local path");
                continue;
            }
            if (!local.exists()) {
                LOG.info("Facility folder name '" + name + 
                        "' maps to non-existent local path '" + local + "'");
                continue;
            }
            Path dir = Paths.get(local.toURI());
            WatchKey key = dir.register(watcher, 
                    StandardWatchEventKinds.ENTRY_CREATE, 
                    StandardWatchEventKinds.ENTRY_MODIFY, 
                    StandardWatchEventKinds.ENTRY_DELETE, 
                    StandardWatchEventKinds.OVERFLOW);
            watchMap.put(key, new WatcherEntry(dir, key, facility));
        }
        return watcher;
    }

    private File mapUncPathToLocalPath(final String uncPath) {
        String canonicalUncPath = uncPath.replace('\\', '/');
        Matcher matcher = Pattern.compile("//([^/]+)/([^/]+)/(.+)").matcher(canonicalUncPath);
        if (!matcher.matches()) {
            LOG.info("Invalid UNC path: '" + canonicalUncPath + "'");
            return null;
        }
        if (!hostNames.contains(matcher.group(1))) {
            LOG.info("UNC path '" + canonicalUncPath + "'s hostname is not us");
            return null;
        }
        String sharePath = shareMap.get(matcher.group(2));
        if (sharePath == null) {
            LOG.info("UNC path '" + canonicalUncPath + "'s share is not known");
            return null;
        }
        return new File(sharePath, matcher.group(3));
    }

}
