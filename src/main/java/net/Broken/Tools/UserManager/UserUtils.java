package net.Broken.Tools.UserManager;

import net.Broken.DB.Entity.PendingUserEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.PendingUserRepository;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.UserManager.UserInfoData;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.ResourceLoader;
import net.Broken.Tools.UserManager.Exceptions.*;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

public class UserUtils {

    private static UserUtils INSTANCE = new UserUtils();
    private Logger logger = LogManager.getLogger();

    /**
     * Private default constructor
     */
    private UserUtils() {
    }

    /**
     * Singleton
     *
     * @return Unique UserUtils instance
     */
    public static UserUtils getInstance() {
        return INSTANCE;
    }
    /**
     * Get user Entity
     *
     * @param userRepository  User DB interface
     * @param passwordEncoder Password encoder
     * @param userInfoData    Received data
     * @return User Entity
     * @throws UserNotFoundException     User not found in User DB
     * @throws PasswordNotMatchException Given password not match
     */
    public UserEntity getUser(UserRepository userRepository, PasswordEncoder passwordEncoder, UserInfoData userInfoData) throws UserNotFoundException, PasswordNotMatchException {
        List<UserEntity> users = userRepository.findByName(userInfoData.name);
        if (users.size() < 1) {
            logger.warn("Login with unknown username: " + userInfoData.name);
            throw new UserNotFoundException();
        } else {
            UserEntity user = users.get(0);
            if (passwordEncoder.matches(userInfoData.password, user.getPassword())) {
                logger.info("Login successful for " + user.getName());
                return user;
            } else {
                logger.warn("Login fail for " + user.getName() + ", wrong password!");
                throw new PasswordNotMatchException();
            }
        }
    }

    /**
     * return token's UserEntity
     *
     * @param userRepository User DB interface
     * @param token          Received token
     * @return User Entity
     * @throws UnknownTokenException Can't find token on User DB
     */
    public UserEntity getUserWithApiToken(UserRepository userRepository, String token) throws UnknownTokenException {
        List<UserEntity> users = userRepository.findByApiToken(token);
        if (users.size() > 0) {
            return users.get(0);
        } else
            throw new UnknownTokenException();

    }

    /**
     * Generate API Token
     *
     * @return UUID String TODO Find something more secure
     */
    public String generateApiToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate short check token
     *
     * @return check token as string
     */
    public String generateCheckToken() {
        SecureRandom random = new SecureRandom();
        long longToken = Math.abs(random.nextLong());
        String randomStr = Long.toString(longToken, 16);
        randomStr = randomStr.substring(0, 4);
        randomStr = randomStr.toUpperCase();
        return randomStr;
    }


}
