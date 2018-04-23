package net.Broken.Commands.Over18;

import net.Broken.Commande;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.Redirection;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

public class Suicide implements Commande{

    private String redirectUrl = "https://suicidegirlsandhopefuls.tumblr.com/random";

    
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Redirection redirection = new Redirection();
        try {
            event.getTextChannel().sendMessage(redirection.get(redirectUrl)).queue();
        } catch (IOException e) {
            LogManager.getLogger().catching(e);
            event.getTextChannel().sendMessage(EmbedMessageUtils.getInternalError()).queue();
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

    @Override
    public boolean isNSFW() {
        return true;
    }
}
