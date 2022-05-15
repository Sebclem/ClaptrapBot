package net.Broken.Tools.UserManager;

import net.Broken.DB.Entity.PendingPwdResetEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.PendingPwdResetRepository;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.SpringContext;
import net.Broken.Tools.UserManager.Exceptions.TokenNotMatch;
import net.Broken.Tools.UserManager.Exceptions.UserNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class PasswordResetUtils {
    private static PasswordResetUtils INSTANCE = new PasswordResetUtils();
    private Logger logger = LogManager.getLogger();
    private PasswordEncoder passwordEncoder;
    private PendingPwdResetRepository pendingPwdResetRepository;
    private UserRepository userRepository;

    /**
     * Private default constructor
     */
    private PasswordResetUtils() {
        ApplicationContext context = SpringContext.getAppContext();
        passwordEncoder = (PasswordEncoder) context.getBean("passwordEncoder");
        pendingPwdResetRepository = (PendingPwdResetRepository) context.getBean("pendingPwdResetRepository");
        userRepository = (UserRepository) context.getBean("userRepository");
    }


    /**
     * Singleton
     *
     * @return Unique PasswordResetUtils instance
     */
    public static PasswordResetUtils getInstance() {
        return INSTANCE;
    }

    public String resetRequest(UserEntity userEntity) {
        String token = UserUtils.getInstance().generateCheckToken();
        String encodedToken = passwordEncoder.encode(token);
        PendingPwdResetEntity entity = new PendingPwdResetEntity(userEntity, encodedToken);
        pendingPwdResetRepository.save(entity);
        return encodedToken;
    }

    public void changePass(UserEntity userEntity, String token, String newPassword) throws UserNotFoundException, TokenNotMatch {
        List<PendingPwdResetEntity> dbResults = pendingPwdResetRepository.findByUserEntity(userEntity);
        if (dbResults.size() == 0)
            throw new UserNotFoundException();
        PendingPwdResetEntity pendingPwdReset = dbResults.get(0);
        if (!passwordEncoder.matches(token, pendingPwdReset.getSecurityToken()))
            throw new TokenNotMatch();

        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }

}
