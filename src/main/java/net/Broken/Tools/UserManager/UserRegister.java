package net.Broken.Tools.UserManager;

import net.Broken.DB.Entity.PendingUserEntity;
import net.Broken.DB.Repository.PendingUserRepository;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.UserManager.UserInfoData;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.PrivateMessage;
import net.Broken.Tools.ResourceLoader;
import net.Broken.Tools.UserManager.Exceptions.PasswordNotMatchException;
import net.Broken.Tools.UserManager.Exceptions.TokenNotMatch;
import net.Broken.Tools.UserManager.Exceptions.UserAlreadyRegistered;
import net.Broken.Tools.UserManager.Exceptions.UserNotFoundException;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

public class UserRegister {

    private Logger logger = LogManager.getLogger();


    public String sendCheckToken(PendingUserRepository pendingUserRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, UserInfoData userInfo) throws UserNotFoundException, PasswordNotMatchException, UserAlreadyRegistered {

        logger.info("New registration for " + userInfo.name);

        List<User> users = MainBot.jda.getUsersByName(userInfo.name,true);
        if(users.size() < 1)
            throw new UserNotFoundException();

        User user = users.get(0);
        logger.info("User found!");

        PendingUserEntity pendingUserEntity = null;
        String token = "";

        //Test if exist on register user
        if(userRepository.findByJdaId(user.getId()).size() > 0){
            logger.warn("User already registered!");
            throw new UserAlreadyRegistered();
        }



        //Check if exist in pading user Table
        List<PendingUserEntity> pendingUsers = pendingUserRepository.findByJdaId(user.getId());
        if(pendingUsers.size() != 0){
            PendingUserEntity thisUser = pendingUsers.get(0);
            if(passwordEncoder.matches(userInfo.password, thisUser.getPassword())){
                logger.info("Password matches");
                pendingUserEntity = thisUser;
                token = thisUser.getCheckToken();
            }else{
                logger.warn("Password Not Match!");
                throw new PasswordNotMatchException();
            }
        }



        logger.info("Generating check Token...");
        if(token.equals("")){
            token = generateCheckToken();
        }

        logger.info("Token generated: " + token);
        if(pendingUserEntity == null) {
            pendingUserEntity = new PendingUserEntity(user.getName(), user.getId(), token, passwordEncoder.encode(userInfo.password));
            pendingUserEntity = pendingUserRepository.save(pendingUserEntity);
        }

        String message = new ResourceLoader().getFile("MessagesTemplates/RegisterMessage.md");

        message = message.replace("%code",token);

        MessageEmbed ebM = EmbedMessageUtils.getRegister(message);
        PrivateMessage.send(user,ebM,logger);
        return pendingUserEntity.getId().toString();
        
    }

    public PendingUserEntity confirmCheckToken(PendingUserRepository pendingUserRepository, int id, String checkToken ) throws TokenNotMatch, UserNotFoundException {
        PendingUserEntity pendingUser = pendingUserRepository.findOne(id);
        if(pendingUser != null) {
            if(pendingUser.getCheckToken().equals(checkToken)){
                logger.info("Check Token match!");
            }
            else{
                logger.warn("Check token not match!");
                throw new TokenNotMatch();
            }
        }
        else{
            logger.warn("Id not Found!");
            throw new UserNotFoundException();
        }
        return pendingUser;
    }


    public String generateApiToken(){
        return UUID.randomUUID().toString();
    }

    public String generateCheckToken(){
        SecureRandom random = new SecureRandom();
        long longToken = Math.abs( random.nextLong() );
        String randomStr = Long.toString( longToken, 16 );
        randomStr = randomStr.substring(0,4);
        randomStr = randomStr.toUpperCase();
        return randomStr;
    }


}
