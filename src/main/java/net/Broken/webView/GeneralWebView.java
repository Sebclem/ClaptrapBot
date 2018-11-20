package net.Broken.webView;

import net.Broken.DB.Entity.PlaylistEntity;
import net.Broken.DB.Entity.TrackEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.RestApi.Commands.Play;
import net.Broken.Tools.SettingsUtils;
import net.dv8tion.jda.core.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Web page controller for index
 */
@Controller
public class GeneralWebView {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public class ForbiddenException extends RuntimeException {}


    @RequestMapping("/")
    public String music(Model model,  @CookieValue(value = "guild", defaultValue = "1") String guildId, @CookieValue(value = "token", defaultValue = "") String token){
        Guild guild = MainBot.jda.getGuildById(guildId);
        if(guild != null)
            model.addAttribute("guild_name", guild.getName());
        else
            model.addAttribute("guild_name", "");
        model.addAttribute("redirect_url", System.getenv("OAUTH_URL"));
        model.addAttribute("isAdmin", SettingsUtils.getInstance().checkPermission(token, guildId));



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

    @RequestMapping("/settings")
    public String settings(Model model, @CookieValue(value = "guild", defaultValue = "") String guildId, @CookieValue(value = "token", defaultValue = "") String token){
        SettingsUtils settingsUtils = SettingsUtils.getInstance();
        if(settingsUtils.checkPermission(token, guildId)){
            Guild guild = MainBot.jda.getGuildById(guildId);
            if(guild != null)
                model.addAttribute("guild_name", guild.getName());
            else
                model.addAttribute("guild_name", "");
            model.addAttribute("redirect_url", System.getenv("OAUTH_URL"));
            model.addAttribute("settings", SettingsUtils.getInstance().extractSettings(guild));
            model.addAttribute("isAdmin", SettingsUtils.getInstance().checkPermission(token, guildId));



            return CheckPage.getPageIfReady("settings");
        }
        else
            throw new ForbiddenException();

    }

    @RequestMapping("/500")
    public String errorTest(Model model){
        return "error/500";
    }




}
