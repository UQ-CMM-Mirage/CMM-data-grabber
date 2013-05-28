/*
* Copyright 2012-2013, CMM, University of Queensland.
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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import au.edu.uq.cmm.aclslib.config.ACLSProxyConfiguration;

public abstract class EcclesProxyConfiguration implements ACLSProxyConfiguration {
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
    private EcclesFallbackMode fallbackMode = EcclesFallbackMode.USER_PASSWORD;
    
    
    public EcclesProxyConfiguration() {
        super();
    }

    public static EcclesProxyConfiguration load(EntityManagerFactory emf) {
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
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
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

    @Enumerated(EnumType.STRING)
    public EcclesFallbackMode getFallbackMode() {
		return fallbackMode;
	}

	public void setFallbackMode(EcclesFallbackMode fallbackMode) {
		this.fallbackMode = fallbackMode == null ? 
				fallbackMode : EcclesFallbackMode.USER_PASSWORD;
	}

	@Transient
    public Set<InetAddress> getTrustedInetAddresses() {
        return trustedInetAddresses;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (allowUnknownClients ? 1231 : 1237);
		result = prime
				* result
				+ ((dummyFacilityHostId == null) ? 0 : dummyFacilityHostId
						.hashCode());
		result = prime
				* result
				+ ((dummyFacilityName == null) ? 0 : dummyFacilityName
						.hashCode());
		result = prime * result
				+ ((fallbackMode == null) ? 0 : fallbackMode.hashCode());
		result = prime * result
				+ ((proxyHost == null) ? 0 : proxyHost.hashCode());
		result = prime * result + proxyPort;
		result = prime * result
				+ ((serverHost == null) ? 0 : serverHost.hashCode());
		result = prime * result + serverPort;
		result = prime
				* result
				+ ((trustedInetAddresses == null) ? 0 : trustedInetAddresses
						.hashCode());
		result = prime * result + (useProject ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EcclesProxyConfiguration other = (EcclesProxyConfiguration) obj;
		if (allowUnknownClients != other.allowUnknownClients)
			return false;
		if (dummyFacilityHostId == null) {
			if (other.dummyFacilityHostId != null)
				return false;
		} else if (!dummyFacilityHostId.equals(other.dummyFacilityHostId))
			return false;
		if (dummyFacilityName == null) {
			if (other.dummyFacilityName != null)
				return false;
		} else if (!dummyFacilityName.equals(other.dummyFacilityName))
			return false;
		if (fallbackMode != other.fallbackMode)
			return false;
		if (proxyHost == null) {
			if (other.proxyHost != null)
				return false;
		} else if (!proxyHost.equals(other.proxyHost))
			return false;
		if (proxyPort != other.proxyPort)
			return false;
		if (serverHost == null) {
			if (other.serverHost != null)
				return false;
		} else if (!serverHost.equals(other.serverHost))
			return false;
		if (serverPort != other.serverPort)
			return false;
		if (trustedInetAddresses == null) {
			if (other.trustedInetAddresses != null)
				return false;
		} else if (!trustedInetAddresses.equals(other.trustedInetAddresses))
			return false;
		if (useProject != other.useProject)
			return false;
		return true;
	}

}
