package net.Broken.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by sebastien on 10/05/17.
 */
public  class FindContentOnWebPage {
    public static String doYourJob(String url, String divClass, String htmlType) throws IOException {
        String source = getUrlSource(url);
        int divIndex = source.indexOf(divClass);
        String sub = source.substring(divIndex);
//        System.out.println(sub);
        sub = sub.replace(divClass,"");
        sub = sub.substring(sub.indexOf(htmlType));
        sub = sub.substring(sub.indexOf("src"));
        sub = sub.replace("src=\"","");
        String[] split = sub.split("\"");
//        System.out.println(split[0]);
        return split[0];
    }

    public static String getUrlSource(String url) throws IOException {
        URL urlC = new URL(url);
        URLConnection yc = urlC.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            a.append(inputLine);
        in.close();

        return a.toString();
    }
}