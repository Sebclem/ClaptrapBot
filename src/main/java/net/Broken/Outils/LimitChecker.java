package net.Broken.Outils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by seb65 on 20/03/2017.
 */
public class LimitChecker {
    static Logger logger = LogManager.getLogger();


    public static int doYourJob(String baseURL, int minNumber) throws IOException {
        int number = minNumber;
        URL u = null;
        int result = -1;

        while(result != 404 )
        {
            u = new URL( baseURL+number+"-2/");
            HttpURLConnection huc = (HttpURLConnection)u.openConnection ();
            huc.setRequestMethod ("GET");
            huc.connect ();
            result = huc.getResponseCode();
            logger.debug("URL: "+u.toString()+" Result: "+result);
            if(result!=404)
                number += 500;
        }
        number-=400;
        result = -1;
        logger.debug("First pass: "+number);
        while(result != 404 )
        {
            u = new URL( baseURL+number+"-2/");
            HttpURLConnection huc = (HttpURLConnection)u.openConnection ();
            huc.setRequestMethod ("GET");
            huc.connect ();
            result = huc.getResponseCode();
            logger.debug("URL: "+u.toString()+" Result: "+result);
            if(result!=404)
                number += 100;
        }
        number-=90;
        result = -1;
        logger.debug("Second pass: "+number);
        while(result != 404 )
        {
            u = new URL( baseURL+number+"-2/");
            HttpURLConnection huc = (HttpURLConnection)u.openConnection ();
            huc.setRequestMethod ("GET");
            huc.connect ();
            result = huc.getResponseCode();
            logger.debug("URL: "+u.toString()+" Result: "+result);
            if(result!=404)
                number += 10;
        }
        number-=9;
        result = -1;
        logger.debug("Third pass: "+number);
        while(result != 404 )
        {
            u = new URL( baseURL+number+"-2/");
            HttpURLConnection huc = (HttpURLConnection)u.openConnection ();
            huc.setRequestMethod ("GET");
            huc.connect ();
            result = huc.getResponseCode();
            logger.debug("URL: "+u.toString()+" Result: "+result);
            if(result!=404)
                number += 1;
        }
        number-=1;
        logger.debug("Final pass: "+number);
        return number;


    }
}