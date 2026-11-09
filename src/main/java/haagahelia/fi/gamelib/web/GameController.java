package haagahelia.fi.gamelib.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GameController {

    @RequestMapping("/")
    public String index() {
        return "redirect:/privateLibrary";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value="/privateLibrary")
    public String gameLibrary() {	
        return "privateLibrary";
    }
    
}
