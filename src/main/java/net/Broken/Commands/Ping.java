package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Tools.MessageTimeOut;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;


/**
 * Command that return the Bot's ping
 */
public class Ping implements Commande {

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        long ping = event.getJDA().getPing();
//        long receivedTime = Timestamp.valueOf(LocalDateTime.ofInstant(event.getMessage().getCreationTime().toInstant(), ZoneId.systemDefault())).getTime();
        if(event.isFromType(ChannelType.PRIVATE))
            event.getPrivateChannel().sendMessage(":arrow_right: Pong! `" + ping+ "ms`").queue();
        else {
            Message rest = event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:arrow_right: Pong! `" + ping + "ms`").complete();
            new MessageTimeOut(MainBot.messageTimeOut, event.getMessage(), rest).start();
        }
        LogManager.getLogger().debug("pong");
    }

    @Override
    public boolean isPrivateUsable() {
        return true;
    }

    @Override
    public boolean isAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }
}
