package net.Broken.Api.Data.Music;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.Broken.Api.Data.Guild.Channel;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Status(
                Boolean connected,
                ConnectionStatus connectionStatus,
                Channel channel,
                Boolean canView,
                Boolean canInteract,
                PlayBackInfo playBackInfo) {
}
