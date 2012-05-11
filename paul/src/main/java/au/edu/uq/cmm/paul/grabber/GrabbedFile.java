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
