package net.Broken.Api.Controllers;

import net.Broken.Api.Data.Guild;
import net.Broken.Api.Data.InviteLink;
import net.Broken.Api.Security.Data.JwtPrincipal;
import net.Broken.Api.Services.GuildService;
import net.Broken.MainBot;
import net.dv8tion.jda.api.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/guild")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GuildController {

    public final GuildService guildService;

    public GuildController(GuildService guildService) {
        this.guildService = guildService;
    }

    @GetMapping("mutual")
    public List<Guild> getMutualGuilds(Authentication authentication){
        JwtPrincipal jwtPrincipal = (JwtPrincipal) authentication.getPrincipal();
        return guildService.getMutualGuilds(jwtPrincipal.user());
    }

    @GetMapping("inviteLink")
    public InviteLink getInviteLink(){
        String link = MainBot.jda.getInviteUrl(Permission.ADMINISTRATOR);
        return new InviteLink(link);
    }
}
