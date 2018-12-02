package net.Broken.webView;

import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.Tools.SettingsUtils;
import net.Broken.Tools.UserManager.Exceptions.UnknownTokenException;
import net.Broken.Tools.UserManager.UserUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Web page controller for /music page
 */
@Controller
public class MusicWebView {

    UserRepository userRepository;

    UserUtils userUtils = UserUtils.getInstance();

    Logger logger = LogManager.getLogger();


    @Autowired
    public MusicWebView(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @RequestMapping("/music")
    public String music(Model model, HttpServletResponse response, HttpServletRequest request, @CookieValue(value = "guild", defaultValue = "1") String guildId, @CookieValue(value = "token", defaultValue = "") String token){
        if(token.equals("")){
            model.addAttribute("redirect_url", System.getenv("OAUTH_URL"));
            return "login";
        }
        try {
            UserEntity userE = userUtils.getUserWithApiToken(userRepository, token);
            User user = MainBot.jda.getUserById(userE.getJdaId());
            if(user == null)
                return "redirect:/";
            Guild guild = MainBot.jda.getGuildById(guildId);
            if(guild != null)
                model.addAttribute("guild_name", guild.getName());
            else
                model.addAttribute("guild_name", "");
            model.addAttribute("isAdmin", SettingsUtils.getInstance().checkPermission(token, guildId));
            return CheckPage.getPageIfReady("music");

        } catch (UnknownTokenException e) {
            logger.debug("Unknown token, flush cookies");
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                cookie.setValue(null);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            model.addAttribute("redirect_url", System.getenv("OAUTH_URL"));
            return "login";

        } catch (NumberFormatException e){
            logger.debug("Unknown guild, flush cookies");
            Cookie cookie = new Cookie("guild", null); // Not necessary, but saves bandwidth.
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0); // Don't set to -1 or it will become a session cookie!
            response.addCookie(cookie);
            return "redirect:music";
        }



    }
}
