package au.edu.uq.cmm.paul.grabber;

import au.edu.uq.cmm.aclslib.service.ThreadServiceBase;
import au.edu.uq.cmm.paul.watcher.FileWatcher;
import au.edu.uq.cmm.paul.watcher.FileWatcherEvent;
import au.edu.uq.cmm.paul.watcher.FileWatcherEventListener;

public class FileGrabber extends ThreadServiceBase implements FileWatcherEventListener {
    
    public FileGrabber(FileWatcher watcher) {
        watcher.addListener(this);
    }

    @Override
    public void eventOccurred(FileWatcherEvent event) {
        System.err.println("Event occured : " + 
                event.getFacility().getFacilityId() + "," + 
                event.getFile() + "," + 
                event.isCreate());
    }

    @Override
    public void run() {
        // FIXME - implement
    }

}
