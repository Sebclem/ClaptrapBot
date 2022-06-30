package net.Broken.Api.Security.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DiscordOauthUserInfo(
        String id,
        String username,
        String discriminator,
        String avatar) {
}

