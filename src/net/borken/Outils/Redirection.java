package net.borken.Outils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by seb65 on 07/11/2016.
 */
public class Redirection {
    Entete entete=new Entete();

    public Redirection(){


    }


    public String get(String urlString) throws IOException {
        URLConnection con = new URL(urlString).openConnection();
        //System.out.println( "orignal url: " + con.getURL() );
        con.connect();
        //System.out.println( "connected url: " + con.getURL() );
        InputStream is = null;
        is = con.getInputStream();
        String urlReturn=con.getURL().toString();
        //System.out.println( "redirected url: " + con.getURL() );
        is.close();
        return urlReturn;
    }



}
