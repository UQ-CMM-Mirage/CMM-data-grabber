/*
* Copyright 2013, CMM, University of Queensland.
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
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

public interface ProxyConfiguration {

    int getProxyPort();

    String getServerHost();

    int getServerPort();

    boolean isUseProject();

    String getProxyHost();

    String getDummyFacilityName();

    String getDummyFacilityHostId();

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    Long getId();

    void setId(Long id);

    void setDummyFacilityHostId(String dummyFacilityHostId);

    void setProxyPort(int proxyPort);

    void setServerHost(String serverHost);

    void setServerPort(int serverPort);

    void setUseProject(boolean useProject);

    void setProxyHost(String proxyHost);

    void setDummyFacilityName(String dummyFacilityName);

    boolean isAllowUnknownClients();

    void setAllowUnknownClients(boolean allowUnknownClients);

    @CollectionTable(name = "trusted_addresses", joinColumns = @JoinColumn(name = "addr_id"))
    @ElementCollection()
    Set<String> getTrustedAddresses();

    void setTrustedAddresses(Set<String> trustedAddresses)
            throws UnknownHostException;

    @Enumerated(EnumType.STRING)
    EcclesFallbackMode getFallbackMode();

    void setFallbackMode(EcclesFallbackMode fallbackMode);

    @Transient
    Set<InetAddress> getTrustedInetAddresses();

}