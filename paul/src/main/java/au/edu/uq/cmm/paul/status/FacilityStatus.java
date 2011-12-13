package au.edu.uq.cmm.paul.status;

import java.util.Date;

import au.edu.uq.cmm.aclslib.server.Facility;

public class FacilityStatus {
    private Facility facility;
    private String user;
    private String account;
    private Date lastLogin;
    private Date lastLogout;
    private boolean inUse;
    
    public FacilityStatus(Facility facility) {
        super();
        this.facility = facility;
    }
    
    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }
    
    public Facility getFacility() {
        return facility;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getLastLogout() {
        return lastLogout;
    }

    public void setLastLogout(Date lastLogout) {
        this.lastLogout = lastLogout;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }
}
