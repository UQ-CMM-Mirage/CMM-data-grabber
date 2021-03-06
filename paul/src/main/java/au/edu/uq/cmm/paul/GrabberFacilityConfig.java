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

import java.util.List;

import au.edu.uq.cmm.aclslib.config.FacilityConfig;

/**
 * Configuration details API for a proxied ACLS facility.  Some
 * of these properties configure the behavior of the proxy and the
 * grabber.  Others are passed to the ACLS facility itself (via
 * the protocol).
 * <p>
 * (The names of some of these properties reflect the sometimes
 * strange terminology used by ACLS and its documentation.)
 * 
 * @author scrawley
 */
public interface GrabberFacilityConfig extends FacilityConfig {
    
    /**
     * Get the datafile template configurations for the datafiles in a dataset. 
     */
    List<? extends DatafileTemplateConfig> getDatafileTemplates();
    
    /**
     * If true, perform case insensitive matching of datafiles against 
     * templates.
     */
    boolean isCaseInsensitive();

    /**
     * If true, the file grabber should acquire a file lock before copying
     * (grabbing) a file from this facility's shared drive area.
     */
    boolean isUseFileLocks();

    /**
     * The file settling time is the time (in milliseconds) to wait after 
     * the last file modification event before the grabber attempts to grab the file.
     */
    int getFileSettlingTime();
    
    public enum FileArrivalMode {
        /**
         * Files are written directly from the instrument.  The file event times reflect
         * the actual times that instrument file writes occur.
         */
        DIRECT,
        /**
         * Files are transferred by an "rsync"-like program that preserves the source
         * file timestamps.  The file event times reflect the activity of the "rsync"
         * program.
         */
        RSYNC,
        /**
         * Like RSYNC, except that source file timestamps are lost in the transfer.
         */
        RSYNC_NO_PRESERVE,
    }
    
    /**
     * The file arrival mode says how files arrive in the directory area we are watching.
     */
    FileArrivalMode getFileArrivalMode();
    
    /**
     * If true (the default) the instrument is operated by the end user, and we can intuit
     * the ownership of output files based on who was logged in at the time the file was created.
     */
    boolean isUserOperated();
}