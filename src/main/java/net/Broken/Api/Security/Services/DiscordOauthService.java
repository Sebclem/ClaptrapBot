package net.Broken.Api.Security.Services;

import com.google.gson.Gson;
import net.Broken.Api.Security.Data.AccessTokenResponse;
import net.Broken.Api.Security.Data.DiscordOauthUserInfo;
import net.Broken.Api.Security.Exception.OAuthLoginFail;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class DiscordOauthService {

    private final Logger logger = LogManager.getLogger();
    @Value("${discord.client-id}")
    private String clientId;

    @Value("${discord.client-secret}")
    private String clientSecret;

    @Value("${discord.token-endpoint}")
    private String tokenEndpoint;

    @Value("${discord.tokenRevokeEndpoint}")
    private String tokenRevokeEndpoint;

    @Value("${discord.userInfoEnpoint}")
    private String userInfoEnpoint;

    private final UserRepository userRepository;

    public DiscordOauthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getAccessToken(String code, String redirectUrl) throws OAuthLoginFail {
        logger.debug("[OAUTH] Getting access token");
        HashMap<String, String> data = new HashMap<>();
        data.put("client_id", this.clientId);
        data.put("client_secret", this.clientSecret);
        data.put("grant_type", "authorization_code");
        data.put("code", code);
        data.put("redirect_uri", redirectUrl);
        try {
            HttpResponse<String> response = makeFormPost(this.tokenEndpoint, data);
            if (response.statusCode() != 200) {
                logger.warn("[OAUTH] Invalid response while getting AccessToken: Status Code: " + response.statusCode() + " Body:" + response.body());
                throw new OAuthLoginFail();
            }
            Gson gson = new Gson();
            AccessTokenResponse accessTokenResponse = gson.fromJson(response.body(), AccessTokenResponse.class);
            return accessTokenResponse.access_token;
        } catch (IOException | InterruptedException e) {
            logger.catching(e);
            throw new OAuthLoginFail();
        }
    }

    public DiscordOauthUserInfo getUserInfo(String token) throws OAuthLoginFail {
        logger.debug("[OAUTH] Getting user info");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.userInfoEnpoint))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.warn("[OAUTH] Invalid response while getting UserInfo: Status Code: " + response.statusCode() + " Body:" + response.body());
                throw new OAuthLoginFail();
            }
            Gson gson = new Gson();
            return gson.fromJson(response.body(), DiscordOauthUserInfo.class);
        } catch (IOException | InterruptedException e) {
            logger.catching(e);
            throw new OAuthLoginFail();
        }
    }

    public void revokeToken(String token) {
        logger.debug("[OAUTH] Revoking access token");
        HashMap<String, String> data = new HashMap<>();
        data.put("token", token);
        try {
            HttpResponse<String> response = makeFormPost(this.tokenRevokeEndpoint, data);
            if (response.statusCode() != 200) {
                logger.warn("OAUTH] Invalid response while token revocation: Status Code: " + response.statusCode() + " Body:" + response.body());
            }
        } catch (IOException | InterruptedException e) {
            logger.catching(e);
        }
    }


    public UserEntity loginOrRegisterDiscordUser(DiscordOauthUserInfo discordOauthUserInfo) {
        return userRepository
                .findByJdaId(discordOauthUserInfo.id)
                .orElseGet(() -> userRepository.save(new UserEntity(discordOauthUserInfo.username, discordOauthUserInfo.id)));
    }

    private String getFormString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    private HttpResponse<String> makeFormPost(String endpoint, HashMap<String, String> data) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(getFormString(data));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenEndpoint))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(body)
                .build();
        HttpClient client = HttpClient.newHttpClient();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
