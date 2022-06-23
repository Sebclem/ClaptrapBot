package net.Broken.Tools.UserManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserUtils {

    private static final UserUtils INSTANCE = new UserUtils();
    private final Logger logger = LogManager.getLogger();

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

}
