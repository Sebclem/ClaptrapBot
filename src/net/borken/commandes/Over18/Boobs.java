package net.borken.commandes.Over18;

import net.borken.Commande;
import net.borken.Outils.Redirection;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by seb65 on 07/11/2016.
 */
public class Boobs implements Commande {
    Logger logger = LogManager.getLogger();
    public String HELP="T'es sérieux la?";

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(event.getTextChannel().getName().equals("over18")) {
            Redirection redirect = new Redirection();
            try {
                event.getTextChannel().sendMessage(redirect.get("http://lesaintdesseins.fr/random")).queue();
            } catch (IOException e) {
                logger.warn("Erreur de redirection.");
            }
        }
        else
        {
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Channel règlementé! Go sur over18!__**:warning:  ").queue();
            logger.warn("Erreur chanel.");
        }
    }

    @Override
    public String help(String[] args) {
        return HELP;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }
}
