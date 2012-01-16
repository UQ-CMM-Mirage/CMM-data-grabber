package au.edu.uq.cmm.paul;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;

import au.edu.uq.cmm.aclslib.server.Configuration;
import au.edu.uq.cmm.aclslib.server.ConfigurationBase;
import au.edu.uq.cmm.aclslib.server.FacilityConfig;
import au.edu.uq.cmm.paul.status.Facility;

/**
 * This class represents the configuration details of a DataGrabber instance.
 * The details are persisted using Hibernate / JPA.
 * 
 * @author scrawley
 */
@Entity
@Table(name = "CONFIGURATION")
public class PaulConfiguration extends ConfigurationBase implements Configuration {
    private static final Logger LOG = Logger.getLogger(PaulConfiguration.class);

    private Long id;
    private Map<String, Facility> facilityMap = new HashMap<String, Facility>();
    
    
    public static PaulConfiguration load(EntityManagerFactory entityManagerFactory) {
        return load(entityManagerFactory, false);
    }
    
    public static PaulConfiguration load(EntityManagerFactory entityManagerFactory,
            boolean createIfMissing) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            try {
                return entityManager.
                    createQuery("from PaulConfiguration", PaulConfiguration.class).
                    getSingleResult();
            } catch (NoResultException ex) {
                if (createIfMissing) {
                    PaulConfiguration res = new PaulConfiguration();
                    entityManager.persist(res);
                    entityManager.getTransaction().commit();
                    return res;
                } else {
                    throw new PaulException("The configuration record is missing", ex);
                }
            }
        } finally {
            entityManager.close();
        }
    }
    
    @Transient
    public Collection<FacilityConfig> getFacilities() {
        return new ArrayList<FacilityConfig>(facilityMap.values());
    }

    public Facility lookupFacilityByAddress(InetAddress addr) {
        Facility facility = facilityMap.get(addr.getHostAddress());
        if (facility == null) {
            facility = facilityMap.get(addr.getHostName());
        }
        return facility;
    }

    public Facility lookupFacilityById(String id) {
        for (Facility f : facilityMap.values()) {
            if (id.equals(f.getFacilityId())) {
                return f;
            }
        }
        return null;
    }

    @Transient
    public boolean isEmpty() {
        return facilityMap.isEmpty();
    }

    public void merge(EntityManagerFactory entityManagerFactory,
            ConfigurationBase staticConfig) {
        LOG.info("Merging details from static Configuration");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            setProxyHost(staticConfig.getProxyHost());
            setServerHost(staticConfig.getServerHost());
            setProxyPort(staticConfig.getProxyPort());
            setServerPort(staticConfig.getServerPort());
            setBaseFileUrl(staticConfig.getBaseFileUrl());
            setCaptureDirectory(staticConfig.getCaptureDirectory());
            for (FacilityConfig facilityConfig: staticConfig.getFacilities()) {
                if (!facilityMap.containsKey(facilityConfig.getAddress())) {
                    Facility facility = new Facility(facilityConfig);
                    facilityMap.put(facility.getAddress(), facility);
                    LOG.info("Merged facility '" + facility.getFacilityId() + 
                            "' with address '" + facility.getAddress() + "'");
                }
            }
            entityManager.merge(this);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    @OneToMany(mappedBy="configuration", cascade=CascadeType.ALL)
    @MapKey(name="address")
    public Map<String, Facility> getFacilityMap() {
        return facilityMap;
    }

    public void setFacilityMap(Map<String, Facility> facilityMap) {
        this.facilityMap = facilityMap;
    }

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
