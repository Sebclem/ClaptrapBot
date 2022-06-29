package net.Broken.Api.Controllers;

import net.Broken.Api.Data.Guild.Channel;
import net.Broken.Api.Data.Guild.Guild;
import net.Broken.Api.Data.Guild.Role;
import net.Broken.Api.Data.InviteLink;
import net.Broken.Api.Security.Data.JwtPrincipal;
import net.Broken.Api.Services.GuildService;
import net.Broken.MainBot;
import net.dv8tion.jda.api.Permission;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public List<Guild> getMutualGuilds(Authentication authentication) {
        JwtPrincipal jwtPrincipal = (JwtPrincipal) authentication.getPrincipal();
        return guildService.getMutualGuilds(jwtPrincipal.user());
    }

    @GetMapping("inviteLink")
    public InviteLink getInviteLink() {
        String link = MainBot.jda.setRequiredScopes("applications.commands").getInviteUrl(Permission.getPermissions(1099511103319L));
        return new InviteLink(link);
    }

    @GetMapping("/{guildId}/voiceChannels")
    @PreAuthorize("isInGuild(#guildId)")
    public List<Channel> getVoiceChannels(@PathVariable String guildId, Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return guildService.getVoiceChannel(guildId, principal.user().getDiscordId());
    }

    @GetMapping("/{guildId}/textChannels")
    @PreAuthorize("isInGuild(#guildId)")
    public List<Channel> getTextChannels(@PathVariable String guildId, Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return guildService.getTextChannel(guildId, principal.user().getDiscordId());
    }

    @GetMapping("/{guildId}/roles")
    @PreAuthorize("isInGuild(#guildId)")
    public List<Role> getRoles(@PathVariable String guildId) {
        return guildService.getRole(guildId);
    }
}
