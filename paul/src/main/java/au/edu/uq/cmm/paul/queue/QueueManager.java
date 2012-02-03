package au.edu.uq.cmm.paul.queue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.PaulException;
import au.edu.uq.cmm.paul.grabber.DatafileMetadata;
import au.edu.uq.cmm.paul.grabber.DatasetMetadata;

/**
 * This class is responsible for low-level management of the ingestion queue.
 * 
 * @author scrawley
 */
public class QueueManager {
    private static final Logger LOG = Logger.getLogger(QueueManager.class);
    private Paul services;

    public QueueManager(Paul services) {
        this.services = services;
    }

    public List<DatasetMetadata> getSnapshot() {
        EntityManager em = services.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("from DatasetMetadata a order by a.id", 
                    DatasetMetadata.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void addEntry(DatasetMetadata metadata, File metadataFile) 
            throws JsonGenerationException, IOException {
        saveToFileSystem(metadataFile, metadata);
        saveToDatabase(metadata);
    }

    private void saveToDatabase(DatasetMetadata metadata) {
        EntityManager em = services.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(metadata);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private void saveToFileSystem(File metadataFile, DatasetMetadata metadata)
            throws IOException, JsonGenerationException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(metadataFile))) {
            ObjectMapper mapper = new ObjectMapper();
            JsonFactory jf = new JsonFactory();
            JsonGenerator jg = jf.createJsonGenerator(bw);
            jg.useDefaultPrettyPrinter();
            mapper.writeValue(jg, metadata);
            LOG.info("Saved admin metadata to " + metadataFile);
        } catch (JsonParseException ex) {
            throw new PaulException(ex);
        } catch (JsonMappingException ex) {
            throw new PaulException(ex);
        }
    }

    public void deleteAll(boolean discard) {
        EntityManager em = services.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            List<DatasetMetadata> datasets = 
                    em.createQuery("from DatasetMetadata", 
                    DatasetMetadata.class).getResultList();
            for (DatasetMetadata dataset : datasets) {
                doDelete(discard, em, dataset);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private void doDelete(boolean discard, EntityManager entityManager,
            DatasetMetadata dataset) {
        for (DatafileMetadata datafile : dataset.getDatafiles()) {
            disposeOfFile(datafile.getCapturedFilePathname(), discard);
        }
        disposeOfFile(dataset.getMetadataFilePathname(), discard);
        entityManager.remove(dataset);
    }

    private void disposeOfFile(String pathname, boolean discard) {
        File file = new File(pathname);
        if (!file.exists()) {
            LOG.info("File " + pathname + " no longer exists");
            return;
        }
        if (!discard) {
            File dest = new File("/tmp/archive", file.getName());
            if (dest.exists()) {
                LOG.info("Archived file " + dest + " already exists");
            } else {
                if (file.renameTo(dest)) {
                    LOG.info("File " + file + " archived as " + dest);
                } else {
                    LOG.info("File " + file + " count not be archived - " +
                    		"it remains in the queue area");
                }
                return;
            }
        }
        if (file.delete()) {
            LOG.info("File " + pathname + " deleted from queue area");
        } else {
            LOG.info("File " + pathname + " not deleted from queue area");
        }
    }

}
