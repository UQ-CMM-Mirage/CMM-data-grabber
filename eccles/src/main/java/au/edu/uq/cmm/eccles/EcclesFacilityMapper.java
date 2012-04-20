package au.edu.uq.cmm.eccles;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import au.edu.uq.cmm.aclslib.config.ConfigurationException;
import au.edu.uq.cmm.aclslib.config.FacilityConfig;
import au.edu.uq.cmm.aclslib.config.FacilityMapper;

public class EcclesFacilityMapper implements FacilityMapper {

    private EntityManagerFactory entityManagerFactory;
    

    public EcclesFacilityMapper(EntityManagerFactory entityManagerFactory) {
        super();
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public FacilityConfig lookup(String localHostId, String facilityName,
            InetAddress clientAddr) throws ConfigurationException {
        EntityManager em = entityManagerFactory.createEntityManager();
        TypedQuery<EcclesFacility> query;
        EcclesFacility res;
        try {
            if (localHostId != null) {
                query = em.createQuery(
                        "from EcclesFacility f where f.localHostId = :localHostId",
                        EcclesFacility.class);
                query.setParameter("localHostId", localHostId);
                res = getFirst(query, "hostId", localHostId);
                if (res != null) {
                    return res;
                }
            }
            if (facilityName != null) {
                query = em.createQuery(
                        "from EcclesFacility f where f.facilityName = :facilityName",
                        EcclesFacility.class);
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
                        "from EcclesFacility f where f.address = :ipAddress or " +
                        "f.address = :fqdn or f.address = :hostName",
                        EcclesFacility.class);
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

    private EcclesFacility getFirst(TypedQuery<EcclesFacility> query, String key, String keyValue) 
            throws ConfigurationException {
        List<EcclesFacility> list = query.getResultList();
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
            TypedQuery<EcclesFacility> query = em.createQuery(
                    "from EcclesFacility", EcclesFacility.class);
            return new ArrayList<FacilityConfig>(query.getResultList());
        } finally {
            em.close();
        }
    }
}
