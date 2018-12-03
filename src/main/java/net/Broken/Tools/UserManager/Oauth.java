package net.Broken.Tools.UserManager;

import jdk.nashorn.internal.parser.JSONParser;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Oauth {
    private static Oauth INSTANCE = new Oauth();
    public static Oauth getInstance(){ return INSTANCE; }

    Logger logger = LogManager.getLogger();
    private String baseUrl = "https://discordapp.com/api";
    private String mePath = "/users/@me";



    private JSONObject getUserId(String token){
        StringBuffer content = new StringBuffer();
        try {
            String httpsURL = baseUrl+mePath;
            URL myUrl = new URL(httpsURL);
            HttpURLConnection con = (HttpURLConnection)myUrl.openConnection();
            con.setRequestProperty("Authorization", "Bearer "+token);
            con.setRequestProperty("User-Agent", "DiscordBot (claptrapbot.com, 0.1)");
            con.setRequestMethod("GET");
            logger.debug("Response code: " + con.getResponseCode());
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject json = new JSONObject(content.toString());
        logger.debug(json);


        return json;
    }


    public UserEntity getUserEntity(String token, UserRepository userRepository, PasswordEncoder passwordEncoder){
        JSONObject discorResponse = getUserId(token);
        List<UserEntity> userEntitys = userRepository.findByJdaId(discorResponse.getString("id"));
        if(userEntitys.size() != 0){
            return userEntitys.get(0);
        }else{
            User jdaUser = MainBot.jda.getUserById(discorResponse.getString("id"));
            UserEntity user;
            if( jdaUser == null)
                user = new UserEntity(discorResponse.getString("username"), discorResponse.getString("id"), passwordEncoder);
            else
                user = new UserEntity(MainBot.jda.getUserById(discorResponse.getString("id")), passwordEncoder);
            user = userRepository.save(user);
            return user;
        }
    }
}
