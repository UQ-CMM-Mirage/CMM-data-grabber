package au.edu.uq.cmm.paul.status;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import au.edu.uq.cmm.aclslib.config.FacilityConfig;
import au.edu.uq.cmm.paul.DatafileTemplateConfig;
import au.edu.uq.cmm.paul.GrabberFacilityConfig;
import au.edu.uq.cmm.paul.status.FacilityStatusManager.FacilityStatus;

/**
 * The Paul implementation of FacilityConfig persists the configuration data
 * using Hibernate.  It also tracks the login sessions for a facility.
 * 
 * @author scrawley
 */
@Entity
@Table(name = "facilities",
       uniqueConstraints={
            @UniqueConstraint(columnNames={"facilityName"}),
            @UniqueConstraint(columnNames={"address", "localHostId"}),
            @UniqueConstraint(columnNames={"localHostId"})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Facility implements FacilityConfig {
    private Long id;
    
    private boolean useFullScreen;
    private String driveName;
    private String accessPassword;
    private String accessName;
    private String folderName;
    private String facilityName;
    private String localHostId;
    private boolean useTimer;
    private String facilityDescription;
    private boolean useFileLocks = true;
    private boolean caseInsensitive;
    private int fileSettlingTime;
    private String address;
    private List<DatafileTemplate> datafileTemplates;
    private boolean disabled;
    private FacilityStatus status;
    

    public Facility() {
        super();
    }

    public Facility(GrabberFacilityConfig facility) {
        useFullScreen = facility.isUseFullScreen();
        driveName = facility.getDriveName();
        accessPassword = facility.getAccessPassword();
        accessName = facility.getAccessName();
        facilityName = facility.getFacilityName();
        localHostId = facility.getLocalHostId();
        folderName = facility.getFolderName();
        useTimer = facility.isUseTimer();
        facilityDescription = facility.getFacilityDescription();
        useFileLocks = facility.isUseFileLocks();
        fileSettlingTime = facility.getFileSettlingTime();
        caseInsensitive = facility.isCaseInsensitive();
        address = facility.getAddress();
        datafileTemplates = new ArrayList<DatafileTemplate>();
        for (DatafileTemplateConfig template : facility.getDatafileTemplates()) {
            datafileTemplates.add(new DatafileTemplate(template));
        }
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccessName() {
        return accessName;
    }

    public String getAccessPassword() {
        return accessPassword;
    }

    public String getDriveName() {
        return driveName;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getFacilityDescription() {
        return facilityDescription;
    }

    public String getFolderName() {
        return folderName;
    }

    public boolean isUseFullScreen() {
        return useFullScreen;
    }

    @JsonIgnore
    @Transient
    public boolean isUseNetDrive() {
        return driveName != null;
    }

    public boolean isUseTimer() {
        return useTimer;
    }

    public void setAccessName(String accessName) {
        this.accessName = accessName;
    }

    public void setAccessPassword(String accessPassword) {
        this.accessPassword = accessPassword;
    }

    public void setDriveName(String driveName) {
        this.driveName = driveName;
    }

    public void setFacilityName(String name) {
        this.facilityName = name;
    }

    public String getLocalHostId() {
        return localHostId;
    }

    public void setLocalHostId(String localHostId) {
        this.localHostId = localHostId;
    }

    public void setFacilityDescription(String desc) {
        this.facilityDescription = desc;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setUseFullScreen(boolean useFullScreen) {
        this.useFullScreen = useFullScreen;
    }

    public void setUseTimer(boolean useTimer) {
        this.useTimer = useTimer;
    }

    public boolean isUseFileLocks() {
        return this.useFileLocks;
    }

    public void setUseFileLocks(boolean useFileLocks) {
        this.useFileLocks = useFileLocks;
    }
    
    public int getFileSettlingTime() {
        return this.fileSettlingTime;
    }

    public void setFileSettlingTime(int fileSettlingTime) {
        this.fileSettlingTime = fileSettlingTime;
    }

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="datafile_id")
    public List<DatafileTemplate> getDatafileTemplates() {
        return datafileTemplates;
    }

    public void setDatafileTemplates(List<DatafileTemplate> templates) {
        this.datafileTemplates = templates;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public Long getId() {
        return id;
    }

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    public void setId(Long id) {
        this.id = id;
    }

    

    @JsonIgnore
    @Transient
    public FacilityStatus getStatus() {
        return status;
    }

    public void setStatus(FacilityStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Facility [id=" + id + ", useFullScreen=" + useFullScreen
                + ", driveName=" + driveName + ", accessPassword="
                + accessPassword + ", accessName=" + accessName
                + ", folderName=" + folderName + ", facilityName="
                + facilityName + ", localHostId=" + localHostId + ", useTimer="
                + useTimer + ", facilityDescription=" + facilityDescription
                + ", useFileLocks=" + useFileLocks + ", caseInsensitive="
                + caseInsensitive + ", fileSettlingTime=" + fileSettlingTime
                + ", address=" + address + ", datafileTemplates="
                + datafileTemplates + ", disabled=" + disabled
                + ", status=" + status + "]";
    }
}
