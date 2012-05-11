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

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import au.edu.uq.cmm.aclslib.config.ConfigurationException;
import au.edu.uq.cmm.aclslib.config.JsonConfigLoader;

/**
 * This class represents the configuration details of an ACLS proxy backed by a
 * JSON file.
 * 
 * @author scrawley
 */
public class StaticPaulConfiguration implements GrabberConfiguration {

    private int proxyPort = 1024;
    private int serverPort = 1024;
    private String serverHost;
    private String proxyHost;
    private boolean allowUnknownClients;
    private Set<String> trustedAddresses = Collections.emptySet();
    private Set<InetAddress> trustedInetAddresses = Collections.emptySet();
    
    private boolean useProject;
    private String dummyFacilityName;
    private String dummyFacilityHostId;
    private String captureDirectory;
    private String archiveDirectory;
    private String baseFileUrl;
    private String feedId;
    private String feedTitle;
    private String feedAuthor;
    private String feedAuthorEmail;
    private String feedUrl;
    private int feedPageSize = 20;
    private long queueExpiryTime;
    private long queueExpiryInterval;
    private boolean expireByDeleting;
    private DataGrabberRestartPolicy dataGrabberRestartPolicy = 
            DataGrabberRestartPolicy.NO_AUTO_START;
    private boolean holdDatasetsWithNoUser = true;
    private String primaryRepositoryUrl;
    private String aclsUrl;

    public final int getProxyPort() {
        return proxyPort;
    }

    public final String getServerHost() {
        return serverHost;
    }

    public final int getServerPort() {
        return serverPort;
    }

    public final boolean isUseProject() {
        return useProject;
    }

    public final void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public final void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public final void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public final void setUseProject(boolean useProject) {
        this.useProject = useProject;
    }

    public final String getProxyHost() {
        return proxyHost;
    }

    public final void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getCaptureDirectory() {
        return captureDirectory;
    }

    public void setCaptureDirectory(String captureDirectory) {
        this.captureDirectory = captureDirectory;
    }

    public String getArchiveDirectory() {
        return archiveDirectory;
    }

    public void setArchiveDirectory(String archiveDirectory) {
        this.archiveDirectory = archiveDirectory;
    }

    public String getBaseFileUrl() {
        return baseFileUrl;
    }

    public void setBaseFileUrl(String baseFileUrl) {
        this.baseFileUrl = baseFileUrl;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getFeedTitle() {
        return feedTitle;
    }

    public void setFeedTitle(String feedTitle) {
        this.feedTitle = feedTitle;
    }

    public String getFeedAuthor() {
        return feedAuthor;
    }

    public void setFeedAuthor(String feedAuthor) {
        this.feedAuthor = feedAuthor;
    }

    public String getFeedAuthorEmail() {
        return feedAuthorEmail;
    }

    public void setFeedAuthorEmail(String feedAuthorEmail) {
        this.feedAuthorEmail = feedAuthorEmail;
    }
    
    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }
    
    public int getFeedPageSize() {
        return feedPageSize ;
    }

    public void setFeedPageSize(int feedPageSize) {
        this.feedPageSize = feedPageSize;
    }

    public long getQueueExpiryTime() {
        return queueExpiryTime;
    }

    public void setQueueExpiryTime(long queueExpiryTime) {
        this.queueExpiryTime = queueExpiryTime;
    }

    public long getQueueExpiryInterval() {
        return queueExpiryInterval;
    }

    public void setQueueExpiryInterval(long queueExpiryInterval) {
        this.queueExpiryInterval = queueExpiryInterval;
    }

    public boolean isExpireByDeleting() {
        return expireByDeleting;
    }

    public void setExpireByDeleting(boolean expireByDeleting) {
        this.expireByDeleting = expireByDeleting;
    }

    public DataGrabberRestartPolicy getDataGrabberRestartPolicy() {
        return dataGrabberRestartPolicy;
    }

    public void setDataGrabberRestartPolicy(
            DataGrabberRestartPolicy dataGrabberRestartPolicy) {
        this.dataGrabberRestartPolicy = dataGrabberRestartPolicy;
    }

    public String getPrimaryRepositoryUrl() {
        return primaryRepositoryUrl;
    }

    public void setPrimaryRepositoryUrl(String primaryRepositoryUrl) {
        this.primaryRepositoryUrl = primaryRepositoryUrl;
    }

    public String getAclsUrl() {
        return aclsUrl;
    }

    public void setAclsUrl(String aclsUrl) {
        this.aclsUrl = aclsUrl;
    }

    public boolean isHoldDatasetsWithNoUser() {
        return holdDatasetsWithNoUser;
    }

    public void setHoldDatasetsWithNoUser(boolean holdDatasetsWithNoUser) {
        this.holdDatasetsWithNoUser = holdDatasetsWithNoUser;
    }
    
    public String getDummyFacilityName() {
        return dummyFacilityName;
    }

    public void setDummyFacilityName(String dummyFacilityName) {
        this.dummyFacilityName = dummyFacilityName;
    }

    public String getDummyFacilityHostId() {
        return dummyFacilityHostId;
    }

    public void setDummyFacilityHostId(String dummyFacilityHostId) {
        this.dummyFacilityHostId = dummyFacilityHostId;
    }

    public boolean isAllowUnknownClients() {
        return allowUnknownClients;
    }

    public void setAllowUnknownClients(boolean allowUnknownClients) {
        this.allowUnknownClients = allowUnknownClients;
    }

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

    @JsonIgnore
    public Set<InetAddress> getTrustedInetAddresses() {
        return trustedInetAddresses;
    }


    /**
     * Load the configuration from a file.
     * 
     * @param configFile
     * @return the configuration or null if it couldn't be found / read.
     * @throws ConfigurationException 
     */
    public static StaticPaulConfiguration loadConfiguration(String configFile) 
            throws ConfigurationException {
        return new JsonConfigLoader<StaticPaulConfiguration>(StaticPaulConfiguration.class).
                loadConfiguration(configFile);
    }

    /**
     * Load the configuration from a URL.  This understands any URL that the
     * JVM has a protocol handler for, and also "classpath:" URLs. 
     * @return the configuration or null
     * @param urlString the URL for the config file
     * @throws URISyntaxException 
     * @throws MalformedURLException 
     */
    public static StaticPaulConfiguration loadConfigurationFromUrl(String urlString) 
            throws ConfigurationException {
        return new JsonConfigLoader<StaticPaulConfiguration>(StaticPaulConfiguration.class).
                loadConfigurationFromUrl(urlString);
    }
}
