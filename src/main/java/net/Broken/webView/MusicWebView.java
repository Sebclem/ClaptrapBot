package net.Broken.webView;

import net.Broken.MainBot;
import net.Broken.Tools.SettingsUtils;
import net.dv8tion.jda.core.entities.Guild;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Web page controller for /music page
 */
@Controller
public class MusicWebView {
    @RequestMapping("/music")
    public String music(Model model,  @CookieValue(value = "guild", defaultValue = "1") String guildId, @CookieValue(value = "token", defaultValue = "1") String token){
        Guild guild = MainBot.jda.getGuildById(guildId);
        if(guild != null)
            model.addAttribute("guild_name", guild.getName());
        else
            model.addAttribute("guild_name", "");
        model.addAttribute("redirect_url", System.getenv("OAUTH_URL"));
        model.addAttribute("isAdmin", SettingsUtils.getInstance().checkPermission(token, guildId));

        return CheckPage.getPageIfReady("music");
    }
}
