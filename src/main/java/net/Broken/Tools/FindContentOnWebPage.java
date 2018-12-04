package net.Broken.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public  class FindContentOnWebPage {
    /**
     * Find picture URL on webPage
     * @param url Web Page URL
     * @param divClass Div class where the picture is
     * @param htmlType HTML tag of image (img)
     * @return Picture URL
     * @throws IOException
     */
    public static String doYourJob(String url, String divClass, String htmlType) throws IOException {
//        System.out.println(url);
        String source = getSourceUrl(url);
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

    /**
     * Get source code of web page
     * @param url Web page URL
     * @return Web page source as String
     * @throws IOException
     */
    public static String getSourceUrl(String url) throws IOException {
        URL urlC = new URL(url);
        URLConnection yc = urlC.openConnection();
        yc.setRequestProperty("User-Agent","Googlebot/2.1 (+http://www.googlebot.com/bot.html)");
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