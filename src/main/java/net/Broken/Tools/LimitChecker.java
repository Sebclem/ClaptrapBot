package net.Broken.Tools;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Find max webPage for web site like baseURL.com/number-2/
 */
public class LimitChecker {
    static final Logger logger = LogManager.getLogger();


    /**
     * Check max page url for web site like baseURL.com/number-2/
     *
     * @param baseURL   Base url without numbers
     * @param minNumber Start number
     * @return max Number
     * @throws IOException
     */
    public static int doYourJob(String baseURL, int minNumber, String suffix) throws IOException {
        int number = minNumber;
        boolean redirected = false;
        Redirection redirection = new Redirection();

        while (!redirected) {
            String origin = baseURL + number + suffix;
            String newUrl = redirection.get(origin);
            logger.trace("Origin URL: " + origin + " Result: " + newUrl);
            if (newUrl.equals(origin))
                number += 500;
            else
                redirected = true;
        }
        number -= 400;
        redirected = false;
        logger.debug("First pass: " + number);
        while (!redirected) {
            String origin = baseURL + number + suffix;
            String newUrl = redirection.get(origin);
            logger.trace("Origin URL: " + origin + " Result: " + newUrl);
            if (newUrl.equals(origin))
                number += 100;
            else
                redirected = true;
        }
        number -= 90;
        redirected = false;
        logger.debug("Second pass: " + number);
        while (!redirected) {
            String origin = baseURL + number + suffix;
            String newUrl = redirection.get(origin);
            logger.trace("Origin URL: " + origin + " Result: " + newUrl);
            if (newUrl.equals(origin))
                number += 10;
            else
                redirected = true;
        }
        number -= 9;
        redirected = false;
        logger.debug("Third pass: " + number);
        while (!redirected) {
            String origin = baseURL + number + suffix;
            String newUrl = redirection.get(origin);
            logger.trace("Origin URL: " + origin + " Result: " + newUrl);
            if (newUrl.equals(origin))
                number += 1;
            else
                redirected = true;
        }
        number -= 1;
        logger.debug("Final pass: " + number);
        return number;


    }
}
