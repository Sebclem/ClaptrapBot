package net.Broken.webView;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Web page controller for index
 */
@Controller
public class GeneralWebView {
    @RequestMapping("/")
    public String music(Model model){

        return CheckPage.getPageIfReady("index");
    }
    @RequestMapping("/loading")
    public String loading(Model model){
        return "loading";
    }
}
