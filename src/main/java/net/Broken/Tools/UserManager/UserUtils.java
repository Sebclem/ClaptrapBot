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
     * return token's UserEntity
     *
     * @param userRepository User DB interface
     * @param token          Received token
     * @return User Entity
     * @throws UnknownTokenException Can't find token on User DB
     */
    public UserEntity getUserWithApiToken(UserRepository userRepository, String token) throws UnknownTokenException {
//        List<UserEntity> users = userRepository.findByApiToken(token);
//        if (users.size() > 0) {
//            return users.get(0);
//        } else
//            throw new UnknownTokenException();
        return null;

    }
}
