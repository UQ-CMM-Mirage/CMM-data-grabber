package au.edu.uq.cmm.paul.status;

import au.edu.uq.cmm.aclslib.server.Facility;

public class FacilitySession {
    private final String userName;
    private final String account;
    private final Facility facility;
    private final long loginTime;
    private long logoutTime;
    
    public FacilitySession(String userName, String account, Facility facility,
            long loginTime) {
        super();
        this.userName = userName;
        this.account = account;
        this.facility = facility;
        this.loginTime = loginTime;
    }

    public long getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(long logoutTime) {
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

    public long getLoginTime() {
        return loginTime;
    }
}
