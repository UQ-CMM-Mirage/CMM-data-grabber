package au.edu.uq.cmm.paul.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.base.BaseSingleFieldPeriod;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

    private static DateTimeFormatter[] FORMATS = new DateTimeFormatter[] {
        ISODateTimeFormat.dateHourMinuteSecond(),
        ISODateTimeFormat.localTimeParser(),
        ISODateTimeFormat.localDateOptionalTimeParser(),
        ISODateTimeFormat.dateTimeParser()
    };
    
    @Autowired
    Paul services;
    
    @RequestMapping(value="/control", method=RequestMethod.GET)
    public String control(Model model) {
        model.addAttribute("grabberStatus", "on");
        model.addAttribute("proxyStatus", "on");
        return "control";
    }
    
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
    
    @RequestMapping(value="/queue", method=RequestMethod.POST, 
            params={"deleteAll"})
    public String deleteAll(Model model, 
            @RequestParam(required=false) String mode, 
            @RequestParam(required=false) String confirmed) {
        if (confirmed == null) {
            return "queueDeleteConfirmation";
        }
        boolean discard = mode.equals("discard");
        int count = services.getQueueManager().deleteAll(discard);
        String verb = discard ? "deleted" : "archived";
        model.addAttribute("message",
                (count == 0 ? "No queue entries " :
                    count == 1 ? "1 queue entry " :
                        (count + " queue entries ")) + verb);
        model.addAttribute("returnTo", "queue");
        return "ok";
    }
    
    @RequestMapping(value="/queue", method=RequestMethod.POST, 
            params={"expire"})
    public String expire(Model model, 
            @RequestParam(required=false) String mode, 
            @RequestParam(required=false) String confirmed,
            @RequestParam(required=false) String olderThan,
            @RequestParam(required=false) String period,
            @RequestParam(required=false) String unit) {
        Date cutoff = determineCutoff(model, tidy(olderThan), tidy(period), tidy(unit));
        if (cutoff == null || confirmed == null) {
            return "queueExpiryForm";
        }
        int count = services.getQueueManager().expire(mode.equals("discard"), cutoff);
        model.addAttribute("message",
                count == 0 ? "No queue entries expired" :
                    count == 1 ? "1 queue entry expired" :
                        (count + " queue entries expired"));
        model.addAttribute("returnTo", "queue");
        return "ok";
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
    
    @RequestMapping(value="/queue/{entry:.+}", method=RequestMethod.POST, 
            params={"delete"})
    public String queueEntryDelete(@PathVariable String entry, Model model, 
            HttpServletResponse response,
            @RequestParam(required=false) String mode, 
            @RequestParam(required=false) String confirmed) 
            throws IOException {
        long id;
        try {
            id = Long.parseLong(entry);
        } catch (NumberFormatException ex) {
            LOG.debug("Rejected request with bad entry id");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        boolean discard = mode.equals("discard");
        services.getQueueManager().delete(id, discard);
        model.addAttribute("message",
                "Queue entry " + (discard ? "deleted" : "archived"));
        model.addAttribute("returnTo", "../queue");
        return "ok";
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
    
    private String tidy(String str) {
        return str == null ? "" : str.trim();
    }
    
    private Date determineCutoff(Model model, String olderThan, 
            String period, String unit) {
        if (olderThan.isEmpty() && period.isEmpty()) {
            model.addAttribute("message", 
                    "Either an expiry date or period must be supplied");
            return null;
        }
        DateTime cutoff;
        if (olderThan.isEmpty()) {
            int value;
            try {
                value = Integer.parseInt(period);
            } catch (NumberFormatException ex) {
                model.addAttribute("message", "Malformed period");
                return null;
            }
            BaseSingleFieldPeriod p;
            switch (unit) {
            case "minute" : case "minutes" :
                p = Minutes.minutes(value);
                break;
            case "hour" : case "hours" :
                p = Hours.hours(value);
                break;
            case "day" : case "days" :
                p = Days.days(value);
                break;
            case "week" : case "weeks" :
                p = Weeks.weeks(value);
                break;
            case "month" : case "months" :
                p = Months.months(value);
                break;
            case "year" : case "years" :
                p = Years.years(value);
                break;
            default :
                model.addAttribute("message", "Unrecognized unit");
                return null;
            }
            cutoff = DateTime.now().minus(p);
        } else {
            cutoff = null;
            for (DateTimeFormatter format : FORMATS) {
                try {
                    cutoff = format.parseDateTime(olderThan);
                    break;
                } catch (IllegalArgumentException ex) {
                    continue;
                }
            }
            if (cutoff == null) {
                model.addAttribute("message", "Unrecognizable expiry date");
                return null;
            }
        }
        if (cutoff.isAfter(new DateTime())) {
            model.addAttribute("message", "Expiry date is in the future");
            return null;
        }
        model.addAttribute("computedDate", FORMATS[0].print(cutoff));
        return cutoff.toDate();
    }
}
