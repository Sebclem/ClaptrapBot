package net.Broken.webView;

import net.Broken.MainBot;
import net.dv8tion.jda.core.entities.Guild;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;

@Controller
public class MyErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model, @CookieValue(value = "guild", defaultValue = "1") String guildId){
        Guild guild = MainBot.jda.getGuildById(guildId);
        if(guild != null)
            model.addAttribute("guild_name", guild.getName());
        else
            model.addAttribute("guild_name", "");
        model.addAttribute("redirect_url", System.getenv("OAUTH_URL"));
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            }
            else if(statusCode == HttpStatus.FORBIDDEN.value()){
                return "error/403";
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()){
                return "error/500";
            }

        }
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
