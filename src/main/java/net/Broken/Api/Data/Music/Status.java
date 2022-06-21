package net.Broken.Api.Data.Music;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.Broken.Api.Data.Guild.Channel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Status(
        Boolean connected,
        Channel channel,
        Boolean canView,
        Boolean canInteract,
        PlayBackInfo playBackInfo
) {
}
