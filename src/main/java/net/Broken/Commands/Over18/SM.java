package net.Broken.Commands.Over18;

import net.Broken.Commande;
import net.Broken.Tools.Redirection;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by seb65 on 10/11/2016.
 */
public class SM implements Commande {
    Logger logger = LogManager.getLogger();
    public String HELP="T'es sérieux la?";
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Redirection redirect= new Redirection();
        if(event.getTextChannel().getName().equals("nsfw-over18"))
        {
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: SM n'est plus disponible pour le moment. Pour plus d'info: https://lc.cx/cbSw :warning: ").queue();
//            try {
//                event.getTextChannel().sendMessage(redirect.get("https://bonjourfetish.tumblr.com/random")).queue();
//            } catch (IOException e) {
//                logger.warn("Erreur de redirection.");
//                event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Erreur de redirection (5 essais), Réessayez__**:warning: ").queue();
//            }
        }
        else
        {
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Channel règlementé! Go sur over18!__**:warning: ").queue();

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

    @Override
    public boolean isPrivateUsable() {
        return false;
    }
}
