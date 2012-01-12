package au.edu.uq.cmm.paul;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import au.edu.uq.cmm.paul.grabber.AdminMetadata;

/**
 * This class is responsible for low-level management of the ingestion queue.
 * 
 * @author scrawley
 */
public class QueueManager {
    private static final Logger LOG = Logger.getLogger(QueueManager.class);
    private EntityManagerFactory entityManagerFactory;

    public QueueManager(Paul services) {
        this.entityManagerFactory = services.getEntityManagerFactory();
    }

    public List<AdminMetadata> getSnapshot() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery("from AdminMetadata a order by a.id", 
                    AdminMetadata.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    public void addEntry(AdminMetadata metadata, File metadataFile) 
            throws JsonGenerationException, IOException {
        saveToFileSystem(metadataFile, metadata);
        saveToDatabase(metadata);
    }

    private void saveToDatabase(AdminMetadata metadata) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(metadata);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    private void saveToFileSystem(File metadataFile, AdminMetadata metadata)
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

}
