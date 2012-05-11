/*
* Copyright 2012, CMM, University of Queensland.
*
* This file is part of Eccles.
*
* Eccles is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Eccles is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Eccles. If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.uq.cmm.eccles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import au.edu.uq.cmm.aclslib.authenticator.AclsLoginDetails;

@Entity
@Table(name = "users",
       uniqueConstraints={
            @UniqueConstraint(columnNames={"userName"})})
public class UserDetails {
    private String userName;
    private Long id;
    private String emailAddress;
    private String humanReadableName;
    private String orgName;
    private Set<String> accounts;
    private Map<String, String> certifications;
    private boolean onsiteAssist; 
    private String digest;
    private long seed;
    
    
    public UserDetails() {
        super();
    }

    public UserDetails(String userName, String emailAddress,
            AclsLoginDetails details, long seed, String digest) {
        super();
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.digest = digest;
        this.seed = seed;
        this.orgName = details.getOrgName();
        this.humanReadableName = details.getHumanReadableName();
        this.onsiteAssist = details.isOnsiteAssist();
        this.accounts = new HashSet<String>(details.getAccounts());
        this.certifications = new HashMap<String, String>();
        this.certifications.put(details.getFacilityName(), details.getCertification().toString());
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

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @CollectionTable(name="accounts",joinColumns=@JoinColumn(name="account_id"))
    @ElementCollection()
    public Set<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<String> accounts) {
        this.accounts = accounts;
    }

    @ElementCollection
    @JoinColumn(name="user_id", insertable=false, updatable=false)
    @MapKeyColumn(name="facility_name")
    @CollectionTable(name="certifications", joinColumns=@JoinColumn(name="user_id"))
    public Map<String, String> getCertifications() {
        return certifications;
    }

    public void setCertifications(Map<String, String> certifications) {
        this.certifications = certifications;
    }

    public boolean isOnsiteAssist() {
        return onsiteAssist;
    }

    public void setOnsiteAssist(boolean onsiteAssist) {
        this.onsiteAssist = onsiteAssist;
    }
}
