package net.Broken.Commands.Over18;

import net.Broken.Commande;
import net.Broken.Tools.FindContentOnWebPage;
import net.Broken.Tools.Redirection;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * TODO Remove this
 */
public class SM implements Commande {
    Logger logger = LogManager.getLogger();

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Redirection redirect = new Redirection();
        try {
            String redirectUrl = redirect.get("https://bonjourfetish.tumblr.com/random");
            logger.debug(redirectUrl);
            String img = FindContentOnWebPage.doYourJob(redirectUrl, "article-picture center", "img");
            event.getTextChannel().sendMessage(img).queue();
        } catch (IOException e) {
            logger.warn("Redirection fail.");
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Redirection fail (5 attempt), Try again__**:warning: ").queue();
        }

    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public boolean isAdminCmd() {
        return false;
    }

    /**
     * Determines if the command is usable only by bot level admin user
     *
     * @return boolean
     */
    @Override
    public boolean isBotAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return true;
    }
}
