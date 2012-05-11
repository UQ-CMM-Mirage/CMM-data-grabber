/*
* Copyright 2012, CMM, University of Queensland.
*
* This file is part of Eccles.
*
* Eccles is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Eccles is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Eccles. If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.uq.cmm.eccles;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnore;

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

    @JsonIgnore
    @Transient
    public boolean isUseNetDrive() {
        return driveName != null;
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
