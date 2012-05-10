package au.edu.uq.cmm.paul;

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
import au.edu.uq.cmm.paul.status.Facility;

public class PaulFacilityMapper implements FacilityMapper {

    private EntityManagerFactory entityManagerFactory;
    

    public PaulFacilityMapper(EntityManagerFactory entityManagerFactory) {
        super();
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public FacilityConfig lookup(String localHostId, String facilityName,
            InetAddress clientAddr) {
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

    private Facility getFirst(TypedQuery<Facility> query, String key, String keyValue) {
        List<Facility> list = query.getResultList();
        if (list.size() == 0) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            throw new AssertionError(
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
}
