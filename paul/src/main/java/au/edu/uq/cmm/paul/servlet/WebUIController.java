package au.edu.uq.cmm.paul.servlet;

import java.io.File;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import au.edu.uq.cmm.paul.Paul;
import au.edu.uq.cmm.paul.grabber.DatafileMetadata;
import au.edu.uq.cmm.paul.grabber.DatasetMetadata;

/**
 * The MVC controller for Paul's web UI.  This supports the status and configuration
 * pages and also implements GET access to the files in the queue area.
 * 
 * @author scrawley
 */
@Controller
public class WebUIController {
    private static final Logger LOG = Logger.getLogger(WebUIController.class);
    
    @Autowired
    Paul services;
    
    @RequestMapping(value="/status", method=RequestMethod.GET)
    public String status(Model model) {
        model.addAttribute("facilities", services.getFacilitySessionManager().getSnapshot());
        return "status";
    }
    
    @RequestMapping(value="/config", method=RequestMethod.GET)
    public String config(Model model) {
        model.addAttribute("config", services.getConfiguration());
        return "config";
    }
    
    @RequestMapping(value="/queue", method=RequestMethod.GET)
    public String queue(Model model) {
        model.addAttribute("queue", services.getQueueManager().getSnapshot());
        return "queue";
    }
    
    @RequestMapping(value="/queue/{entry:.+}", method=RequestMethod.GET)
    public String queueEntry(@PathVariable String entry, Model model, 
            HttpServletResponse response) 
            throws IOException {
        long id;
        try {
            id = Long.parseLong(entry);
        } catch (NumberFormatException ex) {
            LOG.debug("Rejected request with bad entry id");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        DatasetMetadata metadata = fetchMetadata(id);
        if (metadata == null) {
            LOG.debug("Rejected request for unknown entry");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        model.addAttribute("entry", metadata);
        return "queueEntry";
    }
    
    @RequestMapping(value="/files/{fileName:.+}", method=RequestMethod.GET)
    public String file(@PathVariable String fileName, Model model, 
            HttpServletResponse response) 
            throws IOException {
        LOG.debug("Request to fetch file " + fileName);
        // This aims to prevent requests from reading files outside of the queue directory.
        // FIXME - this assumes that the directory for the queue is flat ...
        if (fileName.contains("/") || fileName.equals("..")) {
            LOG.debug("Rejected request for security reasons");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        File file = new File(services.getConfiguration().getCaptureDirectory(), fileName);
        DatafileMetadata metadata = fetchMetadata(file);
        if (metadata == null) {
            LOG.debug("No metadata for file " + fileName);
        } else {
            LOG.debug("Found metadata for file " + fileName);
        }
        model.addAttribute("file", file);
        model.addAttribute("contentType", 
                metadata == null ? "application/octet-stream" : metadata.getMimeType());
        return "fileView";
    }
    
    private DatafileMetadata fetchMetadata(File file) {
        EntityManager entityManager = 
                services.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<DatafileMetadata> query = entityManager.createQuery(
                    "from DatafileMetadata d where d.capturedFilePathname = :pathName", 
                    DatafileMetadata.class);
            query.setParameter("pathName", file.getAbsolutePath());
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            entityManager.close();
        }
    }
    
    private DatasetMetadata fetchMetadata(long id) {
        EntityManager entityManager = 
                services.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<DatasetMetadata> query = entityManager.createQuery(
                    "from DatasetMetadata d where d.id = :id", 
                    DatasetMetadata.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            entityManager.close();
        }
    }
}
