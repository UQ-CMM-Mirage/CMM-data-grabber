package au.edu.uq.cmm.eccles;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Table;

import au.edu.uq.cmm.aclslib.config.ACLSProxyConfiguration;

@Entity
@Table(name = "CONFIGURATION")
public class EcclesProxyConfiguration implements ACLSProxyConfiguration {
    private Long id;
    private int proxyPort;
    private String serverHost;
    private int serverPort;
    private boolean useProject;
    private String proxyHost;
    private String dummyFacilityName;
    private String dummyFacilityHostId;
    
    
    public EcclesProxyConfiguration() {
        super();
    }

    public static ACLSProxyConfiguration load(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("from EcclesProxyConfiguration", 
                    EcclesProxyConfiguration.class).getSingleResult();
        } finally {
            em.close();
        }
    }
    
    @Override
    public int getProxyPort() {
        return proxyPort;
    }

    @Override
    public String getServerHost() {
        return serverHost;
    }

    @Override
    public int getServerPort() {
        return serverPort;
    }

    @Override
    public boolean isUseProject() {
        return useProject;
    }

    @Override
    public String getProxyHost() {
        return proxyHost;
    }

    @Override
    public String getDummyFacilityName() {
        return dummyFacilityName;
    }

    @Override
    public String getDummyFacilityHostId() {
        return dummyFacilityHostId;
    }

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDummyFacilityHostId(String dummyFacilityHostId) {
        this.dummyFacilityHostId = dummyFacilityHostId;
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

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setDummyFacilityName(String dummyFacilityName) {
        this.dummyFacilityName = dummyFacilityName;
    }

}
