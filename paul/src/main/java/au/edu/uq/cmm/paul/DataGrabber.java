package au.edu.uq.cmm.paul;

import java.net.UnknownHostException;

import au.edu.uq.cmm.aclslib.service.CompositeServiceBase;
import au.edu.uq.cmm.paul.grabber.FileGrabber;
import au.edu.uq.cmm.paul.watcher.FileWatcher;

public class DataGrabber extends CompositeServiceBase {
    private FileWatcher fileWatcher;
    private FileGrabber fileGrabber;
    
    
    public DataGrabber(Paul services) throws UnknownHostException {
        super();
        fileWatcher = new FileWatcher(services);
        fileGrabber = new FileGrabber(services, fileWatcher);
    }

    @Override
    protected void doShutdown() throws InterruptedException {
        fileGrabber.shutdown();
        fileWatcher.shutdown();
    }

    @Override
    protected void doStartup() {
        fileWatcher.startup();
        fileGrabber.startup();
    }

    public FileWatcher getFileWatcher() {
        return fileWatcher;
    }

    public FileGrabber getFileGrabber() {
        return fileGrabber;
    }

}
