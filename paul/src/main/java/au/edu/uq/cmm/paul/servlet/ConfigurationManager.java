package au.edu.uq.cmm.paul.servlet;

import java.net.UnknownHostException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.paul.PaulConfiguration;
import au.edu.uq.cmm.paul.StaticPaulConfiguration;
import au.edu.uq.cmm.paul.StaticPaulFacilities;
import au.edu.uq.cmm.paul.StaticPaulFacility;
import au.edu.uq.cmm.paul.status.Facility;

public class ConfigurationManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationManager.class);

    private PaulConfiguration activeConfig;
    private PaulConfiguration latestConfig;
    private StaticPaulConfiguration staticConfig;
    private EntityManagerFactory entityManagerFactory;
    private StaticPaulFacilities staticFacilities;

    
    public ConfigurationManager(EntityManagerFactory entityManagerFactory,
            StaticPaulConfiguration staticConfig, 
            StaticPaulFacilities staticFacilities) {
        this.entityManagerFactory = entityManagerFactory;
        this.staticConfig = staticConfig;
        this.staticFacilities =  staticFacilities;
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
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PaulConfiguration newConfig = new PaulConfiguration(staticConfig);
            // FIXME - we do two transactions.  Combining the two transactions 
            // into one transaction is gives constraint errors when adding the new
            // facilities.  (The fix is to version the configurations.)
            em.getTransaction().begin();
            PaulConfiguration oldConfig = em.
                    createQuery("from PaulConfiguration", PaulConfiguration.class).
                    getSingleResult();
            em.remove(oldConfig);
            List<Facility> facilities = em.
                    createQuery("from Facility", Facility.class).
                    getResultList();
            for (Facility facility : facilities) {
                em.remove(facility);
            }
            em.getTransaction().commit();
            
            // Second transaction
            em.getTransaction().begin();
            em.persist(newConfig);
            for (StaticPaulFacility staticFacility : staticFacilities.getFacilities()) {
                Facility facility = new Facility(staticFacility);
                em.persist(facility);
            }
            em.getTransaction().commit();
            return newConfig;
        } catch (UnknownHostException ex) {
            LOG.error("Reset failed", ex);
            return null;
        } finally {
            em.close();
        }
    }
}
