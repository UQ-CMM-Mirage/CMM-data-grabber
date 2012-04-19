package au.edu.uq.cmm.eccles;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import au.edu.uq.cmm.aclslib.config.FacilityConfig;

@Entity
@Table(name = "facilities",
       uniqueConstraints={
            @UniqueConstraint(columnNames={"facilityName"}),
            @UniqueConstraint(columnNames={"address"})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class EcclesFacility implements FacilityConfig {

    private Long id;
    private String address;
    private String accessName;
    private String accessPassword;
    private String driveName;
    private String facilityName;
    private String folderName;
    private String localHostId;
    private String facilityDescription;
    private boolean useFullScreen;
    private boolean useNetDrive;
    private boolean useTimer;

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getAccessName() {
        return accessName;
    }

    @Override
    public String getAccessPassword() {
        return accessPassword;
    }

    @Override
    public String getDriveName() {
        return driveName;
    }

    @Override
    public String getFacilityName() {
        return facilityName;
    }

    @Override
    public String getLocalHostId() {
        return localHostId;
    }

    @Override
    public String getFacilityDescription() {
        return facilityDescription;
    }

    @Override
    public String getFolderName() {
        return folderName;
    }

    @Override
    public boolean isUseFullScreen() {
        return useFullScreen;
    }

    @Override
    public boolean isUseNetDrive() {
        return useNetDrive;
    }

    @Override
    public boolean isUseTimer() {
        return useTimer;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setLocalHostId(String localHostId) {
        this.localHostId = localHostId;
    }

    public void setFacilityDescription(String facilityDescription) {
        this.facilityDescription = facilityDescription;
    }

    public void setUseFullScreen(boolean useFullScreen) {
        this.useFullScreen = useFullScreen;
    }

    public void setUseNetDrive(boolean useNetDrive) {
        this.useNetDrive = useNetDrive;
    }

    public void setUseTimer(boolean useTimer) {
        this.useTimer = useTimer;
    }
    
    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
