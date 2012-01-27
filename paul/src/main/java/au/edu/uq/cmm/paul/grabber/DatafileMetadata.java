package au.edu.uq.cmm.paul.grabber;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;


/**
 * This class represents the administrative metadata for a file captured
 * by the FileGrabber.
 * 
 * @author scrawley
 */
@Entity
@Table(name = "DATAFILE_METADATA")
public class DatafileMetadata {
    private String sourceFilePathname;
    private Date captureTimestamp;
    private Date fileWriteTimestamp;
    private String capturedFilePathname;
    private String mimeType;
    private Long id;    
    
    public DatafileMetadata() {
        super();
    }
    
    public DatafileMetadata(String sourceFilePathname, String capturedFilePathname, 
            Date captureTimestamp, Date fileWriteTimestamp, String mimeType) {
        super();
        this.sourceFilePathname = sourceFilePathname;
        this.capturedFilePathname = capturedFilePathname;
        this.captureTimestamp = captureTimestamp;
        this.fileWriteTimestamp = fileWriteTimestamp;
        this.mimeType = mimeType;
    }
    
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @JsonIgnore
    public Long getId() {
        return id;
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

    public void setSourceFilePathname(String sourceFilePathname) {
        this.sourceFilePathname = sourceFilePathname;
    }

    public void setCaptureTimestamp(Date captureTimestamp) {
        this.captureTimestamp = captureTimestamp;
    }

    public void setFileWriteTimestamp(Date fileWriteTimestamp) {
        this.fileWriteTimestamp = fileWriteTimestamp;
    }

    public void setCapturedFilePathname(String capturedFilePathname) {
        this.capturedFilePathname = capturedFilePathname;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
