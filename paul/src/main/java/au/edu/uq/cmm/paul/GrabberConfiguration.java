package au.edu.uq.cmm.paul;

import au.edu.uq.cmm.aclslib.config.ACLSProxyConfiguration;

/**
 * The combined configuration property API for ACLSProxy and the data grabber.
 * Different implementations support different persistence mechanisms.
 * 
 * @author scrawley
 */
public interface GrabberConfiguration extends ACLSProxyConfiguration {
    
    public enum DataGrabberRestartPolicy {
        AUTO_START_NO_CATCHUP,
        AUTO_START_CATCHUP,
        NO_AUTO_START
    }

    /**
     * Get the base URL that will be used for the URLs of files
     * in the ingestion queue.
     */
    String getBaseFileUrl();

    /**
     * Get the pathname of the directory where captured (grabbed) files
     * and admin metadata file are written.
     */
    String getCaptureDirectory();
    
    /**
     * Get the pathname of the directory where files and metadata are
     * archived.
     */
    String getArchiveDirectory();

    /**
     * Get the URL or IRI for the atom feed.  This is what is used as
     * the "atom:id" for the feed.
     */
    String getFeedId();

    /**
     * Get the string used as the feed's atom:title
     */
    String getFeedTitle();

    /**
     * Get the string used as the feed's author name
     */
    String getFeedAuthor();

    /**
     * Get the string used as the feed's author email.  This can be
     * empty or null.
     */
    String getFeedAuthorEmail();

    /**
     * Get the URL for fetching the feed.
     */
    String getFeedUrl();

    /**
     * Get the page size for the atom feed.  This should be a number greater or
     * equal to 1.  (Setting this to a very large number effectively turns 
     * off feed paging.)
     */
    int getFeedPageSize();
    
    /**
     * Get queue expiry time.  After an entry has been on the queue this long, 
     * it is eligible for expiry.  (Value is in minutes.)  If zero or negative,
     * queue entries don't expire automatically.
     */
    long getQueueExpiryTime();
    
    /**
     * Get queue expiry interval.  This is the interval between successive queue
     * checks.  (Value is in minutes.)  If zero or negative, automatic queue expiry
     * checking is disabled.
     */
    long getQueueExpiryInterval();
    
    /**
     * This determines if expired queue entries are archived or deleted outright. 
     */
    boolean isExpireByDeleting();
    
    /**
     * This determines the DataGrabber's behavior on restart.
     */
    DataGrabberRestartPolicy getDataGrabberRestartPolicy();
    
    /**
     * This determines if the DataGrabber will hold datasets that don't belong
     * to a user, or queue them for ingestion as-is.
     */
    boolean isHoldDatasetsWithNoUser();
    
    /**
     * Get the URL for Data Grabber's primary downstream repository.  This
     * is the place where the user's browser should be redirected to access 
     * his / her ingested datasets.
     */
    String getPrimaryRepositoryUrl();
    
    /**
     * Get the URL for the local ACLS booking system
     */
    String getAclsUrl();
    
}