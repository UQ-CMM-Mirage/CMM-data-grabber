package au.edu.uq.cmm.paul;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;

import au.edu.uq.cmm.aclslib.proxy.AclsProxy;
import au.edu.uq.cmm.aclslib.service.CompositeServiceBase;
import au.edu.uq.cmm.aclslib.service.ServiceException;
import au.edu.uq.cmm.paul.GrabberConfiguration.DataGrabberRestartPolicy;
import au.edu.uq.cmm.paul.queue.QueueExpirer;
import au.edu.uq.cmm.paul.queue.QueueManager;
import au.edu.uq.cmm.paul.servlet.ConfigurationManager;
import au.edu.uq.cmm.paul.status.FacilityStatusManager;
import au.edu.uq.cmm.paul.status.SessionDetailMapper;
import au.edu.uq.cmm.paul.status.UserDetailsManager;
import au.edu.uq.cmm.paul.watcher.FileWatcher;
import au.edu.uq.cmm.paul.watcher.SambaUncPathnameMapper;
import au.edu.uq.cmm.paul.watcher.UncPathnameMapper;

public class Paul extends CompositeServiceBase implements Lifecycle {
    // FIXME - need to do something to hook this class into the servlet lifecycle 
    // so that it gets told when the servlet is being shutdown.
    private static final Logger LOG = LoggerFactory.getLogger(Paul.class);
    private FacilityStatusManager statusManager;
    private AclsProxy proxy;
    private UncPathnameMapper uncNameMapper;
    private EntityManagerFactory entityManagerFactory;
    private QueueManager queueManager;
    private QueueExpirer queueExpirer;
    private SessionDetailMapper sessionDetailMapper;
    private FileWatcher fileWatcher;
    private UserDetailsManager userDetailsManager;
    private ConfigurationManager configManager;
    
    public Paul(StaticPaulConfiguration staticConfig,
            StaticPaulFacilities staticFacilities,
            EntityManagerFactory entityManagerFactory)
    throws IOException {
        this(staticConfig, staticFacilities,
                entityManagerFactory, null, new SambaUncPathnameMapper());
    }
    
    public Paul(StaticPaulConfiguration staticConfig,
            StaticPaulFacilities staticFacilities,
            EntityManagerFactory entityManagerFactory,
            SessionDetailMapper sessionDetailMapper,
            UncPathnameMapper uncNameMapper)
    throws IOException {
        this.entityManagerFactory = entityManagerFactory;
        this.sessionDetailMapper = sessionDetailMapper;
        configManager = new ConfigurationManager(entityManagerFactory, staticConfig, staticFacilities);
        // Testing ...
        PaulConfiguration.load(entityManagerFactory);
        proxy = new AclsProxy(getConfiguration(), configManager.getFacilityMapper());
        try {
            proxy.probeServer();
        } catch (ServiceException ex) {
            LOG.error("ACLS server probe failed", ex);
            LOG.info("Continuing regardless ...");
        }
        statusManager = new FacilityStatusManager(this);
        this.uncNameMapper = uncNameMapper;
        fileWatcher = new FileWatcher(this);
        queueManager = new QueueManager(this);
        queueExpirer = new QueueExpirer(this);
        userDetailsManager = new UserDetailsManager(this);
    }

    // FIXME - get rid if this?
    public static void main(String[] args) {
        String configFile = null;
        String facilityFile = null;
        if (args.length > 0) {
            configFile = args[0];
            if (args.length > 1) {
                facilityFile = args[1];
            }
        }
        try {
            StaticPaulConfiguration staticConfig = null;
            StaticPaulFacilities staticFacilities = null;
            if (configFile != null) {
                staticConfig = StaticPaulConfiguration.loadConfiguration(configFile);
                if (staticConfig == null) {
                    LOG.info("Can't read/load initial configuration file");
                    System.exit(2);
                }
            }
            if (facilityFile != null) {
                staticFacilities = StaticPaulFacilities.loadFacilities(facilityFile);
                if (staticFacilities == null) {
                    LOG.info("Can't read/load initial facilities file");
                    System.exit(2);
                }
            }
            Paul grabber = new Paul(staticConfig, staticFacilities, 
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
        fileWatcher.shutdown();
        proxy.shutdown();
        LOG.info("Shutdown completed");
    }

    @Override
    protected void doStartup() throws ServiceException {
        LOG.info("Startup started");
        proxy.startup();
        if (getConfiguration().getDataGrabberRestartPolicy() !=
                DataGrabberRestartPolicy.NO_AUTO_START) {
            fileWatcher.startup();
        }
        queueExpirer.startup();
        LOG.info("Startup completed");
    }

    public FacilityStatusManager getFacilitySessionManager() {
        return statusManager;
    }

    public PaulConfiguration getConfiguration() {
        return configManager.getActiveConfig();
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public ConfigurationManager getConfigManager() {
        return configManager;
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

    public UserDetailsManager getUserDetailsManager() {
        return userDetailsManager;
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
        return getState() == State.STARTED;
    }
}
