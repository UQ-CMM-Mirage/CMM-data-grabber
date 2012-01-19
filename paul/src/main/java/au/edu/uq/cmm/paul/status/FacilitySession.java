package au.edu.uq.cmm.paul.status;

import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

/**
 * A FacilitySession records minimal details of an ACLS Facility sesssion.
 * 
 * @author scrawley
 */
@Entity
@Table(name = "SESSIONS")
public class FacilitySession {
    private String userName;
    private String account;
    private Facility facility;
    private Date loginTime;
    private Date logoutTime;
    private Long id;
    private String sessionUuid;
    
    public FacilitySession() {
        super();
    }
    
    public FacilitySession(String userName, String account, Facility facility,
            Date loginTime) {
        super();
        if (userName.isEmpty() || account.isEmpty() || 
                facility == null || loginTime == null) {
            throw new IllegalArgumentException("Empty or missing argument");
        }
        this.userName = userName;
        this.account = account;
        this.facility = facility;
        this.loginTime = loginTime;
        this.sessionUuid = UUID.randomUUID().toString();
    }

    /**
     * Get the logout timestamp.
     * @return the logout timestamp or ({@literal null} if the session is still
     *     notionally in progress.
     */
    public Date getLogoutTime() {
        return logoutTime;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getAccount() {
        return account;
    }

    @ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="facility_id")
    public Facility getFacility() {
        return facility;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getLoginTime() {
        return loginTime;
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(String sessionUuid) {
        this.sessionUuid = sessionUuid;
    }
}
