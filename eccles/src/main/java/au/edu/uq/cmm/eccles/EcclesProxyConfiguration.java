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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import au.edu.uq.cmm.aclslib.config.ACLSProxyConfiguration;

@Entity
@Table(name = "CONFIGURATION")
public class EcclesProxyConfiguration implements ACLSProxyConfiguration {
    private Long id;
    private int proxyPort;
    private String serverHost;
    private int serverPort;
    private boolean useProject;
    private String proxyHost;
    private String dummyFacilityName;
    private String dummyFacilityHostId;
    private boolean allowUnknownClients;
    private Set<String> trustedAddresses = Collections.emptySet();
    private Set<InetAddress> trustedInetAddresses = Collections.emptySet();
    
    
    public EcclesProxyConfiguration() {
        super();
    }

    public static ACLSProxyConfiguration load(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("from EcclesProxyConfiguration", 
                    EcclesProxyConfiguration.class).getSingleResult();
        } finally {
            em.close();
        }
    }
    
    @Override
    public int getProxyPort() {
        return proxyPort;
    }

    @Override
    public String getServerHost() {
        return serverHost;
    }

    @Override
    public int getServerPort() {
        return serverPort;
    }

    @Override
    public boolean isUseProject() {
        return useProject;
    }

    @Override
    public String getProxyHost() {
        return proxyHost;
    }

    @Override
    public String getDummyFacilityName() {
        return dummyFacilityName;
    }

    @Override
    public String getDummyFacilityHostId() {
        return dummyFacilityHostId;
    }

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDummyFacilityHostId(String dummyFacilityHostId) {
        this.dummyFacilityHostId = dummyFacilityHostId;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setUseProject(boolean useProject) {
        this.useProject = useProject;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setDummyFacilityName(String dummyFacilityName) {
        this.dummyFacilityName = dummyFacilityName;
    }

    public boolean isAllowUnknownClients() {
        return allowUnknownClients;
    }

    public void setAllowUnknownClients(boolean allowUnknownClients) {
        this.allowUnknownClients = allowUnknownClients;
    }
    
    @CollectionTable(name="trusted_addresses",joinColumns=@JoinColumn(name="addr_id"))
    @ElementCollection()
    public Set<String> getTrustedAddresses() {
        return trustedAddresses;
    }

    public void setTrustedAddresses(Set<String> trustedAddresses) 
            throws UnknownHostException {
        this.trustedAddresses = trustedAddresses;
        this.trustedInetAddresses = new HashSet<InetAddress>(trustedAddresses.size());
        for (String address : trustedAddresses) {
            trustedInetAddresses.add(InetAddress.getByName(address));
        }
    }

    @Transient
    public Set<InetAddress> getTrustedInetAddresses() {
        return trustedInetAddresses;
    }

}
