package au.edu.uq.cmm.eccles;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

/**
 * A FacilitySession records minimal details of an ACLS Facility sesssion.
 * 
 * @author scrawley
 */
@Entity
@Table(name = "SESSIONS",
       uniqueConstraints=@UniqueConstraint(columnNames={"sessionUuid"}))
public class FacilitySession {
    public static final String UNKNOWN = "unknown";
    
    private String userName;
    private String account;
    private String facilityName;
    private Date loginTime;
    private Date logoutTime;
    private Long id;
    private String sessionUuid;
    private String emailAddress;
    
    public FacilitySession() {
        super();
    }
    
    public FacilitySession(String facilityName) {
        this.facilityName = facilityName;
    }
    
    public FacilitySession(String userName, String account, String facilityName,
            String emailAddress, Date loginTime) {
        super();
        if (userName.isEmpty() || account.isEmpty() || 
                facilityName == null || loginTime == null) {
            throw new IllegalArgumentException("Empty or missing argument");
        }
        this.userName = userName;
        this.account = account;
        this.facilityName = facilityName;
        this.loginTime = loginTime;
        this.emailAddress = emailAddress;
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

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(String sessionUuid) {
        this.sessionUuid = sessionUuid;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public static FacilitySession makeDummySession(String facilityName, Date now) {
        FacilitySession res = new FacilitySession(
                FacilitySession.UNKNOWN, FacilitySession.UNKNOWN, 
                facilityName, null, now);
        res.setSessionUuid(FacilitySession.UNKNOWN);
        return res;
    }
}
