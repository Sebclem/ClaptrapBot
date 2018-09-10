package net.Broken.webView;

import net.Broken.DB.Entity.PlaylistEntity;
import net.Broken.DB.Entity.TrackEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.RestApi.Commands.Play;
import net.dv8tion.jda.core.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Web page controller for index
 */
@Controller
public class GeneralWebView {


    @RequestMapping("/")
    public String music(Model model,  @CookieValue(value = "guild", defaultValue = "1") String guildId){
        Guild guild = MainBot.jda.getGuildById(guildId);
        if(guild != null)
            model.addAttribute("guild_name", guild.getName());
        else
            model.addAttribute("guild_name", "");
        model.addAttribute("redirect_url", System.getenv("OAUTH_URL"));


        return CheckPage.getPageIfReady("index");
    }
    @RequestMapping("/loading")
    public String loading(Model model){
        return "loading";
    }

    @RequestMapping("/forgetPass")
    public String forgetPass(Model model){
        return CheckPage.getPageIfReady("forgetPass");
    }

    @RequestMapping("/oauthCallback")
    public String oauthCallback(Model model){
        return "oauthCallback";
    }




}
