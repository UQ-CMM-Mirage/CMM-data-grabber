package au.edu.uq.cmm.paul.servlet;

import java.io.File;
import java.io.IOException;

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

/**
 * The MVC controller for Paul's web UI.  This supports the status and configuration
 * pages and also implements GET access to the files in the queue area.
 * 
 * @author scrawley
 */
@Controller
public class WebUIController {
    private static final Logger LOG = Logger.getLogger(WebUIController.class);
    // FIXME - hard-wired == BAD
    private final String directory = "/tmp/safe";
    
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
    
    @RequestMapping(value="/files/{fileName:.+}", method=RequestMethod.GET)
    public String file(@PathVariable String fileName, Model model, HttpServletResponse response) 
            throws IOException {
        LOG.debug("Request to fetch file " + fileName);
        // This aims to prevent requests from reading files outside of the queue directory.
        // FIXME - this assumes that the directory for the queue is flat ...
        if (fileName.contains("/") || fileName.equals("..")) {
            LOG.debug("Rejected request for security reasons");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        File file = new File(directory, fileName);
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
        return null;
    }
}
