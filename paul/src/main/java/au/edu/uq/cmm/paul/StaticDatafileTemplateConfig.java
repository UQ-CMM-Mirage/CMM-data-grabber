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

package au.edu.uq.cmm.paul;

public class StaticDatafileTemplateConfig implements DatafileTemplateConfig {

    private boolean optional;
    private String mimeType;
    private String filePattern;
    private String suffix;
    private int minimumSize;
    
    public StaticDatafileTemplateConfig() {
        super();
    }

    public String getFilePattern() {
        return filePattern;
    }

    public String getMimeType() {
        return mimeType;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getMinimumSize() {
        return minimumSize;
    }

    public void setMinimumSize(int minimumSize) {
        this.minimumSize = minimumSize;
    }

}
