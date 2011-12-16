package au.edu.uq.cmm.paul;

import java.io.IOException;

import org.apache.log4j.Logger;

import au.edu.uq.cmm.aclslib.proxy.AclsProxy;
import au.edu.uq.cmm.aclslib.server.Configuration;
import au.edu.uq.cmm.aclslib.service.CompositeServiceBase;
import au.edu.uq.cmm.aclslib.service.ServiceException;
import au.edu.uq.cmm.paul.grabber.FileGrabber;
import au.edu.uq.cmm.paul.status.FacilityStatusManager;
import au.edu.uq.cmm.paul.watcher.FileWatcher;
import au.edu.uq.cmm.paul.watcher.SambaUncPathameMapper;
import au.edu.uq.cmm.paul.watcher.UncPathnameMapper;

public class Paul extends CompositeServiceBase {
    private static final String SMB_CONF_PATHNAME = "/etc/samba/smb.conf";
    private static final Logger LOG = Logger.getLogger(Paul.class);
    private FileWatcher fileWatcher;
    private FileGrabber fileGrabber;
    private FacilityStatusManager statusManager;
    private AclsProxy proxy;
    private UncPathnameMapper uncNameMapper;
    
    public Paul(Configuration config) throws IOException {
        this.proxy = new AclsProxy(config);
        // If the probe fails, we die ...
        proxy.probeServer();
        this.statusManager = new FacilityStatusManager(proxy);
        // FIXME ... this should be pluggable.
        this.uncNameMapper = new SambaUncPathameMapper(SMB_CONF_PATHNAME);
        this.fileWatcher = new FileWatcher(config, uncNameMapper);
        this.fileGrabber = new FileGrabber(fileWatcher, statusManager);
    }

    public static void main(String[] args) {
        String configFile = null;
        if (args.length > 0) {
            configFile = args[0];
        }
        try {
            Configuration config = Configuration.loadConfiguration(configFile);
            if (config == null) {
                LOG.info("Can't read/load configuration file");
                System.exit(2);
            }
            Paul grabber = new Paul(config);
            grabber.startup();
            grabber.awaitShutdown();
            LOG.info("Exitting normally");
            System.exit(0);
        } catch (Throwable ex) {
            LOG.error("Unhandled exception", ex);
            System.exit(1);
        }
    }
    
    @Override
    protected void doShutdown() throws InterruptedException {
        fileGrabber.shutdown();
        fileWatcher.shutdown();
        proxy.shutdown();
    }

    @Override
    protected void doStartup() throws ServiceException {
        proxy.startup();
        fileWatcher.startup();
        fileGrabber.startup();
    }

    public FacilityStatusManager getFacilitySessionManager() {
        return statusManager;
    }
}
