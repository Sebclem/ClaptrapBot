package net.Broken.commandes;

import net.Broken.Commande;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;


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

        event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+" Pong!").queue();
        LogManager.getLogger().info("pong");
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
