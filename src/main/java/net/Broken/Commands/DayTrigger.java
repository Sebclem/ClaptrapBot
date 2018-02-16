package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Tools.DayListener.DayListener;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.Broken.Tools.PrivateMessage;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DayTrigger implements Commande{
    Logger logger = LogManager.getLogger();

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            DayListener.getInstance().trigger();
            event.getMessage().delete().queue();
        }
        else{
            logger.warn("Not admin! DENY!");
            if(!event.isFromType(ChannelType.PRIVATE)){
                Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getError("Vous n'avez pas l'autorisation de faire ça!")).complete();
                List<Message> messages = new ArrayList<Message>(){{
                    add(message);
                    add(event.getMessage());
                }};
                new MessageTimeOut(messages, MainBot.messageTimeOut).start();
            }
            else{
                PrivateMessage.send(event.getAuthor(),EmbedMessageUtils.getError("Vous n'avez pas l'autorisation de faire ça!"),logger);
            }



        }

    }

    @Override
    public String help(String[] args) {
        return null;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public boolean isPrivateUsable() {
        return true;
    }
}
