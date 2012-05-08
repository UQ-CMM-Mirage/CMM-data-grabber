package au.edu.uq.cmm.paul.servlet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.BatchUpdateException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

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

    public ValidationResult<Facility> updateFacility(String facilityName, Map<?, ?> params) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Facility> query = em.createQuery(
                    "from Facility f where f.facilityName = :name",
                    Facility.class);
            query.setParameter("name", facilityName);
            Facility facility = query.getSingleResult();
            Map<String, String> diags = buildFacility(facility, params);
            if (diags.isEmpty()) {
                em.getTransaction().commit();
            } else {
                em.getTransaction().rollback();
            }
            return new ValidationResult<Facility>(diags, facility);
        } catch (RollbackException ex) {
            Throwable e = ex;
            while (e.getCause() != null) {
                e = e.getCause();
            }
            if (e instanceof BatchUpdateException) {
                LOG.error("update failed - next is",
                        ((BatchUpdateException) e).getNextException());
            } else {
                LOG.error("update failed - cause is", e);
            }
            throw ex;
        } finally {
            em.close();
        }
    }
    
    public Map<String, String> buildFacility(Facility res, Map<?, ?> params) {
        Map<String, String> diags = new HashMap<String, String>();
        res.setFacilityName(getNonEmptyString(params, "facilityName", diags));
        res.setFacilityDescription(getStringOrNull(params, "facilityDescription", diags));
        res.setAddress(getStringOrNull(params, "address", diags));
        if (res.getAddress() != null) {
            try {
                InetAddress.getByName(res.getAddress());
            } catch (UnknownHostException ex) {
                addDiag(diags, "address", ex.getMessage());
            }
        }
        res.setLocalHostId(getStringOrNull(params, "localHostId", diags));
        if (res.getAddress() == null && res.getLocalHostId() == null) {
            addDiag(diags, "localHostId",
                    "the local host id must be non-empty if address is empty");
        }
        res.setAccessName(getStringOrNull(params, "accessName", diags));
        res.setAccessPassword(getStringOrNull(params, "accessPassword", diags));
        res.setFolderName(getNonEmptyString(params, "folderName", diags));
        res.setDriveName(getStringOrNull(params, "driveName", diags));
        if (res.getDriveName() != null && !res.getDriveName().matches("[A-Z]")) {
            addDiag(diags, "driveName", 
                    "the drive name must be a single uppercase letter");
        }
        res.setFileSettlingTime(getInteger(params, "fileSettlingTime", diags));
        res.setCaseInsensitive(getBoolean(params, "caseInsensitive", diags));
        res.setUseFileLocks(getBoolean(params, "useFileLocks", diags));
        res.setUseFullScreen(getBoolean(params, "useFullScreen", diags));
        res.setUseTimer(getBoolean(params, "useTimer", diags));
        List<DatafileTemplate> templates = new LinkedList<DatafileTemplate>();
        for (int i = 1; params.get("template" + i + "filePattern") != null; i++) {
            DatafileTemplate template = new DatafileTemplate();
            template.setFilePattern(getNonEmptyString(params, "template" + i + "filePattern", diags));
            template.setSuffix(getNonEmptyString(params, "template" + i + "suffix", diags));
            template.setMimeType(getNonEmptyString(params, "template" + i + "mimeType", diags));
            template.setOptional(getBoolean(params, "template" + i + "optional", diags));
            for (DatafileTemplate existing : templates) {
                if (template.getFilePattern() != null &&
                        template.getFilePattern().equals(existing.getFilePattern())) {
                    addDiag(diags, "template" + i + "filePattern", 
                            "this file pattern has already been used");
                }
            }
            templates.add(template);
        }
        res.setDatafileTemplates(templates);
        return diags;
    }

    private void addDiag(Map<String, String> diags, String key,
            String message) {
        if (diags.get(key) == null) {
            diags.put(key, message);
        }
    }

    private String getStringOrNull(Map<?, ?> params, String key, Map<String, String> diags) {
        String str = getString(params, key, diags, false);
        if (str != null && str.isEmpty()) {
            return null;
        } else {
            return str;
        }
    }
    
    private String getString(Map<?, ?> params, String key, 
            Map<String, String> diags, boolean canBeMissing) {
        String[] values = (String[]) params.get(key);
        if (values != null && values.length > 0) {
            return values[0].trim();
        } else {
            if (!canBeMissing) {
                diags.put(key, "field is missing");
            }
            return null;
        }
    }
    
    private String getNonEmptyString(Map<?, ?> params, String key, Map<String, String> diags) {
        String str = getString(params, key, diags, false);
        if (str != null && str.isEmpty()) {
            addDiag(diags, key, "this field must not be empty");
        }
        return str;
    }
    
    private int getInteger(Map<?, ?> params, String key, Map<String, String> diags) {
        String str = getNonEmptyString(params, key, diags);
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            addDiag(diags, key, "this value is not a valid number");
            return 0;
        }
    }

    private boolean getBoolean(Map<?, ?> params, String key, Map<String, String> diags) {
        String str = getString(params, key, diags, true);
        if (str == null || str.isEmpty() || str.equalsIgnoreCase("false")) {
            return false;
        } else if (str.equalsIgnoreCase("true")) {
            return true;
        } else {
            addDiag(diags, key, "this value must be 'true' or 'false' / ''");
            return false;
        }
    }
}
