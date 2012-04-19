package au.edu.uq.cmm.paul;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import au.edu.uq.cmm.aclslib.config.ConfigurationException;
import au.edu.uq.cmm.aclslib.config.JsonConfigLoader;

public class StaticPaulFacilities {
    private List<StaticPaulFacility> facilities;
    
    public StaticPaulFacilities() {
        super();
    }

    public List<StaticPaulFacility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<StaticPaulFacility> facilities) {
        this.facilities = facilities;
    }

    /**
     * Load the facilities from a file.
     * 
     * @param facilitiesFile
     * @return the facilities or null if it couldn't be found / read.
     * @throws ConfigurationException 
     */
    public static StaticPaulFacilities loadFacilities(String facilitiesFile) 
            throws ConfigurationException {
        return new JsonConfigLoader<StaticPaulFacilities>(StaticPaulFacilities.class).
                loadConfiguration(facilitiesFile);
    }

    /**
     * Load the facilities from a URL.  This understands any URL that the
     * JVM has a protocol handler for, and also "classpath:" URLs. 
     * 
     * @return the facilities or null if it couldn't be found / read.
     * @param urlString the URL for the config file
     * @throws URISyntaxException 
     * @throws MalformedURLException 
     * @throws ConfigurationException 
     */
    public static StaticPaulFacilities loadFacilitiesFromUrl(String urlString) 
            throws ConfigurationException {
        return new JsonConfigLoader<StaticPaulFacilities>(StaticPaulFacilities.class).
                loadConfigurationFromUrl(urlString);
    }
}