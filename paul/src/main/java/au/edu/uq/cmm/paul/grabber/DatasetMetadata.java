package au.edu.uq.cmm.paul.grabber;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;


/**
 * This class represents the administrative metadata for a dataset captured
 * by the FileGrabber.
 * 
 * @author scrawley
 */
@Entity
@Table(name = "DATASET_METADATA",
        uniqueConstraints=@UniqueConstraint(columnNames={"recordUuid"}))
public class DatasetMetadata {
    private String userName;
    private String facilityName;
    private String accountName;
    private String sourceFilePathnameBase;
    private Date captureTimestamp;
    private Date sessionStartTimestamp;
    private String metadataFilePathname;
    private Long id;
    private String sessionUuid;
    private String recordUuid;
    private String emailAddress;
    private List<DatafileMetadata> datafiles;
    
    
    public DatasetMetadata() {
        super();
    }
    
    public DatasetMetadata(String sourceFilePathnameBase, 
            String metadataFilePathname, String userName, String facilityName,
            String accountName, String emailAddress, Date captureTimestamp,
            String sessionUuid, Date sessionStartTimestamp,
            List<DatafileMetadata> datafiles) {
        super();
        this.sourceFilePathnameBase = sourceFilePathnameBase;
        this.metadataFilePathname = metadataFilePathname;
        this.userName = userName;
        this.facilityName = facilityName;
        this.accountName = accountName;
        this.emailAddress = emailAddress;
        this.captureTimestamp = captureTimestamp;
        this.sessionStartTimestamp = sessionStartTimestamp;
        this.sessionUuid = sessionUuid;
        this.recordUuid = UUID.randomUUID().toString();
        this.datafiles = datafiles;
    }
    
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @JsonIgnore
    public Long getId() {
        return id;
    }
    
    public String getUserName() {
        return userName;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getAccountName() {
        return accountName;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCaptureTimestamp() {
        return captureTimestamp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getSessionStartTimestamp() {
        return sessionStartTimestamp;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setCaptureTimestamp(Date captureTimestamp) {
        this.captureTimestamp = captureTimestamp;
    }

    public void setSessionStartTimestamp(Date sessionStartTimestamp) {
        this.sessionStartTimestamp = sessionStartTimestamp;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(String uuid) {
        this.sessionUuid = uuid;
    }

    public String getRecordUuid() {
        return recordUuid;
    }

    public void setRecordUuid(String recordUuid) {
        this.recordUuid = recordUuid;
    }

    public String getMetadataFilePathname() {
        return metadataFilePathname;
    }

    public void setMetadataFilePathname(String metadataFilePathname) {
        this.metadataFilePathname = metadataFilePathname;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSourceFilePathnameBase() {
        return sourceFilePathnameBase;
    }

    public void setSourceFilePathnameBase(String sourceFilePathnameBase) {
        this.sourceFilePathnameBase = sourceFilePathnameBase;
    }

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="datafile_id")
    public List<DatafileMetadata> getDatafiles() {
        return datafiles;
    }

    public void setDatafiles(List<DatafileMetadata> datafiles) {
        this.datafiles = datafiles;
    }
}
