package net.Broken.Api.Data.Music;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlayBackInfo(
        Boolean paused,
        Boolean stopped,
        Long progress,
        TrackInfo trackInfo
) {
}
