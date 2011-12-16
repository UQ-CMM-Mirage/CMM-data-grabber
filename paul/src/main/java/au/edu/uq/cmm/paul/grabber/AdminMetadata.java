package au.edu.uq.cmm.paul.grabber;

/**
 * This class represents the administrative metadata for a file captured
 * by the FileGrabber.
 * 
 * @author scrawley
 */
public class AdminMetadata {
    private final String userName;
    private final String facilityId;
    private final String accountName;
    private final String sourceFilePathname;
    private final long captureTimestamp;
    private final long fileWriteTimestamp;
    private final String capturedFilePathname;
    
    public AdminMetadata(String sourceFilePathname, String capturedFilePathname, 
            String userName, String facilityId,
            String accountName, long captureTimestamp, long fileWriteTimestamp) {
        super();
        this.sourceFilePathname = sourceFilePathname;
        this.capturedFilePathname = capturedFilePathname;
        this.userName = userName;
        this.facilityId = facilityId;
        this.accountName = accountName;
        this.captureTimestamp = captureTimestamp;
        this.fileWriteTimestamp = fileWriteTimestamp;
    }
    
    public String getUserName() {
        return userName;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getAccountName() {
        return accountName;
    }

    public long getCaptureTimestamp() {
        return captureTimestamp;
    }

    public long getFileWriteTimestamp() {
        return fileWriteTimestamp;
    }
    
    public String getFilePathname() {
        return sourceFilePathname;
    }

    public String getSourceFilePathname() {
        return sourceFilePathname;
    }

    public String getCapturedFilePathname() {
        return capturedFilePathname;
    }
}
