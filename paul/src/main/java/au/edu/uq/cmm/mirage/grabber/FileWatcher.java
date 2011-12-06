package au.edu.uq.cmm.mirage.grabber;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;

import org.apache.log4j.Logger;

import au.edu.uq.cmm.aclslib.service.MonitoredThreadServiceBase;

public class FileWatcher extends MonitoredThreadServiceBase {
    private static final Logger LOG = Logger.getLogger(FileWatcher.class);
    
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    @Override
    public void run() {
        try {
            FileSystem fs = FileSystems.getDefault();
            WatchService watcher = fs.newWatchService();
            Path dir = Paths.get(new File("/tmp").toURI());
            dir.register(watcher, 
                    StandardWatchEventKinds.ENTRY_CREATE, 
                    StandardWatchEventKinds.ENTRY_MODIFY, 
                    StandardWatchEventKinds.ENTRY_DELETE, 
                    StandardWatchEventKinds.OVERFLOW);
            while (true) {
                WatchKey key = watcher.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        LOG.error("Event overflow!");
                        continue;
                    }
                    WatchEvent<Path> ev = cast(event);
                    Path file = dir.resolve(ev.context());
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        LOG.debug("Created - " + file);
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        LOG.debug("Modified - " + file);
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        LOG.debug("Deleted - " + file);
                    }
                }
                key.reset();
            }
        } catch (IOException ex) {
            throw new GrabberException("Unexpected IO error", ex);
        } catch (InterruptedException ex) {
            LOG.debug("Interrupted ... we're done");
        } 
    }

}
