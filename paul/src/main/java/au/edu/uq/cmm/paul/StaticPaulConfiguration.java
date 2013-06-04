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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import au.edu.uq.cmm.aclslib.config.ConfigurationException;
import au.edu.uq.cmm.aclslib.config.JsonConfigLoader;
import au.edu.uq.cmm.paul.queue.QueueFileManager;

/**
 * This class represents the configuration details of an ACLS proxy backed by a
 * JSON file.
 * 
 * @author scrawley
 */
public class StaticPaulConfiguration implements GrabberConfiguration {
    
    private String captureDirectory;
    private String archiveDirectory;
    private int grabberTimeout;
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
    private boolean holdDatasetsWithNoUser = true;
    private String primaryRepositoryUrl;
    private String aclsUrl;
    private QueueFileManager.Strategy queueFileStrategy = QueueFileManager.Strategy.COPY_FILES;
    private long queueFileSizeThreshold;

    @Override
    public String getCaptureDirectory() {
        return captureDirectory;
    }

    public void setCaptureDirectory(String captureDirectory) {
        this.captureDirectory = captureDirectory;
    }

    @Override
    public String getArchiveDirectory() {
        return archiveDirectory;
    }

    public void setArchiveDirectory(String archiveDirectory) {
        this.archiveDirectory = archiveDirectory;
    }

    @Override
    public int getGrabberTimeout() {
        return grabberTimeout;
    }

    public void setGrabberTimeout(int grabberTimeout) {
        this.grabberTimeout = grabberTimeout;
    }

    @Override
    public String getBaseFileUrl() {
        return baseFileUrl;
    }

    public void setBaseFileUrl(String baseFileUrl) {
        this.baseFileUrl = baseFileUrl;
    }

    @Override
    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    @Override
    public String getFeedTitle() {
        return feedTitle;
    }

    public void setFeedTitle(String feedTitle) {
        this.feedTitle = feedTitle;
    }

    @Override
    public String getFeedAuthor() {
        return feedAuthor;
    }

    public void setFeedAuthor(String feedAuthor) {
        this.feedAuthor = feedAuthor;
    }

    @Override
    public String getFeedAuthorEmail() {
        return feedAuthorEmail;
    }

    public void setFeedAuthorEmail(String feedAuthorEmail) {
        this.feedAuthorEmail = feedAuthorEmail;
    }

    @Override
    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    @Override
    public int getFeedPageSize() {
        return feedPageSize ;
    }

    public void setFeedPageSize(int feedPageSize) {
        this.feedPageSize = feedPageSize;
    }

    @Override
    public long getQueueExpiryTime() {
        return queueExpiryTime;
    }

    public void setQueueExpiryTime(long queueExpiryTime) {
        this.queueExpiryTime = queueExpiryTime;
    }

    @Override
    public long getQueueExpiryInterval() {
        return queueExpiryInterval;
    }

    public void setQueueExpiryInterval(long queueExpiryInterval) {
        this.queueExpiryInterval = queueExpiryInterval;
    }

    @Override
    public boolean isExpireByDeleting() {
        return expireByDeleting;
    }

    public void setExpireByDeleting(boolean expireByDeleting) {
        this.expireByDeleting = expireByDeleting;
    }

    @Override
    public String getPrimaryRepositoryUrl() {
        return primaryRepositoryUrl;
    }

    public void setPrimaryRepositoryUrl(String primaryRepositoryUrl) {
        this.primaryRepositoryUrl = primaryRepositoryUrl;
    }

    @Override
    public String getAclsUrl() {
        return aclsUrl;
    }

    public void setAclsUrl(String aclsUrl) {
        this.aclsUrl = aclsUrl;
    }

    @Override
    public boolean isHoldDatasetsWithNoUser() {
        return holdDatasetsWithNoUser;
    }

    public void setHoldDatasetsWithNoUser(boolean holdDatasetsWithNoUser) {
        this.holdDatasetsWithNoUser = holdDatasetsWithNoUser;
    }

    @Override
    public QueueFileManager.Strategy getQueueFileStrategy() {
        return queueFileStrategy;
    }

    public void setQueueFileStrategy(QueueFileManager.Strategy queueFileStrategy) {
        this.queueFileStrategy = queueFileStrategy;
    }

    @Override
    public Long getQueueFileSizeThreshold() {
        return queueFileSizeThreshold;
    }

    public void setQueueFileSizeThreshold(Long queueFileSizeThreshold) {
        this.queueFileSizeThreshold = queueFileSizeThreshold;
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

    /**
     * Load the configuration from a stream.   
     * @return the configuration or null
     * @param urlString the URL for the config file
     * @throws URISyntaxException 
     * @throws MalformedURLException 
     */
    public static StaticPaulConfiguration loadConfiguration(InputStream is) 
            throws ConfigurationException {
        return new JsonConfigLoader<StaticPaulConfiguration>(StaticPaulConfiguration.class).
                loadConfiguration(is);
    }

    @Override
    public String toString() {
        return "StaticPaulConfiguration [captureDirectory=" + captureDirectory
                + ", archiveDirectory=" + archiveDirectory
                + ", grabberTimeout=" + grabberTimeout + ", baseFileUrl="
                + baseFileUrl + ", feedId=" + feedId + ", feedTitle="
                + feedTitle + ", feedAuthor=" + feedAuthor
                + ", feedAuthorEmail=" + feedAuthorEmail + ", feedUrl="
                + feedUrl + ", feedPageSize=" + feedPageSize
                + ", queueExpiryTime=" + queueExpiryTime
                + ", queueExpiryInterval=" + queueExpiryInterval
                + ", expireByDeleting=" + expireByDeleting
                + ", holdDatasetsWithNoUser=" + holdDatasetsWithNoUser
                + ", primaryRepositoryUrl=" + primaryRepositoryUrl
                + ", aclsUrl=" + aclsUrl + ", queueFileStrategy="
                + queueFileStrategy + "]";
    }
}
