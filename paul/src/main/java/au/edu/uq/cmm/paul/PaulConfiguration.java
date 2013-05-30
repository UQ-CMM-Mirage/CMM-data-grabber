/*
* Copyright 2012-2013, CMM, University of Queensland.
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

import java.net.UnknownHostException;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/**
 * This class represents the configuration details of a DataGrabber instance.
 * The details are persisted using Hibernate / JPA.
 * 
 * @author scrawley
 */
@Entity
@Table(name = "CONFIGURATION")
public class PaulConfiguration implements GrabberConfiguration {
    private Long id;
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
    
    public PaulConfiguration() {
        super();
    }
    
    public PaulConfiguration(StaticPaulConfiguration staticConfig) throws UnknownHostException {
        setBaseFileUrl(staticConfig.getBaseFileUrl());
        setCaptureDirectory(staticConfig.getCaptureDirectory());
        setArchiveDirectory(staticConfig.getArchiveDirectory());
        setGrabberTimeout(staticConfig.getGrabberTimeout());
        setFeedId(staticConfig.getFeedId());
        setFeedTitle(staticConfig.getFeedTitle());
        setFeedAuthor(staticConfig.getFeedAuthor());
        setFeedAuthorEmail(staticConfig.getFeedAuthorEmail());
        setFeedUrl(staticConfig.getFeedUrl());
        setFeedPageSize(staticConfig.getFeedPageSize());
        setQueueExpiryInterval(staticConfig.getQueueExpiryInterval());
        setQueueExpiryTime(staticConfig.getQueueExpiryTime());
        setExpireByDeleting(staticConfig.isExpireByDeleting());
        setHoldDatasetsWithNoUser(staticConfig.isHoldDatasetsWithNoUser());
        setPrimaryRepositoryUrl(staticConfig.getPrimaryRepositoryUrl());
        setAclsUrl(staticConfig.getAclsUrl());
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

    public int getGrabberTimeout() {
        return grabberTimeout;
    }

    public void setGrabberTimeout(int grabberTimeout) {
        this.grabberTimeout = grabberTimeout;
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
        return feedPageSize;
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

    public boolean isHoldDatasetsWithNoUser() {
        return holdDatasetsWithNoUser;
    }

    public void setHoldDatasetsWithNoUser(boolean holdDatasetsWithNoUser) {
        this.holdDatasetsWithNoUser = holdDatasetsWithNoUser;
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
    
    public static PaulConfiguration load(EntityManagerFactory entityManagerFactory) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
        	return em.createQuery("from PaulConfiguration", 
        			PaulConfiguration.class).getSingleResult();
        } catch (NoResultException ex) {
        	return null;
        } finally {
        	em.close();
        }
    }

    @Transient
    public boolean isEmpty() {
        return this.equals(new PaulConfiguration());
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((aclsUrl == null) ? 0 : aclsUrl.hashCode());
		result = prime
				* result
				+ ((archiveDirectory == null) ? 0 : archiveDirectory.hashCode());
		result = prime * result
				+ ((baseFileUrl == null) ? 0 : baseFileUrl.hashCode());
		result = prime
				* result
				+ ((captureDirectory == null) ? 0 : captureDirectory.hashCode());
		result = prime * result + (expireByDeleting ? 1231 : 1237);
		result = prime * result
				+ ((feedAuthor == null) ? 0 : feedAuthor.hashCode());
		result = prime * result
				+ ((feedAuthorEmail == null) ? 0 : feedAuthorEmail.hashCode());
		result = prime * result + ((feedId == null) ? 0 : feedId.hashCode());
		result = prime * result + feedPageSize;
		result = prime * result
				+ ((feedTitle == null) ? 0 : feedTitle.hashCode());
		result = prime * result + ((feedUrl == null) ? 0 : feedUrl.hashCode());
		result = prime * result + grabberTimeout;
		result = prime * result + (holdDatasetsWithNoUser ? 1231 : 1237);
		result = prime
				* result
				+ ((primaryRepositoryUrl == null) ? 0 : primaryRepositoryUrl
						.hashCode());
		result = prime * result
				+ (int) (queueExpiryInterval ^ (queueExpiryInterval >>> 32));
		result = prime * result
				+ (int) (queueExpiryTime ^ (queueExpiryTime >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PaulConfiguration other = (PaulConfiguration) obj;
		if (aclsUrl == null) {
			if (other.aclsUrl != null)
				return false;
		} else if (!aclsUrl.equals(other.aclsUrl))
			return false;
		if (archiveDirectory == null) {
			if (other.archiveDirectory != null)
				return false;
		} else if (!archiveDirectory.equals(other.archiveDirectory))
			return false;
		if (baseFileUrl == null) {
			if (other.baseFileUrl != null)
				return false;
		} else if (!baseFileUrl.equals(other.baseFileUrl))
			return false;
		if (captureDirectory == null) {
			if (other.captureDirectory != null)
				return false;
		} else if (!captureDirectory.equals(other.captureDirectory))
			return false;
		if (expireByDeleting != other.expireByDeleting)
			return false;
		if (feedAuthor == null) {
			if (other.feedAuthor != null)
				return false;
		} else if (!feedAuthor.equals(other.feedAuthor))
			return false;
		if (feedAuthorEmail == null) {
			if (other.feedAuthorEmail != null)
				return false;
		} else if (!feedAuthorEmail.equals(other.feedAuthorEmail))
			return false;
		if (feedId == null) {
			if (other.feedId != null)
				return false;
		} else if (!feedId.equals(other.feedId))
			return false;
		if (feedPageSize != other.feedPageSize)
			return false;
		if (feedTitle == null) {
			if (other.feedTitle != null)
				return false;
		} else if (!feedTitle.equals(other.feedTitle))
			return false;
		if (feedUrl == null) {
			if (other.feedUrl != null)
				return false;
		} else if (!feedUrl.equals(other.feedUrl))
			return false;
		if (grabberTimeout != other.grabberTimeout)
			return false;
		if (holdDatasetsWithNoUser != other.holdDatasetsWithNoUser)
			return false;
		if (primaryRepositoryUrl == null) {
			if (other.primaryRepositoryUrl != null)
				return false;
		} else if (!primaryRepositoryUrl.equals(other.primaryRepositoryUrl))
			return false;
		if (queueExpiryInterval != other.queueExpiryInterval)
			return false;
		if (queueExpiryTime != other.queueExpiryTime)
			return false;
		return true;
	}

    @Override
    public String toString() {
        return "PaulConfiguration [id=" + id + ", captureDirectory="
                + captureDirectory + ", archiveDirectory=" + archiveDirectory
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
                + ", aclsUrl=" + aclsUrl + "]";
    }
}
