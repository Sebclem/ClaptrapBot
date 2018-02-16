package net.Broken.webView;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GeneralWebView {
    @RequestMapping("/")
    public String music(Model model){
        return "index";
    }
}
