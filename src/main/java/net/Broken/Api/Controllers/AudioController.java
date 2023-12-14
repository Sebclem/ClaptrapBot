package net.Broken.Api.Controllers;

import net.Broken.Api.Data.Music.Add;
import net.Broken.Api.Data.Music.Connect;
import net.Broken.Api.Data.Music.Status;
import net.Broken.Api.Security.Data.JwtPrincipal;
import net.Broken.Api.Services.AudioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v2/audio")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AudioController {

    public final AudioService audioService;

    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }

    @GetMapping("/{guildId}/status")
    @PreAuthorize("@webSecurity.isInGuild(#guildId)")
    public Status getMusicStatus(@PathVariable String guildId, Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return audioService.getGuildAudioStatus(guildId, principal.user().getDiscordId());
    }

    @PostMapping("/{guildId}/connect")
    @PreAuthorize("@webSecurity.isInGuild(#guildId) && @webSecurity.canInteractWithVoiceChannel(#guildId, #body)")
    public ResponseEntity<Status> connect(@PathVariable String guildId, @RequestBody Connect body,
            Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return audioService.connect(guildId, body, principal.user().getDiscordId());
    }

    @PostMapping("/{guildId}/disconnect")
    @PreAuthorize("@webSecurity.isInGuild(#guildId) && @webSecurity.canInteractWithVoiceChannel(#guildId)")
    public ResponseEntity<Status> disconnect(@PathVariable String guildId, Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return audioService.disconnect(guildId, principal.user().getDiscordId());
    }

    @PostMapping("/{guildId}/resume")
    @PreAuthorize("@webSecurity.isInGuild(#guildId) && @webSecurity.canInteractWithVoiceChannel(#guildId)")
    public ResponseEntity<Status> resume(@PathVariable String guildId, Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return audioService.resume(guildId, principal.user().getDiscordId());
    }

    @PostMapping("/{guildId}/pause")
    @PreAuthorize("@webSecurity.isInGuild(#guildId) && @webSecurity.canInteractWithVoiceChannel(#guildId)")
    public ResponseEntity<Status> pause(@PathVariable String guildId, Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return audioService.pause(guildId, principal.user().getDiscordId());
    }

    @PostMapping("/{guildId}/skip")
    @PreAuthorize("@webSecurity.isInGuild(#guildId) && @webSecurity.canInteractWithVoiceChannel(#guildId)")
    public ResponseEntity<Status> skip(@PathVariable String guildId, Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return audioService.skip(guildId, principal.user().getDiscordId());
    }

    @PostMapping("/{guildId}/stop")
    @PreAuthorize("@webSecurity.isInGuild(#guildId) && @webSecurity.canInteractWithVoiceChannel(#guildId)")
    public ResponseEntity<Status> stop(@PathVariable String guildId, Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return audioService.stop(guildId, principal.user().getDiscordId());
    }

    @PostMapping("/{guildId}/add")
    @PreAuthorize("@webSecurity.isInGuild(#guildId) && @webSecurity.canInteractWithVoiceChannel(#guildId)")
    public ResponseEntity<Status> add(@PathVariable String guildId, @RequestBody Add body,
            Authentication authentication) throws ExecutionException, InterruptedException {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return audioService.add(guildId, principal.user().getDiscordId(), body);
    }
}
