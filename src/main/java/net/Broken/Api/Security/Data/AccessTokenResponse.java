package net.Broken.Api.Security.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public record AccessTokenResponse(
        String access_token,
        String token_type,
        String expires_in,
        String refresh_token,
        String scope
) {
}
