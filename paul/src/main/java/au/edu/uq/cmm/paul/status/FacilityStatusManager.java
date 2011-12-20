package au.edu.uq.cmm.paul.status;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import au.edu.uq.cmm.aclslib.proxy.AclsFacilityEvent;
import au.edu.uq.cmm.aclslib.proxy.AclsFacilityEventListener;
import au.edu.uq.cmm.aclslib.proxy.AclsLoginEvent;
import au.edu.uq.cmm.aclslib.proxy.AclsLogoutEvent;
import au.edu.uq.cmm.aclslib.proxy.AclsProxy;
import au.edu.uq.cmm.aclslib.server.Facility;
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
    private Map<String, FacilityStatus> statuses = 
            new TreeMap<String, FacilityStatus>();

    public FacilityStatusManager(AclsProxy proxy) {
        this.proxy = proxy;
        this.proxy.addListener(this);
    }

    public void eventOccurred(AclsFacilityEvent event) {
        synchronized (statuses) {
            Facility facility = event.getFacility();
            String facilityId = facility.getFacilityId();
            FacilityStatus status = statuses.get(facilityId);
            if (status == null) {
                status = new FacilityStatus(event.getFacility());
                statuses.put(facilityId, status);
            }
            if (event instanceof AclsLoginEvent) {
                FacilitySession details = new FacilitySession(
                        event.getUserName(), event.getAccount(), facility, 
                        System.currentTimeMillis());
                status.addSession(details);
            } else if (event instanceof AclsLogoutEvent) {
                FacilitySession details = status.currentSession();
                if (!details.getUserName().equals(event.getUserName()) ||
                        !details.getAccount().equals(event.getAccount())) {
                    details = new FacilitySession(
                            event.getUserName(), event.getAccount(), facility, 
                            System.currentTimeMillis());
                    status.addSession(details);
                }
                details.setLogoutTime(System.currentTimeMillis());
            }
        }
    }

    public FacilitySession getLoginDetails(Facility facility, long timestamp) {
        FacilityStatus status = statuses.get(facility.getFacilityId());
        if (status == null) {
            LOG.error("No status record for facility " + facility.getFacilityId());
            return null;
        }
        return status.getLoginDetails(timestamp);
    }
    
    public Collection<FacilityStatus> getSnapshot() {
        return Collections.unmodifiableCollection(statuses.values());
    }
}
