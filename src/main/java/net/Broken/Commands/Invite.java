package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.Tools.PrivateMessage;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;

public class Invite implements Commande{
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(event.getChannelType().isGuild()){
            event.getTextChannel().sendMessage("You can invite me whit this link:\nhttps://discordapp.com/oauth2/authorize?client_id=" + event.getJDA().getSelfUser().getId() + "&scope=bot&permissions=8").complete();
        }
        else {
            PrivateMessage.send(event.getAuthor(),"You can invite me whit this link:\nhttps://discordapp.com/oauth2/authorize?client_id=" + event.getJDA().getSelfUser().getId() + "&scope=bot&permissions=8",LogManager.getLogger());
        }
    }

    @Override
    public boolean isPrivateUsable() {
        return true;
    }

    @Override
    public boolean isAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }
}
