package au.edu.uq.cmm.paul.grabber;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;


/**
 * This class represents the administrative metadata for a file captured
 * by the FileGrabber.
 * 
 * @author scrawley
 */
@Entity
@Table(name = "ADMIN_METADATA")
public class AdminMetadata {
    private String userName;
    private String facilityId;
    private String accountName;
    private String sourceFilePathname;
    private Date captureTimestamp;
    private Date fileWriteTimestamp;
    private Date sessionStartTimestamp;
    private long sessionId;
    private String capturedFilePathname;
    private long id;
    
    
    public AdminMetadata() {
        super();
    }
    
    public AdminMetadata(String sourceFilePathname, String capturedFilePathname, 
            String userName, String facilityId,
            String accountName, Date captureTimestamp, Date fileWriteTimestamp,
            long sessionId, Date sessionStartTimestamp) {
        super();
        this.sourceFilePathname = sourceFilePathname;
        this.capturedFilePathname = capturedFilePathname;
        this.userName = userName;
        this.facilityId = facilityId;
        this.accountName = accountName;
        this.captureTimestamp = captureTimestamp;
        this.fileWriteTimestamp = fileWriteTimestamp;
        this.sessionId = sessionId;
        this.sessionStartTimestamp = sessionStartTimestamp;
    }
    
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public Long getId() {
        return id;
    }
    
    public String getUserName() {
        return userName;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getAccountName() {
        return accountName;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCaptureTimestamp() {
        return captureTimestamp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getFileWriteTimestamp() {
        return fileWriteTimestamp;
    }

    public String getSourceFilePathname() {
        return sourceFilePathname;
    }

    public String getCapturedFilePathname() {
        return capturedFilePathname;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getSessionStartTimestamp() {
        return sessionStartTimestamp;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setSourceFilePathname(String sourceFilePathname) {
        this.sourceFilePathname = sourceFilePathname;
    }

    public void setCaptureTimestamp(Date captureTimestamp) {
        this.captureTimestamp = captureTimestamp;
    }

    public void setFileWriteTimestamp(Date fileWriteTimestamp) {
        this.fileWriteTimestamp = fileWriteTimestamp;
    }

    public void setSessionStartTimestamp(Date sessionStartTimestamp) {
        this.sessionStartTimestamp = sessionStartTimestamp;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public void setCapturedFilePathname(String capturedFilePathname) {
        this.capturedFilePathname = capturedFilePathname;
    }

    public void setId(long id) {
        this.id = id;
    }
}
