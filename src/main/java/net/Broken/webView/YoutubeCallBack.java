package net.Broken.webView;


import net.Broken.audio.Youtube.Receiver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class YoutubeCallBack {
    @RequestMapping("/youtube/callback")
    public String callback(@RequestParam(value="error", required = false, defaultValue = "") String error,
                           @RequestParam(value = "code", required = false, defaultValue = "") String code,
                           Model model){
        model.addAttribute("error", error);
        model.addAttribute("code", code);
        if(!code.equals("")){
            Receiver.getInstance(null).setCode(code);
        }
        return "youtubeCallBack";
    }
}
