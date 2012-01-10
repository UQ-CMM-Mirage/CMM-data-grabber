package au.edu.uq.cmm.paul.status;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import au.edu.uq.cmm.aclslib.proxy.AclsFacilityEvent;
import au.edu.uq.cmm.aclslib.proxy.AclsFacilityEventListener;
import au.edu.uq.cmm.aclslib.proxy.AclsLoginEvent;
import au.edu.uq.cmm.aclslib.proxy.AclsLogoutEvent;
import au.edu.uq.cmm.aclslib.proxy.AclsProxy;
import au.edu.uq.cmm.aclslib.server.FacilityConfig;
import au.edu.uq.cmm.paul.PaulException;
import au.edu.uq.cmm.paul.grabber.FileGrabber;

/**
 * This class represents the session state of the facilities as 
 * captured by the ACLS proxy.
 * 
 * @author scrawley
 */
public class FacilityStatusManager implements AclsFacilityEventListener {
    private static final Logger LOG = Logger.getLogger(FileGrabber.class);
    // FIXME - the facility statuses need to be persisted.
    private AclsProxy proxy;
    private Map<String, Facility> facilities = 
            new TreeMap<String, Facility>();
    private EntityManagerFactory entityManagerFactory;

    public FacilityStatusManager(AclsProxy proxy) {
        this.proxy = proxy;
        this.proxy.addListener(this);
        this.entityManagerFactory = Persistence.createEntityManagerFactory(
                "au.edu.uq.cmm.paul.facility.jpa");
    }

    public void eventOccurred(AclsFacilityEvent event) {
        synchronized (facilities) {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            try {
                entityManager.getTransaction().begin();
                String facilityId = event.getFacilityId();
                Facility facility = getFacility(entityManager, facilityId);
                if (facility == null) {
                    LOG.error("No facility found for facility id " + facilityId);
                    return;
                }
                if (event instanceof AclsLoginEvent) {
                    FacilitySession details = new FacilitySession(
                            event.getUserName(), event.getAccount(), facility, 
                            new Date());
                    facility.addSession(details);
                } else if (event instanceof AclsLogoutEvent) {
                    FacilitySession details = facility.currentSession();
                    if (details == null ||
                            !details.getUserName().equals(event.getUserName()) ||
                            !details.getAccount().equals(event.getAccount())) {
                        details = new FacilitySession(
                                event.getUserName(), event.getAccount(), facility, 
                                new Date(0L));
                        facility.addSession(details);
                    }
                    details.setLogoutTime(new Date());
                }
                entityManager.persist(facility);
                entityManager.getTransaction().commit();
            } finally {
                entityManager.close();
            }
        }
    }

    private Facility getFacility(EntityManager entityManager, String facilityId) {
        TypedQuery<Facility> query = entityManager.createQuery(
                "select f from Facility where f.facilityId = ?1", Facility.class);
        query.setParameter(1, facilityId);
        List<Facility> res = query.getResultList();
        if (res.size() == 0) {
            return null;
        } else if (res.size() == 1) {
            return res.get(0);
        } else {
            throw new PaulException("Duplicate facility entries");
        }
    }

    public FacilitySession getLoginDetails(FacilityConfig facility, long timestamp) {
        Facility status = facilities.get(facility.getFacilityId());
        if (status == null) {
            LOG.error("No status record for facility " + facility.getFacilityId());
            return null;
        }
        return status.getLoginDetails(timestamp);
    }
    
    public Collection<Facility> getSnapshot() {
        return Collections.unmodifiableCollection(facilities.values());
    }
}
