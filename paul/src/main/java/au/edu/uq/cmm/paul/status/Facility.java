/*
* Copyright 2012, CMM, University of Queensland.
*
* This file is part of Paul.
*
* Paul is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Paul is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Paul. If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.uq.cmm.paul.status;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import au.edu.uq.cmm.paul.GrabberFacilityConfig.FileArrivalMode;

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
    private boolean multiplexed = false;
    private boolean userOperated = true;

    private FileArrivalMode fileArrivalMode;
    

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
        multiplexed = facility.isMultiplexed();
        userOperated = facility.isUserOperated();
        fileArrivalMode = facility.getFileArrivalMode();
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

    public boolean isMultiplexed() {
        return multiplexed;
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

    public void setMultiplexed(boolean multiplexed) {
        this.multiplexed = multiplexed;
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

    @Enumerated(EnumType.STRING)
    public FileArrivalMode getFileArrivalMode() {
        return fileArrivalMode;
    }

    public void setFileArrivalMode(FileArrivalMode mode) {
        this.fileArrivalMode = mode == null ? FileArrivalMode.DIRECT : mode;
    }

    public boolean isUserOperated() {
        return userOperated;
    }

    public void setUserOperated(boolean userOperated) {
        this.userOperated = userOperated;
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
                + ", fileArrivalMode=" + fileArrivalMode
                + ", userOperated=" + userOperated
                + ", multiplexed=" + multiplexed + ", status=" + status + "]";
    }
}
