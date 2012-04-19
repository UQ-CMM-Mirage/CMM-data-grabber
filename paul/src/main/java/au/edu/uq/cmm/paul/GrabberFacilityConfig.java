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
    
}