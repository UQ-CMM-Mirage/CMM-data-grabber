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

import java.io.File;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import au.edu.uq.cmm.aclslib.service.Service;
import au.edu.uq.cmm.paul.grabber.FileGrabber;
import au.edu.uq.cmm.paul.status.FacilityStatusManager.Status;

@Entity
@Table(name = "facility_status")
public class FacilityStatus {
    private Long facilityId;
    private Status status;
    private String message;
    private File localDirectory;
    private FileGrabber fileGrabber;
    private Date grabberHWMTimestamp;
    private Service fileGrabberService;
    
    public FacilityStatus() {
        this(null);
    }

    public FacilityStatus(Long facilityId) {
        super();
        this.facilityId = facilityId;
        this.message = "";
        this.status = Status.OFF;
    }

    @Transient
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Transient
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Id
    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getGrabberHWMTimestamp() {
        return grabberHWMTimestamp;
    }

    public void setGrabberHWMTimestamp(Date grabberHWMTimestamp) {
        this.grabberHWMTimestamp = grabberHWMTimestamp;
    }

    @Transient
    public FileGrabber getFileGrabber() {
        return this.fileGrabber;
    }

    public void setFileGrabber(FileGrabber fileGrabber) {
        this.fileGrabber = fileGrabber;
    }

    @Transient
    public Service getFileGrabberService() {
        return fileGrabberService;
    }

    public void setFileGrabberService(Service service) {
        this.fileGrabberService = service;
    }

    @Transient
    public File getLocalDirectory() {
        return localDirectory;
    }
    
    public void setLocalDirectory(File localDirectory) {
        this.localDirectory = localDirectory;
    }
    
    @Override
    public String toString() {
        return "FacilityStatus [facilityId=" + facilityId + ", status="
                + status + ", message=" + message + "]";
    }
}