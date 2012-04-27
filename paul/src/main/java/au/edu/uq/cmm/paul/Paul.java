package au.edu.uq.cmm.paul;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;

import au.edu.uq.cmm.aclslib.proxy.AclsHelper;
import au.edu.uq.cmm.aclslib.service.CompositeServiceBase;
import au.edu.uq.cmm.aclslib.service.ServiceException;
import au.edu.uq.cmm.eccles.UserDetailsManager;
import au.edu.uq.cmm.paul.GrabberConfiguration.DataGrabberRestartPolicy;
import au.edu.uq.cmm.paul.queue.QueueExpirer;
import au.edu.uq.cmm.paul.queue.QueueManager;
import au.edu.uq.cmm.paul.servlet.ConfigurationManager;
import au.edu.uq.cmm.paul.status.FacilityStatusManager;
import au.edu.uq.cmm.paul.watcher.FileWatcher;
import au.edu.uq.cmm.paul.watcher.SambaUncPathnameMapper;
import au.edu.uq.cmm.paul.watcher.UncPathnameMapper;

public class Paul extends CompositeServiceBase implements Lifecycle {
    // FIXME - need to do something to hook this class into the servlet lifecycle 
    // so that it gets told when the servlet is being shutdown.
    private static final Logger LOG = LoggerFactory.getLogger(Paul.class);
    private FacilityStatusManager statusManager;
    private UncPathnameMapper uncNameMapper;
    private EntityManagerFactory entityManagerFactory;
    private QueueManager queueManager;
    private QueueExpirer queueExpirer;
    private FileWatcher fileWatcher;
    private UserDetailsManager userDetailsManager;
    private ConfigurationManager configManager;
    private PaulFacilityMapper facilityMapper;
    private AclsHelper aclsHelper;
    
    public Paul(StaticPaulConfiguration staticConfig,
            StaticPaulFacilities staticFacilities,
            EntityManagerFactory entityManagerFactory)
    throws IOException {
        this(staticConfig, staticFacilities,
                entityManagerFactory, new SambaUncPathnameMapper());
    }
    
    public Paul(StaticPaulConfiguration staticConfig,
            StaticPaulFacilities staticFacilities,
            EntityManagerFactory entityManagerFactory,
            UncPathnameMapper uncNameMapper)
    throws IOException {
        this.entityManagerFactory = entityManagerFactory;
        this.configManager = new ConfigurationManager(entityManagerFactory, staticConfig, staticFacilities);
        this.facilityMapper = new PaulFacilityMapper(entityManagerFactory);
        PaulConfiguration activeConfig = configManager.getActiveConfig();
        this.aclsHelper = new AclsHelper(activeConfig.getProxyHost(), activeConfig.getProxyPort(), false);
        this.statusManager = new FacilityStatusManager(this);
        this.uncNameMapper = uncNameMapper;
        this.fileWatcher = new FileWatcher(this);
        this.queueManager = new QueueManager(this);
        this.queueExpirer = new QueueExpirer(this);
        this.userDetailsManager = new UserDetailsManager(entityManagerFactory);
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
        LOG.info("Shutdown completed");
    }

    @Override
    protected void doStartup() throws ServiceException {
        LOG.info("Startup started");
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

    public UncPathnameMapper getUncNameMapper() {
        return uncNameMapper;
    }

    public UserDetailsManager getUserDetailsManager() {
        return userDetailsManager;
    }

    public PaulFacilityMapper getFacilityMapper() {
        return facilityMapper;
    }

    public AclsHelper getAclsHelper() {
        return aclsHelper;
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
