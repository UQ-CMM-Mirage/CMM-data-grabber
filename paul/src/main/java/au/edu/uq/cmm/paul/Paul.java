package au.edu.uq.cmm.paul;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.springframework.context.Lifecycle;

import au.edu.uq.cmm.aclslib.config.StaticConfiguration;
import au.edu.uq.cmm.aclslib.proxy.AclsProxy;
import au.edu.uq.cmm.aclslib.service.CompositeServiceBase;
import au.edu.uq.cmm.aclslib.service.ServiceException;
import au.edu.uq.cmm.paul.grabber.FileGrabber;
import au.edu.uq.cmm.paul.queue.QueueExpirer;
import au.edu.uq.cmm.paul.queue.QueueManager;
import au.edu.uq.cmm.paul.status.FacilityStatusManager;
import au.edu.uq.cmm.paul.status.SessionDetailMapper;
import au.edu.uq.cmm.paul.watcher.FileWatcher;
import au.edu.uq.cmm.paul.watcher.SambaUncPathnameMapper;
import au.edu.uq.cmm.paul.watcher.UncPathnameMapper;

public class Paul extends CompositeServiceBase implements Lifecycle {
    // FIXME - need to do something to hook this class into the servlet lifecycle 
    // so that it gets told when the servlet is being shutdown.
    private static final Logger LOG = Logger.getLogger(Paul.class);
    private FileWatcher fileWatcher;
    private FileGrabber fileGrabber;
    private FacilityStatusManager statusManager;
    private AclsProxy proxy;
    private UncPathnameMapper uncNameMapper;
    private EntityManagerFactory entityManagerFactory;
    private PaulConfiguration config;
    private QueueManager queueManager;
    private QueueExpirer queueExpirer;
    private SessionDetailMapper sessionDetailMapper;
    
    public Paul(StaticConfiguration staticConfig,
            EntityManagerFactory entityManagerFactory)
    throws IOException {
        this(staticConfig, entityManagerFactory, null, new SambaUncPathnameMapper());
    }
    
    public Paul(StaticConfiguration staticConfig,
            EntityManagerFactory entityManagerFactory,
            SessionDetailMapper sessionDetailMapper,
            UncPathnameMapper uncNameMapper)
    throws IOException {
        this.entityManagerFactory = entityManagerFactory;
        this.sessionDetailMapper = sessionDetailMapper;
        config = PaulConfiguration.load(entityManagerFactory, true);
        
        if (config.isEmpty() && staticConfig != null) {
            config = config.merge(entityManagerFactory, staticConfig);
        }
        // Testing ...
        PaulConfiguration.load(entityManagerFactory);
        proxy = new AclsProxy(config);
        try {
            proxy.probeServer();
        } catch (ServiceException ex) {
            LOG.error("ACLS server probe failed", ex);
            LOG.info("Continuing regardless ...");
        }
        statusManager = new FacilityStatusManager(this);
        this.uncNameMapper = uncNameMapper;
        fileWatcher = new FileWatcher(this);
        fileGrabber = new FileGrabber(this);
        queueManager = new QueueManager(this);
        queueExpirer = new QueueExpirer(this);
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
        LOG.info("Shutdown started");
        queueExpirer.shutdown();
        fileGrabber.shutdown();
        fileWatcher.shutdown();
        proxy.shutdown();
        LOG.info("Shutdown completed");
    }

    @Override
    protected void doStartup() throws ServiceException {
        LOG.info("Startup started");
        proxy.startup();
        fileWatcher.startup();
        fileGrabber.startup();
        queueExpirer.startup();
        LOG.info("Startup completed");
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

    public SessionDetailMapper getSessionDetailMapper() {
        return sessionDetailMapper;
    }

    @Override
    public void start() {
        startup();
    }

    @Override
    public void stop() {
        try {
            shutdown();
        } catch (InterruptedException ex) {
            LOG.warn("Shutdown interrupted", ex);
        }
    }

    @Override
    public boolean isRunning() {
        return getState() == State.RUNNING;
    }
    
    
}
