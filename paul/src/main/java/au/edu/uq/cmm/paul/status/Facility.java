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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import au.edu.uq.cmm.aclslib.server.FacilityConfig;
import au.edu.uq.cmm.paul.DynamicConfiguration;

@Entity
@Table(name = "facilities")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Facility implements FacilityConfig {
    private List<FacilitySession> sessions = new ArrayList<FacilitySession>();
    private Long id;
    private DynamicConfiguration configuration;
    
    private boolean useFullScreen;
    private String driveName;
    private String accessPassword;
    private String accessName;
    private String folderName;
    private String facilityId;
    private boolean useTimer;
    private String facilityName;
    private boolean dummy;
    private boolean useFileLocks = true;
    private int fileSettlingTime;
    

    public Facility() {
        super();
    }

    public Facility(FacilityConfig facilityConfig) {
        useFullScreen = facilityConfig.isUseFullScreen();
        driveName = facilityConfig.getDriveName();
        accessPassword = facilityConfig.getAccessPassword();
        accessName = facilityConfig.getAccessName();
        facilityId = facilityConfig.getFacilityId();
        folderName = facilityConfig.getFolderName();
        useTimer = facilityConfig.isUseTimer();
        facilityName = facilityConfig.getFacilityName();
        dummy = facilityConfig.isDummy();
        useFileLocks = facilityConfig.isUseFileLocks();
        fileSettlingTime = facilityConfig.getFileSettlingTime();
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

    public String getFacilityId() {
        return facilityId;
    }

    public String getFacilityName() {
        return facilityName;
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

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
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
    
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public Long getId() {
        return id;
    }
    
    public synchronized void addSession(FacilitySession session) {
        sessions.add(session);
    }

    public synchronized FacilitySession currentSession() {
        return (sessions.size() == 0) ? null : sessions.get(sessions.size() - 1);
    }
    
    @JsonIgnore
    @Transient
    public synchronized boolean isInUse() {
        return sessions.size() > 0 && 
                currentSession().getLogoutTime().getTime() == 0L;
    }

    public synchronized FacilitySession getLoginDetails(long timestamp) {
        for (int i = sessions.size() - 1; i >= 0; i++) {
            FacilitySession session = sessions.get(i);
            if (session.getLoginTime().getTime() <= timestamp && 
                    (session.getLogoutTime().getTime() == 0L || 
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

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name="configuration_id", updatable=false)
    public DynamicConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(DynamicConfiguration configuration) {
        this.configuration = configuration;
    }
}
