package au.edu.uq.cmm.paul.servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import au.edu.uq.cmm.paul.Paul;

@Controller
public class WebUIController {
    
    @Autowired
    Paul services;
    
    @RequestMapping(value="/status", method=RequestMethod.GET)
    public String status(Model model) {
        model.addAttribute("facilities", services.getFacilitySessionManager().getSnapshot());
        return "status";
    }
    
    @RequestMapping(value="/config", method=RequestMethod.GET)
    public String config(Model model) {
        model.addAttribute("configuration", services.getConfiguration());
        return "config";
    }
    
    @RequestMapping(value="/files/{fileName:.+}", method=RequestMethod.GET)
    public String file(@PathVariable String fileName, Model model) {
        model.addAttribute("fileName", fileName);
        return "fileView";
    }
}
