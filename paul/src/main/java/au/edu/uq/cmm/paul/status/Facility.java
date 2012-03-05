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

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import au.edu.uq.cmm.aclslib.config.DatafileTemplateConfig;
import au.edu.uq.cmm.aclslib.config.FacilityConfig;

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
            @UniqueConstraint(columnNames={"address"})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Facility implements FacilityConfig {
    public enum Status {
        ENABLED, DISABLED, MISCONFIGURED, DUMMY
    }

    private List<FacilitySession> sessions = new ArrayList<FacilitySession>();
    private Long id;
    
    private boolean useFullScreen;
    private String driveName;
    private String accessPassword;
    private String accessName;
    private String folderName;
    private String facilityName;
    private boolean useTimer;
    private String facilityDescription;
    private boolean dummy;
    private boolean useFileLocks = true;
    private boolean caseInsensitive;
    private int fileSettlingTime;
    private String address;
    private List<DatafileTemplate> datafileTemplates;
    private Status status = Status.ENABLED;
    private String message = "";
    

    public Facility() {
        super();
    }

    public Facility(FacilityConfig facilityConfig) {
        useFullScreen = facilityConfig.isUseFullScreen();
        driveName = facilityConfig.getDriveName();
        accessPassword = facilityConfig.getAccessPassword();
        accessName = facilityConfig.getAccessName();
        facilityName = facilityConfig.getFacilityName();
        folderName = facilityConfig.getFolderName();
        useTimer = facilityConfig.isUseTimer();
        facilityDescription = facilityConfig.getFacilityDescription();
        dummy = facilityConfig.isDummy();
        useFileLocks = facilityConfig.isUseFileLocks();
        fileSettlingTime = facilityConfig.getFileSettlingTime();
        caseInsensitive = facilityConfig.isCaseInsensitive();
        address = facilityConfig.getAddress();
        datafileTemplates = new ArrayList<DatafileTemplate>();
        for (DatafileTemplateConfig template : facilityConfig.getDatafileTemplates()) {
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

    public boolean isDummy() {
        return dummy;
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

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
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

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name="datafile_id")
    public List<DatafileTemplate> getDatafileTemplates() {
        return datafileTemplates;
    }

    public void setDatafileTemplates(List<DatafileTemplate> templates) {
        this.datafileTemplates = templates;
    }
    
    public Status getStatus() {
        Logger.getLogger(this.getClass()).error("Got " + status);
        return status;
    }

    public void setStatus(Status status) {
        Logger.getLogger(this.getClass()).error("Setting status to " + status);
        this.status = status;
    }

    @JsonIgnore
    @Transient
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public Long getId() {
        return id;
    }
    
    public synchronized void addSession(FacilitySession session) {
        sessions.add(session);
    }

    @JsonIgnore
    @Transient
    public synchronized FacilitySession getCurrentSession() {
        return (sessions.size() == 0) ? null : sessions.get(sessions.size() - 1);
    }
    
    @JsonIgnore
    @Transient
    public synchronized boolean isInUse() {
        return sessions.size() > 0 && getCurrentSession().getLogoutTime() == null;
    }

    public synchronized FacilitySession getLoginDetails(long timestamp) {
        for (int i = sessions.size() - 1; i >= 0; i--) {
            FacilitySession session = sessions.get(i);
            if (session.getLoginTime().getTime() <= timestamp && 
                    (session.getLogoutTime() == null || 
                     session.getLogoutTime().getTime() >= timestamp)) {
                return session;
            }
        }
        return null;
    }

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="session_id")
    public List<FacilitySession> getSessions() {
        return sessions;
    }

    public void setSessions(List<FacilitySession> sessions) {
        this.sessions = sessions;
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
}
