package net.Broken.Outils;

import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.logging.log4j.Logger;

/**
 * Created by seb65 on 04/09/2017.
 */
public class PrivateMessage {
    public static void send(User user, String message, Logger logger){
        if(!user.hasPrivateChannel()){
            logger.debug("Create Private Chanel");

            user.openPrivateChannel().complete();

        }
        user.getJDA().getPrivateChannels().get(0).sendMessage(message).queue();

    }
}
