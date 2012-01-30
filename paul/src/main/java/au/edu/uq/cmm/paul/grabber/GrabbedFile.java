package au.edu.uq.cmm.paul.grabber;

import java.io.File;
import java.util.Date;

class GrabbedFile {

    private File baseFile;
    private File file;
    private File copiedFile;
    private Date copyTimestamp;
    private Date fileTimestamp;
    private String mimeType;
    

    public GrabbedFile(File baseFile, File file, String mimeType) {
        this.file = file;
        this.mimeType = mimeType;
        this.baseFile = baseFile;
    }

    public File getFile() {
        return file;
    }

    public File getCopiedFile() {
        return copiedFile;
    }

    public Date getCopyTimestamp() {
        return copyTimestamp;
    }

    public Date getFileTimestamp() {
        return fileTimestamp;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setCopiedFile(File copiedFile) {
        this.copiedFile = copiedFile;
    }

    public void setCopyTimestamp(Date copyTimestamp) {
        this.copyTimestamp = copyTimestamp;
    }

    public void setFileTimestamp(Date fileTimestamp) {
        this.fileTimestamp = fileTimestamp;
    }

    public File getBaseFile() {
        return baseFile;
    }

    public void setBaseFile(File baseFile) {
        this.baseFile = baseFile;
    }
}
