package net.Broken.webView;

import net.Broken.MainBot;

public class CheckPage {
    public static String getPageIfReady(String page){
        if(MainBot.ready)
            return page;
        else
            return "loading";
    }
}
