package au.edu.uq.cmm.paul.servlet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FacilityStatusController {

    @RequestMapping(value="/status")
    public String status() {
        return "WEB-INF/views/status.jsp";
    }
}
