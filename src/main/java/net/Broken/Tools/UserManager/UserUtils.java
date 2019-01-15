package net.Broken.Tools.UserManager;

import net.Broken.DB.Entity.PendingUserEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.PendingUserRepository;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.UserManager.UserInfoData;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.PrivateMessage;
import net.Broken.Tools.ResourceLoader;
import net.Broken.Tools.UserManager.Exceptions.*;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

public class UserUtils {

    private Logger logger = LogManager.getLogger();

    private static UserUtils INSTANCE = new UserUtils();

    /**
     * Private default constructor
     */
    private UserUtils(){}

    /**
     * Singleton
     * @return Unique UserUtils instance
     */
    public static UserUtils getInstance(){
        return INSTANCE;
    }


    /**
     * Check if user exist on Guild, if exist, generate and send checkTocken, create entry on PendingUser DB
     * @param pendingUserRepository Pending user DB interface
     * @param userRepository User DB interface
     * @param passwordEncoder Password encoder
     * @param userInfo Received data
     * @return PendingUserEntity PK
     * @throws UserNotFoundException User not found in guild
     * @throws PasswordNotMatchException User already registered in PendingUser DB but password not match
     * @throws UserAlreadyRegistered User already registered in User DB
     */
    public String sendCheckToken(PendingUserRepository pendingUserRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, UserInfoData userInfo) throws UserNotFoundException, PasswordNotMatchException, UserAlreadyRegistered {

        logger.info("New registration for " + userInfo.name);

        List<User> users = MainBot.jda.getUsersByName(userInfo.name,true);
        if(users.size() < 1)
            throw new UserNotFoundException();

        User user = users.get(0);
        logger.info("User found!");

        PendingUserEntity pendingUserEntity = null;
        String token = "";

        //Preference if exist on register user
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
                logger.warn("Password don't match!");
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

        String message = null;
        try {
            message = new ResourceLoader().getFile("MessagesTemplates/RegisterMessage.md");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        message = message.replace("%code",token);

        MessageEmbed ebM = EmbedMessageUtils.getRegister(message);
        PrivateMessage.send(user,ebM,logger);
        return pendingUserEntity.getId().toString();
        
    }

    /**
     * Confirm user account
     * @param pendingUserRepository Pending user DB interface
     * @param id UserPendingEntity PK to cofirm
     * @param checkToken received token
     * @return PendingUserEntity
     * @throws TokenNotMatch Given token not match
     * @throws UserNotFoundException User not found in Pending user DB
     */
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

    /**
     * Get user Entity
     * @param userRepository User DB interface
     * @param passwordEncoder Password encoder
     * @param userInfoData Received data
     * @return User Entity
     * @throws UserNotFoundException User not found in User DB
     * @throws PasswordNotMatchException Given password not match
     */
    public UserEntity getUser(UserRepository userRepository, PasswordEncoder passwordEncoder, UserInfoData userInfoData) throws UserNotFoundException, PasswordNotMatchException {
        List<UserEntity> users = userRepository.findByName(userInfoData.name);
        if(users.size()<1){
            logger.warn("Login with unknown username: " + userInfoData.name);
            throw new UserNotFoundException();
        }
        else{
            UserEntity user = users.get(0);
            if(passwordEncoder.matches(userInfoData.password,user.getPassword())){
                logger.info("Login successful for " + user.getName());
                return user;
            }
            else
            {
                logger.warn("Login fail for " + user.getName() + ", wrong password!");
                throw new PasswordNotMatchException();
            }
        }
    }

    /**
     * return token's UserEntity
     * @param userRepository User DB interface
     * @param token Received token
     * @return User Entity
     * @throws UnknownTokenException Can't find token on User DB
     */
    public UserEntity getUserWithApiToken(UserRepository userRepository, String token) throws UnknownTokenException {
        List<UserEntity> users = userRepository.findByApiToken(token);
        if(users.size() > 0){
            return users.get(0);
        }
        else
            throw new UnknownTokenException();

    }

    /**
     * Generate API Token
     * @return UUID String TODO Find something more secure
     */
    public String generateApiToken(){
        return UUID.randomUUID().toString();
    }

    /**
     * Generate short check token
     * @return check token as string
     */
    public String generateCheckToken(){
        SecureRandom random = new SecureRandom();
        long longToken = Math.abs( random.nextLong() );
        String randomStr = Long.toString( longToken, 16 );
        randomStr = randomStr.substring(0,4);
        randomStr = randomStr.toUpperCase();
        return randomStr;
    }


}
