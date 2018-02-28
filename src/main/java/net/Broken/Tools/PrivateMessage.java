package net.Broken.Tools;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.Logger;

/**
 * Private message utils
 */
public class PrivateMessage {

    /**
     * Auto open private channel and send message
     * @param user User to send message
     * @param message Message to send
     * @param logger Logger
     */
    public static void send(User user, String message, Logger logger){

        user.openPrivateChannel().complete().sendMessage(message).queue();

    }

    /**
     * Auto open private channel and send message
     * @param user User to send message
     * @param message Message to send
     * @param logger Logger
     * @return Sended Message
     */
    public static Message send(User user, MessageEmbed message, Logger logger){
        return user.openPrivateChannel().complete().sendMessage(message).complete();

    }
}
