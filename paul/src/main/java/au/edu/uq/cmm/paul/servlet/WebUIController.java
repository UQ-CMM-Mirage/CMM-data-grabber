package au.edu.uq.cmm.paul.servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import au.edu.uq.cmm.paul.Paul;

@Controller
public class WebUIController {
    
    @Autowired
    Paul services;
    
    @RequestMapping(value="/status")
    public String status(Model model) {
        model.addAttribute("facilities", services.getFacilitySessionManager().getSnapshot());
        return "status";
    }
    
    @RequestMapping(value="/config")
    public String config(Model model) {
        model.addAttribute("configuration", services.getConfiguration());
        return "config";
    }
    
    @RequestMapping(value="/queue")
    public String queue(Model model) {
        model.addAttribute("queue", services.getQueueManager().getSnapshot());
        return "queue";
    }
}
