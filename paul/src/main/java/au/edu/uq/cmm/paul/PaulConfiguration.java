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
public class PaulConfiguration implements Configuration {
    private static final Logger LOG = Logger.getLogger(PaulConfiguration.class);
    
    private Long id;
    private Map<String, Facility> facilityMap = new HashMap<String, Facility>();
    
    private int proxyPort = 1024;
    private int serverPort = 1024;
    private String serverHost;
    private String proxyHost;
    private boolean useProject;
    private String captureDirectory;
    private String baseFileUrl;
    private String feedId;
    private String feedTitle;
    private String feedAuthor;
    private String feedAuthorEmail;
    private String feedUrl;
    private int feedPageSize = 20;

    
    public int getProxyPort() {
        return proxyPort;
    }

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public boolean isUseProject() {
        return useProject;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setUseProject(boolean useProject) {
        this.useProject = useProject;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    @Transient
    public final String getDummyFacility() {
        for (FacilityConfig facility : getFacilities()) {
            if (facility.isDummy()) {
                return facility.getFacilityName();
            }
        }
        throw new IllegalStateException("There are no dummy facilities");
    }

    public String getCaptureDirectory() {
        return captureDirectory;
    }

    public void setCaptureDirectory(String captureDirectory) {
        this.captureDirectory = captureDirectory;
    }

    public String getBaseFileUrl() {
        return baseFileUrl;
    }

    public void setBaseFileUrl(String baseFileUrl) {
        this.baseFileUrl = baseFileUrl;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getFeedTitle() {
        return feedTitle;
    }

    public void setFeedTitle(String feedTitle) {
        this.feedTitle = feedTitle;
    }

    public String getFeedAuthor() {
        return feedAuthor;
    }

    public void setFeedAuthor(String feedAuthor) {
        this.feedAuthor = feedAuthor;
    }

    public String getFeedAuthorEmail() {
        return feedAuthorEmail;
    }

    public void setFeedAuthorEmail(String feedAuthorEmail) {
        this.feedAuthorEmail = feedAuthorEmail;
    }
    
    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }
    
    public int getFeedPageSize() {
        return feedPageSize;
    }

    public void setFeedPageSize(int feedPageSize) {
        this.feedPageSize = feedPageSize;
    }

    public static PaulConfiguration load(EntityManagerFactory entityManagerFactory) {
        return load(entityManagerFactory, false);
    }
    
    public static PaulConfiguration load(EntityManagerFactory entityManagerFactory,
            boolean createIfMissing) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            try {
                PaulConfiguration res = entityManager.
                    createQuery("from PaulConfiguration", PaulConfiguration.class).
                    getSingleResult();
                return res;
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

    public Facility lookupFacilityByName(String id) {
        for (Facility f : facilityMap.values()) {
            if (id.equals(f.getFacilityName())) {
                return f;
            }
        }
        return null;
    }

    @Transient
    public boolean isEmpty() {
        return facilityMap.isEmpty();
    }

    public PaulConfiguration merge(EntityManagerFactory entityManagerFactory,
            Configuration staticConfig) {
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
            setFeedId(staticConfig.getFeedId());
            setFeedTitle(staticConfig.getFeedTitle());
            setFeedAuthor(staticConfig.getFeedAuthor());
            setFeedAuthorEmail(staticConfig.getFeedAuthorEmail());
            setFeedUrl(staticConfig.getFeedUrl());
            setFeedPageSize(staticConfig.getFeedPageSize());
            for (FacilityConfig facilityConfig: staticConfig.getFacilities()) {
                if (!facilityMap.containsKey(facilityConfig.getAddress())) {
                    Facility facility = new Facility(facilityConfig);
                    facilityMap.put(facility.getAddress(), facility);
                    LOG.info("Merged facility '" + facility.getFacilityName() + 
                            "' with address '" + facility.getAddress() + "'");
                }
            }
            PaulConfiguration res = entityManager.merge(this);
            entityManager.getTransaction().commit();
            return res;
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
