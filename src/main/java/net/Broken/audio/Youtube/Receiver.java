package net.Broken.audio.Youtube;

import com.google.api.client.extensions.java6.auth.oauth2.AbstractPromptReceiver;
import net.dv8tion.jda.core.entities.Guild;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

public class Receiver extends AbstractPromptReceiver {

    private static Receiver INSTANCE;
    private Guild guild;
    private String code;

    private Receiver(Guild guild){
        this.guild = guild;
    }

    public static Receiver getInstance(Guild guild){
        if(INSTANCE == null)
            INSTANCE = new Receiver(guild);
        return INSTANCE;
    }


    @Override
    public String getRedirectUri() throws IOException {
        return System.getenv("SITE_URL") + "/youtube/callback";
    }

    @Override
    public String waitForCode() {
        if(System.getenv("SITE_URL").isEmpty()){
            LogManager.getLogger().fatal("Please set \"SITE_URL\" environment variable and restart the bot!");
        }
        while(code == null){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return code;
    }

    @Override
    public void stop() {

    }

    public void setCode(String code) {
        this.code = code;
    }
}
