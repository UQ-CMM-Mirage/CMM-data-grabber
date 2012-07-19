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

package au.edu.uq.cmm.paul.grabber;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.paul.PaulException;


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
    private static final Logger LOG = LoggerFactory.getLogger(DatasetMetadata.class);
    
    private String userName;
    private Long facilityId;
    private String facilityName;
    private String accountName;
    private String sourceFilePathnameBase;
    private String facilityFilePathnameBase;
    private Date captureTimestamp;
    private Date updateTimestamp;
    private Date sessionStartTimestamp;
    private String metadataFilePathname;
    private Long id;
    private String sessionUuid;
    private String recordUuid;
    private String emailAddress;
    private String datasetHash;
    private List<DatafileMetadata> datafiles;
    
    
    public DatasetMetadata() {
        super();
    }
    
    public DatasetMetadata(String sourceFilePathnameBase, 
            String facilityFilePathnameBase,
            String metadataFilePathname, String userName, 
            String facilityName, Long facilityId,
            String accountName, String emailAddress, Date captureTimestamp,
            String sessionUuid, Date sessionStartTimestamp, 
            List<DatafileMetadata> datafiles) {
        super();
        this.sourceFilePathnameBase = sourceFilePathnameBase;
        this.facilityFilePathnameBase = facilityFilePathnameBase;
        this.metadataFilePathname = metadataFilePathname;
        this.userName = userName;
        this.facilityName = facilityName;
        this.facilityId = facilityId;
        this.accountName = accountName;
        this.emailAddress = emailAddress;
        this.captureTimestamp = captureTimestamp;
        this.updateTimestamp = captureTimestamp;
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
    
    @JsonIgnore
    @Transient
    public Date getFirstFileTimestamp() {
        if (datafiles.isEmpty()) {
            return null;
        } else {
            Date min = null;
            for (DatafileMetadata datafile : datafiles) {
                if (min == null) {
                    min = datafile.getFileWriteTimestamp();
                } else {
                    Date tmp = datafile.getFileWriteTimestamp();
                    if (tmp != null && tmp.getTime() < min.getTime()) {
                        min = tmp;
                    }
                }
            }
            return min;
        }
    }

    @JsonIgnore
    @Transient
    public Date getLastFileTimestamp() {
        if (datafiles.isEmpty()) {
            return null;
        } else {
            Date max = null;
            for (DatafileMetadata datafile : datafiles) {
                if (max == null) {
                    max = datafile.getFileWriteTimestamp();
                } else {
                    Date tmp = datafile.getFileWriteTimestamp();
                    if (tmp != null && tmp.getTime() > max.getTime()) {
                        max = tmp;
                    }
                }
            }
            return max;
        }
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

    public String getFacilityFilePathnameBase() {
        return facilityFilePathnameBase;
    }

    public void setFacilityFilePathnameBase(String facilityFilePathnameBase) {
        this.facilityFilePathnameBase = facilityFilePathnameBase;
    }

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @OrderBy(value="fileWriteTimestamp ASC")
    @JoinColumn(name="datafile_id")
    public List<DatafileMetadata> getDatafiles() {
        return datafiles;
    }

    public void setDatafiles(List<DatafileMetadata> datafiles) {
        this.datafiles = datafiles;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public String getDatasetHash() {
        return datasetHash;
    }

    public void setDatasetHash(String hash) {
        this.datasetHash = (hash != null && hash.isEmpty()) ? null : hash;
    }
    
    @Transient 
    public String getCombinedDatafileHash() {
        String hash = null;
        for (DatafileMetadata datafile : datafiles) {
            try {
                hash = HashUtils.combineHashes(hash, datafile.getDatafileHash());
            } catch (InvalidHashException ex) {
                LOG.warn("Skipped an invalid hash for datafile " + datafile);
            }
        }
        return hash;
    }
    
    public void checkHashes(boolean includeDatasetHash) throws IncorrectHashException {
        for (DatafileMetadata datafile : datafiles) {
            datafile.checkDatafileHash();
        }
        if (includeDatasetHash && datasetHash != null) {
            String tmp = calculateDatasetHash();
            if (!datasetHash.equals(tmp)) {
                throw new IncorrectHashException("Dataset hash is incorrect", datasetHash, tmp);
            }
        }
    }
    
    public void updateDatasetHash() {
        setDatasetHash(calculateDatasetHash());
    }

    private String calculateDatasetHash() {
        StringWriter sw = new StringWriter();
        // Note that the hash excludes the 'datasetHash' field ...
        String savedHash = datasetHash;
        try {
            datasetHash = null;
            serialize(sw);
            byte[] data = sw.toString().getBytes("UTF-8");
            MessageDigest md = HashUtils.createDigester();
            md.update(data);
            byte[] hash = md.digest();
            return HashUtils.bytesToHexString(hash);
        } catch (IOException ex) {
            throw new PaulException("Impossible exception", ex);
        } finally {
            datasetHash = savedHash;
        }
    }

    public void serialize(Writer writer) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonFactory jf = new JsonFactory();
            JsonGenerator jg = jf.createJsonGenerator(writer);
            jg.useDefaultPrettyPrinter();
            mapper.writeValue(jg, this);
        } catch (JsonParseException ex) {
            throw new PaulException(ex);
        } catch (JsonMappingException ex) {
            throw new PaulException(ex);
        }
    }
    
    public String toString() {
        StringWriter sw = new StringWriter();
        try {
            serialize(sw);
        } catch (IOException ex) {
            throw new PaulException("Impossible exception", ex);
        }
        return sw.toString();
    }
}
