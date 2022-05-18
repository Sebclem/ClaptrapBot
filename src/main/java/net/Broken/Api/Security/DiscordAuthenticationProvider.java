package net.Broken.Api.Security;

import net.Broken.Api.Security.Data.DiscordOauthUserInfo;
import net.Broken.Api.Security.Exception.OAuthLoginFail;
import net.Broken.Api.Security.Services.DiscordOauthService;
import net.Broken.DB.Entity.UserEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DiscordAuthenticationProvider implements AuthenticationProvider {
    private final Logger logger = LogManager.getLogger();
    private final DiscordOauthService discordOauthService;

    public DiscordAuthenticationProvider(DiscordOauthService discordOauthService) {
        this.discordOauthService = discordOauthService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String redirectUri = authentication.getPrincipal().toString();
        String code = authentication.getCredentials().toString();
        try {
            String token = discordOauthService.getAccessToken(code, redirectUri);
            DiscordOauthUserInfo discordOauthUserInfo = discordOauthService.getUserInfo(token);
            discordOauthService.revokeToken(token);
            UserEntity userEntity = discordOauthService.loginOrRegisterDiscordUser(discordOauthUserInfo);
            return new UsernamePasswordAuthenticationToken(userEntity, null, new ArrayList<>());

        } catch (OAuthLoginFail e) {
            throw new BadCredentialsException("Bad response form Discord Oauth server ! Code expired ?");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
