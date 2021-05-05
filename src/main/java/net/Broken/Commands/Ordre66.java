package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Tools.MessageTimeOut;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;

public class Ordre66 implements Commande {

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Message rest = event.getTextChannel().sendMessage("Très bien maître " + event.getAuthor().getAsMention()+". J'arrive ! ").complete();
        Message reste = event.getTextChannel().sendMessage("https://media2.giphy.com/media/UfzTayIyH7g5hk2BA2/giphy.gif\n").complete();
    }

    @Override
    public boolean isPrivateUsable() {
        return true;
    }

    @Override
    public boolean isAdminCmd() {
        return true;
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
        return false;
    }
}
