package net.Broken.webView;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MusicWebView {
    @RequestMapping("/music")
    public String music(Model model){
        return "music";
    }
}
