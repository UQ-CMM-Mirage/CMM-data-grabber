package au.edu.uq.cmm.paul.status;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import au.edu.uq.cmm.aclslib.server.Facility;

public class FacilitySession {
    private final String userName;
    private final String account;
    private final Facility facility;
    private final Date loginTime;
    private Date logoutTime;
    private final long sessionId;
    
    private static final AtomicLong sessionIdGenerator = new AtomicLong();
    
    public FacilitySession(String userName, String account, Facility facility,
            Date loginTime) {
        super();
        this.userName = userName;
        this.account = account;
        this.facility = facility;
        this.loginTime = loginTime;
        // FIXME - the sessionId should be allocated by the persistence layer.
        this.sessionId = sessionIdGenerator.incrementAndGet();
    }

    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getAccount() {
        return account;
    }

    public Facility getFacility() {
        return facility;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public long getSessionId() {
        return sessionId;
    }
}
