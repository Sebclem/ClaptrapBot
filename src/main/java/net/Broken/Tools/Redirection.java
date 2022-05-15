package net.Broken.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Redirection URL Util
 */
public class Redirection {

    public Redirection() {
    }

    /**
     * Return Redirected URL
     *
     * @param urlString Source URL
     * @return Redirected URL
     * @throws IOException
     */
    public String get(String urlString) throws IOException {
        System.setProperty("http.agent", "Googlebot");
        HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
        con.setRequestProperty("User-Agent", "Googlebot/2.1 (+http://www.googlebot.com/bot.html)");
        //System.out.println( "orignal url: " + con.getURL() );
        con.connect();
        //System.out.println( "connected url: " + con.getURL() );
        InputStream is = null;
        if (con.getResponseCode() != 200)
            return "";
        is = con.getInputStream();
        String urlReturn = con.getURL().toString();
        //System.out.println( "redirected url: " + con.getURL() );
        is.close();

        return urlReturn;
    }


}
