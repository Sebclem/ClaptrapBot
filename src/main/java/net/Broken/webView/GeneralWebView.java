package net.Broken.webView;

import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.Tools.CacheTools;
import net.Broken.Tools.SettingsUtils;
import net.Broken.Tools.UserManager.Exceptions.UnknownTokenException;
import net.Broken.Tools.UserManager.Stats.GuildStatsPack;
import net.Broken.Tools.UserManager.Stats.UserStatsUtils;
import net.Broken.Tools.UserManager.UserUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Web page controller for index
 */
@Controller
public class GeneralWebView {

    private UserRepository userRepository;

    private UserUtils userUtils = UserUtils.getInstance();

    private Logger logger = LogManager.getLogger();


    @Autowired
    public GeneralWebView(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @ResponseStatus(HttpStatus.FORBIDDEN)
    public class ForbiddenException extends RuntimeException {
    }


    @RequestMapping("/")
    public String music(Model model, HttpServletResponse response, HttpServletRequest request, @CookieValue(value = "guild", defaultValue = "1") String guildId, @CookieValue(value = "token", defaultValue = "") String token) {
        if (token.equals("")) {
            model.addAttribute("isLogged", false);
            model.addAttribute("guild_name", "");
            model.addAttribute("isAdmin", false);
            model.addAttribute("inviteLink", "https://discordapp.com/oauth2/authorize?client_id=" + MainBot.jda.getSelfUser().getId() + "&scope=bot&permissions=8");

            return CheckPage.getPageIfReady("index");
        }

        try {

            UserEntity userE = userUtils.getUserWithApiToken(userRepository, token);
            User user = MainBot.jda.getUserById(userE.getJdaId());
            if (user == null) {
                model.addAttribute("noMutualGuilds", true);
                addGuildAndRedirect(model, token, guildId, user);
            } else {
                model.addAttribute("noMutualGuilds", false);
                addGuildAndRedirect(model, token, guildId, user);
            }

            model.addAttribute("isAdmin", SettingsUtils.getInstance().checkPermission(token, guildId));
            model.addAttribute("isLogged", true);
            model.addAttribute("inviteLink", "https://discordapp.com/oauth2/authorize?client_id=" + MainBot.jda.getSelfUser().getId() + "&scope=bot&permissions=8");
            return CheckPage.getPageIfReady("index");

        } catch (UnknownTokenException e) {
            logger.debug("Unknown token, clear cookies");
            Cookie[] cookies = request.getCookies();
            logger.debug(cookies);
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                cookie.setValue(null);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            model.addAttribute("redirect_url", System.getenv("OAUTH_URL"));
            return CheckPage.getPageIfReady("login");

        } catch (NumberFormatException e) {
            logger.debug("Unknown guild, flush cookies");
            Cookie cookie = new Cookie("guild", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return "redirect:/";
        }


    }


    @RequestMapping("/loading")
    public String loading(Model model) {
        return "loading";
    }

    @RequestMapping("/forgetPass")
    public String forgetPass(Model model) {
        return CheckPage.getPageIfReady("forgetPass");
    }

    @RequestMapping("/oauthCallback")
    public String oauthCallback(Model model) {
        return "oauthCallback";
    }

    @RequestMapping("/settings")
    public String settings(Model model, @CookieValue(value = "guild", defaultValue = "") String guildId, @CookieValue(value = "token", defaultValue = "") String token) {
        SettingsUtils settingsUtils = SettingsUtils.getInstance();
        if (settingsUtils.checkPermission(token, guildId)) {
            try {
                UserEntity userE = userUtils.getUserWithApiToken(userRepository, token);
                User user = MainBot.jda.getUserById(userE.getJdaId());
                addGuildAndRedirect(model, token, guildId, user);
                Guild guild = MainBot.jda.getGuildById(guildId);
                model.addAttribute("settings", SettingsUtils.getInstance().extractSettings(guild));
            } catch (UnknownTokenException e) {
                throw new ForbiddenException();
            }


            return CheckPage.getPageIfReady("settings");
        } else
            throw new ForbiddenException();

    }

    @RequestMapping("/login")
    public String login(Model model, @CookieValue(value = "token", defaultValue = "") String token) {
        model.addAttribute("redirect_url", System.getenv("OAUTH_URL"));
        if (!token.equals("")) {
            return "redirect:/";
        } else
            return "login";
    }


    @RequestMapping("/rank")
    public String login(Model model, @CookieValue(value = "token", defaultValue = "1") String token, @CookieValue(value = "guild", defaultValue = "") String cookieGuildId, @RequestParam(value = "guild", defaultValue = "") String praramGuildId) {
        model.addAttribute("redirect_url", System.getenv("OAUTH_URL"));
        try {
            UserEntity userEntity = userUtils.getUserWithApiToken(userRepository, token);
            GuildStatsPack stack;
            if (!cookieGuildId.equals("")) {
                stack = UserStatsUtils.getINSTANCE().getStatPack(userEntity, cookieGuildId);


            } else
                stack = null;
            model.addAttribute("stack", stack);
            try {
                UserEntity userE = userUtils.getUserWithApiToken(userRepository, token);
                User user = MainBot.jda.getUserById(userE.getJdaId());
                addGuildAndRedirect(model, token, cookieGuildId, user);
            } catch (UnknownTokenException e) {
                throw new ForbiddenException();
            }
            return CheckPage.getPageIfReady("rank");

        } catch (UnknownTokenException e) {
            return "login"; // TODO Public rank
        }

    }


    public static Model addGuildAndRedirect(Model model, String token, String guildId, User user) {
        List<Guild> mutualGuilds = user.getMutualGuilds();
        Integer lastCount = MainBot.mutualGuildCount.get(user.getId());
        if (lastCount == null || lastCount != mutualGuilds.size()) {
            LogManager.getLogger().debug("Guild miss match, re-cache users...");
            CacheTools.loadAllGuildMembers();
            mutualGuilds = user.getMutualGuilds();
            MainBot.mutualGuildCount.put(user.getId(), mutualGuilds.size());
        }

        Guild guild = MainBot.jda.getGuildById(guildId);
        if (guild != null) {
            model.addAttribute("guild_name", guild.getName());
            model.addAttribute("guild_id", guild.getId());
            model.addAttribute("guild_icon", guild.getIconUrl() == null ? "https://discordapp.com/assets/dd4dbc0016779df1378e7812eabaa04d.png" : guild.getIconUrl());
            model.addAttribute("mutual_guilds", mutualGuilds);
            model.addAttribute("isAdmin", SettingsUtils.getInstance().checkPermission(token, guildId));
        } else {
            model.addAttribute("guild_name", "");
            model.addAttribute("guild_icon", "");
        }

        model.addAttribute("redirect_url", System.getenv("OAUTH_URL"));
        return model;
    }


}
