package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.PrivateMessage;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Send standard internal error.
 */

public class Error implements Commande{

    private Logger logger = LogManager.getLogger();

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(event.isFromType(ChannelType.PRIVATE))
            PrivateMessage.send(event.getAuthor(), EmbedMessageUtils.getInternalError(), logger);
        else
            event.getTextChannel().sendMessage(EmbedMessageUtils.getInternalError()).queue();
    }

    @Override
    public boolean isPrivateUsable() {
        return true;
    }

    @Override
    public boolean isAdminCmd() {
        return true;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }
}
