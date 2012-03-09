package au.edu.uq.cmm.paul.status;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "users",
       uniqueConstraints={
            @UniqueConstraint(columnNames={"userName"})})
public class UserDetails {
    private String userName;
    private Long id;
    private String emailAddress;
    private String humanReadableName;
    
    
    public UserDetails() {
        super();
    }

    public UserDetails(String userName, String emailAddress,
            String humanReadableName) {
        super();
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.humanReadableName = humanReadableName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getHumanReadableName() {
        return humanReadableName;
    }

    public void setHumanReadableName(String humanReadableName) {
        this.humanReadableName = humanReadableName;
    }
    
    
}
