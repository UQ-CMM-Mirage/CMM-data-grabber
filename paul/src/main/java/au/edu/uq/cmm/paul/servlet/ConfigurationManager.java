package au.edu.uq.cmm.paul.servlet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.aclslib.config.FacilityConfig;
import au.edu.uq.cmm.aclslib.config.StaticConfiguration;
import au.edu.uq.cmm.paul.PaulConfiguration;
import au.edu.uq.cmm.paul.status.Facility;

public class ConfigurationManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationManager.class);

    PaulConfiguration activeConfig;
    PaulConfiguration latestConfig;
    StaticConfiguration staticConfig;
    private EntityManagerFactory entityManagerFactory;

    public ConfigurationManager(EntityManagerFactory entityManagerFactory,
            StaticConfiguration staticConfig) {
        this.entityManagerFactory = entityManagerFactory;
        this.staticConfig = staticConfig;
        activeConfig = PaulConfiguration.load(entityManagerFactory, true);
        if (activeConfig.isEmpty() && staticConfig != null) {
            activeConfig = doResetConfiguration();
        }
        latestConfig = activeConfig;
    }

    public PaulConfiguration getActiveConfig() {
        return activeConfig;
    }

    public PaulConfiguration getLatestConfig() {
        return latestConfig;
    }
    
    public void resetConfiguration() {
        latestConfig = doResetConfiguration();
    }

    private PaulConfiguration doResetConfiguration() {
        LOG.info("Resetting details from static Configuration");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            // FIXME - we do two transactions.  Combining the two transactions 
            // into one transaction is gives constraint errors when adding the new
            // facilities.  (The fix is to version the configurations.)
            entityManager.getTransaction().begin();
            PaulConfiguration config = entityManager.
                    createQuery("from PaulConfiguration", PaulConfiguration.class).
                    getSingleResult();
            entityManager.remove(config);
            entityManager.getTransaction().commit();
            
            // Second transaction
            entityManager.getTransaction().begin();
            config = new PaulConfiguration();
            config.setProxyHost(staticConfig.getProxyHost());
            config.setServerHost(staticConfig.getServerHost());
            config.setProxyPort(staticConfig.getProxyPort());
            config.setServerPort(staticConfig.getServerPort());
            config.setBaseFileUrl(staticConfig.getBaseFileUrl());
            config.setCaptureDirectory(staticConfig.getCaptureDirectory());
            config.setArchiveDirectory(staticConfig.getArchiveDirectory());
            config.setFeedId(staticConfig.getFeedId());
            config.setFeedTitle(staticConfig.getFeedTitle());
            config.setFeedAuthor(staticConfig.getFeedAuthor());
            config.setFeedAuthorEmail(staticConfig.getFeedAuthorEmail());
            config.setFeedUrl(staticConfig.getFeedUrl());
            config.setFeedPageSize(staticConfig.getFeedPageSize());
            config.setQueueExpiryInterval(staticConfig.getQueueExpiryInterval());
            config.setQueueExpiryTime(staticConfig.getQueueExpiryTime());
            config.setExpireByDeleting(staticConfig.isExpireByDeleting());
            config.setDataGrabberRestartPolicy(staticConfig.getDataGrabberRestartPolicy());
            config.setHoldDatasetsWithNoUser(staticConfig.isHoldDatasetsWithNoUser());
            config.setPrimaryRepositoryUrl(staticConfig.getPrimaryRepositoryUrl());
            config.setAclsUrl(staticConfig.getAclsUrl());
            for (FacilityConfig facilityConfig: staticConfig.getFacilityConfigs()) {
                config.getFacilities().add(new Facility(facilityConfig));
            }
            entityManager.persist(config);
            entityManager.getTransaction().commit();
            return config;
        } finally {
            entityManager.close();
        }
    }
}
