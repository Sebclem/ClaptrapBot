package net.Broken.Commands.Over18;

import net.Broken.Commande;
import net.Broken.Tools.Redirection;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO Remove this
 */
public class SM implements Commande {
    Logger logger = LogManager.getLogger();
    public String HELP="T'es sérieux la?";

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Redirection redirect= new Redirection();
        event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: SM n'est plus disponible pour le moment. Pour plus d'info: https://lc.cx/cbSw :warning: ").queue();
//            try {
//                event.getTextChannel().sendMessage(redirect.get("https://bonjourfetish.tumblr.com/random")).queue();
//            } catch (IOException e) {
//                logger.warn("Erreur de redirection.");
//                event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Erreur de redirection (5 essais), Réessayez__**:warning: ").queue();
//            }

    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public boolean isAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return true;
    }
}
