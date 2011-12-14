package au.edu.uq.cmm.paul.watcher;

import java.util.EventListener;

public interface FileWatcherEventListener extends EventListener {

    void eventOccurred(FileWatcherEvent event);
}
