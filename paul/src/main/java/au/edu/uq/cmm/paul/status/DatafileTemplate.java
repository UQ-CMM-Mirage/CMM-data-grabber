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

import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import au.edu.uq.cmm.paul.DatafileTemplateConfig;

@Entity
@Table(name = "datafile_templates")
public class DatafileTemplate implements DatafileTemplateConfig {

    private boolean optional;
    private String mimeType;
    private String filePattern;
    private String suffix;
    private Long id;
    private Pattern compiledPattern;
    
    public DatafileTemplate() {
        super();
    }

    public DatafileTemplate(DatafileTemplateConfig template) {
        this.optional = template.isOptional();
        this.mimeType = template.getMimeType();
        this.filePattern = template.getFilePattern();
        this.suffix = template.getSuffix();
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

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public Pattern getCompiledFilePattern(boolean caseInsensitive) {
        if (compiledPattern == null) {
            compiledPattern = Pattern.compile(
                    filePattern, caseInsensitive ? Pattern.CASE_INSENSITIVE : 0);
        }
        return compiledPattern;
    }
}
