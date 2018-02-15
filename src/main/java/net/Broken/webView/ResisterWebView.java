package net.Broken.webView;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResisterWebView {
    @RequestMapping("/register")
    public String music(@RequestParam(value="id", required = true, defaultValue = "") String id, Model model){
        model.addAttribute("id", id);
        return "register";
    }
}
