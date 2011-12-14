package au.edu.uq.cmm.paul.status;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import au.edu.uq.cmm.aclslib.proxy.AclsFacilityEvent;
import au.edu.uq.cmm.aclslib.proxy.AclsFacilityEventListener;
import au.edu.uq.cmm.aclslib.proxy.AclsLoginEvent;
import au.edu.uq.cmm.aclslib.proxy.AclsLogoutEvent;
import au.edu.uq.cmm.aclslib.proxy.AclsProxy;
import au.edu.uq.cmm.aclslib.server.Facility;

public class FacilityStatusManager implements AclsFacilityEventListener {
    // FIXME - the facility statuses need to be persisted.
    private AclsProxy proxy;
    private Map<String, FacilityStatus> facilities = 
            new HashMap<String, FacilityStatus>();

    public FacilityStatusManager(AclsProxy proxy) {
        this.proxy = proxy;
        this.proxy.addListener(this);
    }

    public void eventOccurred(AclsFacilityEvent event) {
        synchronized (facilities) {
            Facility facility = event.getFacility();
            String facilityId = facility.getFacilityId();
            FacilityStatus status = facilities.get(facilityId);
            if (status == null) {
                status = new FacilityStatus(event.getFacility());
                facilities.put(facilityId, status);
            }
            if (event instanceof AclsLoginEvent) {
                status.setUser(event.getUserName());
                status.setAccount(event.getAccount());
                status.setLastLogin(new Date());
                status.setInUse(true);
            } else if (event instanceof AclsLogoutEvent) {
                status.setUser(event.getUserName());
                status.setAccount(event.getAccount());
                status.setLastLogout(new Date());
                status.setInUse(false);
            }
        }
    }
}
