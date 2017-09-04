package net.Broken.Commandes;

import net.Broken.Commande;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;

import java.sql.Timestamp;
import java.time.*;


/**
 * Created by seb65 on 19/10/2016.
 */
public class PingCommande implements Commande {


    private String HELP = "`//ping` \n :arrow_right:\t*Le bot vous répondra Pong!*";
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        long receivedTime = Timestamp.valueOf(LocalDateTime.ofInstant(event.getMessage().getCreationTime().toInstant(), ZoneId.systemDefault())).getTime();
        if(event.isFromType(ChannelType.PRIVATE))
            event.getPrivateChannel().sendMessage(":arrow_right: Pong! `"+((Timestamp.from(Instant.now()).getTime()-receivedTime))+"ms`").queue();
        else
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:arrow_right: Pong! `"+((Timestamp.from(Instant.now()).getTime()-receivedTime))+"ms`").queue();
        LogManager.getLogger().debug("pong");
    }

    @Override
    public String help(String[] args) {

        return HELP;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        return;

    }
}
