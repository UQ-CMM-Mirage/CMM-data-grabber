package au.edu.uq.cmm.paul.status;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import au.edu.uq.cmm.aclslib.config.DatafileTemplateConfig;

@Entity
@Table(name = "datafile_templates")
public class DatafileTemplate implements DatafileTemplateConfig {

    private boolean optional;
    private String mimeType;
    private String filePattern;
    private String suffix;
    private Long id;
    
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
}
