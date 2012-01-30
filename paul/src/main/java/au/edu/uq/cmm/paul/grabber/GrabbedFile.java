package au.edu.uq.cmm.paul.grabber;

import java.io.File;
import java.util.Date;

import au.edu.uq.cmm.paul.status.DatafileTemplate;

/**
 * This class holds the details of a Datafile that we have grabbed, 
 * or are going to grab.
 * 
 * @author scrawley
 */
class GrabbedFile {

    private File baseFile;
    private File file;
    private File copiedFile;
    private Date copyTimestamp;
    private Date fileTimestamp;
    private DatafileTemplate template;
    

    public GrabbedFile(File baseFile, File file, DatafileTemplate template) {
        this.file = file;
        this.template = template;
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

    public DatafileTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DatafileTemplate template) {
        this.template = template;
    }
}
