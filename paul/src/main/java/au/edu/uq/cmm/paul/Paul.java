package au.edu.uq.cmm.paul;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

import au.edu.uq.cmm.aclslib.proxy.AclsProxy;
import au.edu.uq.cmm.aclslib.server.StaticConfiguration;
import au.edu.uq.cmm.aclslib.service.CompositeServiceBase;
import au.edu.uq.cmm.aclslib.service.ServiceException;
import au.edu.uq.cmm.paul.grabber.FileGrabber;
import au.edu.uq.cmm.paul.queue.QueueManager;
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
    private EntityManagerFactory entityManagerFactory;
    private PaulConfiguration config;
    private QueueManager queueManager;
    
    public Paul(StaticConfiguration staticConfig,
            EntityManagerFactory entityManagerFactory) throws IOException {
        this.entityManagerFactory = entityManagerFactory;
        config = new PaulConfiguration();
        if (config.isEmpty() && staticConfig != null) {
            config.merge(entityManagerFactory, staticConfig);
        }
        proxy = new AclsProxy(config);
        try {
            proxy.probeServer();
        } catch (ServiceException ex) {
            LOG.error("ACLS server probe failed", ex);
            LOG.info("Continuing regardless ...");
        }
        statusManager = new FacilityStatusManager(this);
        // FIXME ... this should be pluggable.
        uncNameMapper = new SambaUncPathameMapper(SMB_CONF_PATHNAME);
        fileWatcher = new FileWatcher(this);
        fileGrabber = new FileGrabber(this);
        queueManager = new QueueManager(this);
    }

    // FIXME - get rid if this?
    public static void main(String[] args) {
        String configFile = null;
        if (args.length > 0) {
            configFile = args[0];
        }
        try {
            StaticConfiguration staticConfig = null;
            if (configFile != null) {
                staticConfig = StaticConfiguration.loadConfiguration(configFile);
                if (staticConfig == null) {
                    LOG.info("Can't read/load initial configuration file");
                    System.exit(2);
                }
            }
            Paul grabber = new Paul(staticConfig, 
                    Persistence.createEntityManagerFactory("au.edu.uq.cmm.paul"));
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

    public PaulConfiguration getConfiguration() {
        return config;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public FileGrabber getFileGrabber() {
        return fileGrabber;
    }

    public FileWatcher getFileWatcher() {
        return fileWatcher;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public AclsProxy getProxy() {
        return proxy;
    }

    public UncPathnameMapper getUncNameMapper() {
        return uncNameMapper;
    }
}
