package net.Broken.RestApi;

import net.Broken.DB.Entity.PendingUserEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.PendingUserRepository;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.UserManager.*;
import net.Broken.Tools.UserManager.Exceptions.*;
import net.Broken.Tools.UserManager.Oauth;
import net.Broken.Tools.UserManager.UserUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Rest Api controller for /api/userManagement
 */
@RestController
@RequestMapping("/api/userManagement")
public class UserManagerAPIController {
    Logger logger = LogManager.getLogger();
    final
    PendingUserRepository pendingUserRepository;

    final
    UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    UserUtils userUtils = UserUtils.getInstance();

    @Autowired
    public UserManagerAPIController(PendingUserRepository pendingUserRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.pendingUserRepository = pendingUserRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @RequestMapping(value = "/preRegister", method = RequestMethod.POST)
    public ResponseEntity<CheckResposeData> command(@RequestBody UserInfoData data){
        if(data != null && data.name != null) {
            try {
                String id = userUtils.sendCheckToken(pendingUserRepository, userRepository, passwordEncoder, data);
                return new ResponseEntity<>(new CheckResposeData(true, data.name, "Message sent", id), HttpStatus.OK);
            } catch (UserNotFoundException e) {
                logger.warn("User \"" + data.name + "\" not found!");
                return new ResponseEntity<>(new CheckResposeData(false, data.name, "User not found on server!",""), HttpStatus.NOT_FOUND);
            } catch (PasswordNotMatchException userAlreadyRegistered) {
                return new ResponseEntity<>(new CheckResposeData(false, data.name, "User already registered in pending database and password not match!",""), HttpStatus.NOT_ACCEPTABLE);

            } catch (UserAlreadyRegistered userAlreadyRegistered) {
                return new ResponseEntity<>(new CheckResposeData(false, data.name, "User already registered in database!",""), HttpStatus.NOT_ACCEPTABLE);
            }
        }
        else{
            return new ResponseEntity<>(new CheckResposeData(false, "", "Missing parameter(s)",""), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/confirmAccount", method = RequestMethod.POST)
    public ResponseEntity<UserConnectionData> confirAccount(@RequestBody ConfirmData data){
        try {
            PendingUserEntity pUser = userUtils.confirmCheckToken(pendingUserRepository, Integer.parseInt(data.id), data.checkToken);
            UserEntity user = new UserEntity(pUser, userUtils.generateApiToken());
            userRepository.save(user);
            pendingUserRepository.delete(pUser);

            return new ResponseEntity<>(new UserConnectionData(true, user.getName(), user.getApiToken(),""),HttpStatus.OK);
        } catch (TokenNotMatch tokenNotMatch) {
            logger.warn("Pre token not match for "+data.id+"!");
            return new ResponseEntity<>(new UserConnectionData(false,"Token not match!","token"),HttpStatus.NOT_ACCEPTABLE);
        } catch (UserNotFoundException e) {
            logger.warn("Id not found in DB ("+data.id+")");
            return new ResponseEntity<>(new UserConnectionData(false,"User not found on DB!", "user"),HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @RequestMapping(value = "/requestToken", method = RequestMethod.POST)
    public ResponseEntity<UserConnectionData> requestToken(@RequestBody UserInfoData data){
        try {
            UserEntity user = userUtils.getUser(userRepository, passwordEncoder, data);
            return new ResponseEntity<>(new UserConnectionData(true, user.getName(), user.getApiToken(), ""), HttpStatus.OK);

        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(new UserConnectionData(false,"User not registered!", "user"),HttpStatus.NOT_ACCEPTABLE);
        } catch (PasswordNotMatchException e) {
            return new ResponseEntity<>(new UserConnectionData(false,"Wrong user name or password!", "password"),HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @RequestMapping(value = "/getGuilds", method = RequestMethod.GET)
    public ResponseEntity<List<GuildInfo>> getGuilds(@CookieValue("token") String token){
        try {
            UserEntity userE = userUtils.getUserWithApiToken(userRepository, token);
            User user = MainBot.jda.getUserById(userE.getJdaId());
            List<GuildInfo> temp = new ArrayList<>();

            for (Guild guild : user.getMutualGuilds()){

                temp.add(new GuildInfo(guild.getName(), guild.getId(), guild.getMember(user).hasPermission(Permission.ADMINISTRATOR)));
            }
            return new ResponseEntity<>(temp, HttpStatus.OK);


        } catch (UnknownTokenException e) {
            logger.catching(e);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }




    @RequestMapping(value = "/oauthLogin", method = RequestMethod.POST)
    public ResponseEntity<UserConnectionData> oauthLogin(@RequestParam(value = "token") String discordToken){
        logger.debug(discordToken);
        UserEntity user = Oauth.getInstance().getUserEntity(discordToken, userRepository);
        logger.info("OAuth login for " + user.getName());
        return new ResponseEntity<>(new UserConnectionData(true, user.getName(), user.getApiToken(), ""), HttpStatus.OK);



    }




}
