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

import au.edu.uq.cmm.paul.queue.QueueFileManager;


/**
 * The combined configuration property API for ACLSProxy and the data grabber.
 * Different implementations support different persistence mechanisms.
 * 
 * @author scrawley
 */
public interface GrabberConfiguration {
    
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
     * Get the maximum time (in milliseconds) that the data grabber 
     * should wait for an instrument to finish writing out files in
     * a dataset.
     */
    int getGrabberTimeout();

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
    
    /**
     * Returns the queue file management strategy.  This determines whether
     * files are copied or symlinked into the queue.
     */
    QueueFileManager.Strategy getQueueFileStrategy();
    
    /**
     * If the queue file strategy is HYBRID, files larger than this size (in bytes)
     * will be symlinked.
     */
    Long getQueueFileSizeThreshold();
    
}