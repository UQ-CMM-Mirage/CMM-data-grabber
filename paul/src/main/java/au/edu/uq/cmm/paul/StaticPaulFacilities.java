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
     * @return the facilities.
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
     * @return the facilities.
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

    /**
     * Load the facilities from a Stream.
     * 
     * @return the facilities.
     * @param urlString the URL for the config file
     * @throws URISyntaxException 
     * @throws MalformedURLException 
     * @throws ConfigurationException 
     */
    public static StaticPaulFacilities loadFacilities(InputStream is) 
            throws ConfigurationException {
        return new JsonConfigLoader<StaticPaulFacilities>(StaticPaulFacilities.class).
                loadConfiguration(is);
    }
}