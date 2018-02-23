package net.Broken.Commands.Over18;

import net.Broken.Commande;
import net.Broken.Tools.Redirection;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


/**
 * Created by seb65 on 11/11/2016.
 */
public class Madame implements Commande{
    Logger logger = LogManager.getLogger();
    public String HELP="T'es sérieux la?";
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        if(event.getTextChannel().isNSFW()) {
            Redirection redirect = new Redirection();
            boolean success=false;
            boolean error=false;
            int errorCp=0;
            while(!success && !error)
            {
                try {
                    event.getTextChannel().sendMessage(redirect.get("http://dites.bonjourmadame.fr/random")).queue();
                    success=true;
                } catch (IOException e) {
                    errorCp++;
                    logger.warn("Erreur de redirection. (Essais n°"+errorCp+")");
                    if(errorCp>5)
                    {
                        logger.error("5 Erreur de redirection.");
                        error=true;
                        event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Erreur de redirection (5 essais), Réessayez__**:warning: ").queue();

                    }

                }
            }

        }
        else
        {
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Channel règlementé! Go sur over18!__**:warning: ").queue();

            logger.warn("Erreur chanel.");
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public boolean isAdminCmd() {
        return false;
    }
}
