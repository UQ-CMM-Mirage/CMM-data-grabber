package au.edu.uq.cmm.paul.servlet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.cmm.paul.PaulConfiguration;
import au.edu.uq.cmm.paul.StaticPaulConfiguration;
import au.edu.uq.cmm.paul.StaticPaulFacilities;
import au.edu.uq.cmm.paul.StaticPaulFacility;
import au.edu.uq.cmm.paul.status.DatafileTemplate;
import au.edu.uq.cmm.paul.status.Facility;

public class ConfigurationManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationManager.class);

    private PaulConfiguration activeConfig;
    private PaulConfiguration latestConfig;
    private StaticPaulConfiguration staticConfig;
    private EntityManagerFactory entityManagerFactory;
    private StaticPaulFacilities staticFacilities;

    
    public ConfigurationManager(EntityManagerFactory entityManagerFactory,
            StaticPaulConfiguration staticConfig, 
            StaticPaulFacilities staticFacilities) {
        this.entityManagerFactory = entityManagerFactory;
        this.staticConfig = staticConfig;
        this.staticFacilities =  staticFacilities;
        activeConfig = PaulConfiguration.load(entityManagerFactory, true);
        if (activeConfig.isEmpty() && staticConfig != null) {
            activeConfig = doResetConfiguration();
        }
        latestConfig = activeConfig;
    }

    public PaulConfiguration getActiveConfig() {
        return activeConfig;
    }

    public PaulConfiguration getLatestConfig() {
        return latestConfig;
    }
    
    public void resetConfiguration() {
        latestConfig = doResetConfiguration();
    }

    private PaulConfiguration doResetConfiguration() {
        LOG.info("Resetting details from static Configuration");
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PaulConfiguration newConfig = new PaulConfiguration(staticConfig);
            // FIXME - we do two transactions.  Combining the two transactions 
            // into one transaction is gives constraint errors when adding the new
            // facilities.  (The fix is to version the configurations.)
            em.getTransaction().begin();
            PaulConfiguration oldConfig = em.
                    createQuery("from PaulConfiguration", PaulConfiguration.class).
                    getSingleResult();
            em.remove(oldConfig);
            List<Facility> facilities = em.
                    createQuery("from Facility", Facility.class).
                    getResultList();
            for (Facility facility : facilities) {
                em.remove(facility);
            }
            em.getTransaction().commit();
            
            // Second transaction
            em.getTransaction().begin();
            em.persist(newConfig);
            for (StaticPaulFacility staticFacility : staticFacilities.getFacilities()) {
                Facility facility = new Facility(staticFacility);
                em.persist(facility);
            }
            em.getTransaction().commit();
            return newConfig;
        } catch (UnknownHostException ex) {
            LOG.error("Reset failed", ex);
            return null;
        } finally {
            em.close();
        }
    }
    
    public Map<String, String> buildFacility(Facility res, Map<?, ?> params) {
        Map<String, String> diags = new HashMap<String, String>();
        res.setFacilityName(getNonEmptyString(params, "facilityName", diags));
        res.setFacilityDescription(getStringOrNull(params, "facilityDescription", diags));
        String address = getString(params, "address", diags);
        if (!address.isEmpty()) {
            try {
                InetAddress.getByName(address);
                res.setAddress(address);
            } catch (UnknownHostException ex) {
                diags.put("address", ex.getMessage());
            }
        }
        res.setLocalHostId(getStringOrNull(params, "localHostId", diags));
        if (res.getAddress() == null && res.getLocalHostId() == null) {
            diags.put("localHostId",
                    "this field must be non-empty if no address is provided");
        }
        res.setAccessName(getStringOrNull(params, "accessName", diags));
        res.setAccessPassword(getStringOrNull(params, "accessPassword", diags));
        res.setFolderName(getStringOrNull(params, "folderName", diags));
        res.setDriveName(getStringOrNull(params, "driveName", diags));
        res.setFileSettlingTime(getInteger(params, "fileSettlingTime", diags));
        res.setCaseInsensitive(getBoolean(params, "caseInsensitive", diags));
        res.setUseFileLocks(getBoolean(params, "useFileLocks", diags));
        res.setUseFullScreen(getBoolean(params, "useFullScreen", diags));
        res.setUseTimer(getBoolean(params, "useTimer", diags));
        List<DatafileTemplate> templates = new LinkedList<DatafileTemplate>();
        for (int i = 1; params.get("template-" + i + ".filePattern") != null; i++) {
            DatafileTemplate template = new DatafileTemplate();
            template.setFilePattern(getString(params, "template-" + i + ".filePattern", diags));
            template.setSuffix(getString(params, "template-" + i + ".suffix", diags));
            template.setMimeType(getString(params, "template-" + i + ".mimeType", diags));
            template.setOptional(getBoolean(params, "template-" + i + ".optional", diags));
            templates.add(template);
        }
        res.setDatafileTemplates(templates);
        return diags;
    }

    private String getStringOrNull(Map<?, ?> params, String key, Map<String, String> diags) {
        String[] values = (String[]) params.get(key);
        if (values != null && values.length > 0) {
            String str = values[0].trim();
            return str.isEmpty() ? null : str;
        } else {
            return null;
        }
    }

    private String getString(Map<?, ?> params, String key, Map<String, String> diags) {
        String[] values = (String[]) params.get(key);
        if (values != null && values.length > 0) {
            return values[0].trim();
        } else {
            diags.put(key, "field is missing");
            return "";
        }
    }
    
    private String getNonEmptyString(Map<?, ?> params, String key, Map<String, String> diags) {
        String str = getString(params, key, diags);
        if (str.isEmpty()) {
            diags.put(key, "this field must not be empty");
        }
        return str;
    }
    
    private int getInteger(Map<?, ?> params, String key, Map<String, String> diags) {
        String str = getString(params, key, diags);
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            diags.put(key, "this value is not a valid number");
            return 0;
        }
    }

    private boolean getBoolean(Map<?, ?> params, String key, Map<String, String> diags) {
        String str = getStringOrNull(params, key, diags);
        if (str == null || str.equalsIgnoreCase("false")) {
            return false;
        } else if (str.equalsIgnoreCase("true")) {
            return true;
        } else {
            diags.put(key, "this value must be 'true' or 'false' / ''");
            return false;
        }
    }
}
