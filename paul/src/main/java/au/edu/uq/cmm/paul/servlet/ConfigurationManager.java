package au.edu.uq.cmm.paul.servlet;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.aclslib.config.ConfigurationException;
import au.edu.uq.cmm.aclslib.config.FacilityConfig;
import au.edu.uq.cmm.aclslib.config.FacilityMapper;
import au.edu.uq.cmm.paul.PaulConfiguration;
import au.edu.uq.cmm.paul.StaticPaulConfiguration;
import au.edu.uq.cmm.paul.StaticPaulFacilities;
import au.edu.uq.cmm.paul.StaticPaulFacility;
import au.edu.uq.cmm.paul.status.Facility;

public class ConfigurationManager implements FacilityMapper {
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

    public FacilityMapper getFacilityMapper() {
        return this;
    }
    
    public void resetConfiguration() {
        latestConfig = doResetConfiguration();
    }

    @Override
    public FacilityConfig lookup(String localHostId, String facilityName,
            InetAddress clientAddr) throws ConfigurationException {
        EntityManager em = entityManagerFactory.createEntityManager();
        TypedQuery<Facility> query;
        Facility res;
        try {
            if (localHostId != null) {
                query = em.createQuery(
                        "from Facility f where f.localHostId = :localHostId",
                        Facility.class);
                query.setParameter("localHostId", localHostId);
                res = getFirst(query, "hostId", localHostId);
                if (res != null) {
                    return res;
                }
            }
            if (facilityName != null) {
                query = em.createQuery(
                        "from Facility f where f.facilityName = :facilityName",
                        Facility.class);
                query.setParameter("facilityName", facilityName);
                res = getFirst(query, "facilityName", facilityName);
                if (res != null) {
                    return res;
                }
            }
            if (clientAddr != null) {
                String ipAddress = clientAddr.getHostAddress();
                String fqdn = clientAddr.getCanonicalHostName();
                String[] hostNameParts = clientAddr.getCanonicalHostName().split("\\.");
                query = em.createQuery(
                        "from Facility f where f.address = :ipAddress or " +
                        "f.address = :fqdn or f.address = :hostName",
                        Facility.class);
                query.setParameter("ipAddress", ipAddress);
                query.setParameter("fqdn", fqdn);
                query.setParameter("hostName", hostNameParts[hostNameParts.length - 1]);
                res = getFirst(query, "ipAddress/hostname", ipAddress + "/" + fqdn);
                if (res != null) {
                    return res;
                }
            }
            return null;
        } finally {
            em.close();
        }
    }

    private Facility getFirst(TypedQuery<Facility> query, String key, String keyValue) 
            throws ConfigurationException {
        List<Facility> list = query.getResultList();
        if (list.size() == 0) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            throw new ConfigurationException(
                    "Multiple facilities have " + key + " equal to " + keyValue);
        }
    }

    @Override
    public Collection<FacilityConfig> allFacilities() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            TypedQuery<Facility> query = em.createQuery(
                    "from Facility", Facility.class);
            return new ArrayList<FacilityConfig>(query.getResultList());
        } finally {
            em.close();
        }
    }

    private PaulConfiguration doResetConfiguration() {
        LOG.info("Resetting details from static Configuration");
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            // FIXME - we do two transactions.  Combining the two transactions 
            // into one transaction is gives constraint errors when adding the new
            // facilities.  (The fix is to version the configurations.)
            em.getTransaction().begin();
            PaulConfiguration config = em.
                    createQuery("from PaulConfiguration", PaulConfiguration.class).
                    getSingleResult();
            em.remove(config);
            List<Facility> facilities = em.
                    createQuery("from Facility", Facility.class).
                    getResultList();
            for (Facility facility : facilities) {
                em.remove(facility);
            }
            em.getTransaction().commit();
            
            // Second transaction
            em.getTransaction().begin();
            config = new PaulConfiguration(staticConfig);
            em.persist(config);
            for (StaticPaulFacility staticFacility : staticFacilities.getFacilities()) {
                Facility facility = new Facility(staticFacility);
                em.persist(facility);
            }
            em.getTransaction().commit();
            return config;
        } finally {
            em.close();
        }
    }
}
