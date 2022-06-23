package net.Broken.Api.Controllers;

import net.Broken.Api.Data.Music.Connect;
import net.Broken.Api.Data.Music.Status;
import net.Broken.Api.Security.Data.JwtPrincipal;
import net.Broken.Api.Services.AudioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/audio")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AudioController {

    public final AudioService audioService;

    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }


    @GetMapping("/{guildId}/status")
    @PreAuthorize("isInGuild(#guildId)")
    public Status getMusicStatus(@PathVariable String guildId, Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return audioService.getGuildAudioStatus(guildId, principal.user().getDiscordId());
    }


    @PostMapping("/{guildId}/connect")
    @PreAuthorize("isInGuild(#guildId) && canInteractWithVoiceChannel(#guildId, #body)")
    public ResponseEntity<Status> connect(@PathVariable String guildId, @RequestBody Connect body, Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return audioService.connect(guildId, body, principal.user().getDiscordId());
    }

    @PostMapping("/{guildId}/disconnect")
    @PreAuthorize("isInGuild(#guildId) && canInteractWithVoiceChannel(#guildId)")
    public ResponseEntity<Status> disconnect(@PathVariable String guildId, Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return audioService.disconnect(guildId, principal.user().getDiscordId());
    }
}
