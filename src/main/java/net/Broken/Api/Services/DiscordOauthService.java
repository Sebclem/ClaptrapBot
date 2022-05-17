package net.Broken.Api.Services;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

@Service
public class DiscordOauthService {

    @Value("${discord.client-id}")
    private String clientId;

    @Value("${discord.client-secret}")
    private String clientSecret;

    @Value("${discord.token-endpoint}")
    private String tokenEndpoint;

    public String getAccessToken(String code, String redirectUrl){
        HashMap<String, String> data = new HashMap<>();
        data.put("client_id", this.clientId);
        data.put("client_secret", this.clientSecret);
        data.put("grant_type", "authorization_code");
        data.put("code", code);
        data.put("redirect_uri", redirectUrl);

        Gson gson = new Gson();
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(data));


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenEndpoint))
                .header("Content-Type", "application/json")
                .POST(body)
                .build();
        HttpClient client = HttpClient.newHttpClient();
        client.send(request, HttpResponse.BodyHandlers.ofString());

    }
}
