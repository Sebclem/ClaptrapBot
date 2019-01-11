package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Command to flush X last message on channel.
 */

public class Flush implements Commande{
    Logger logger = LogManager.getLogger();

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(args.length<1){
            event.getTextChannel().sendMessage(EmbedMessageUtils.getFlushError("Missing argument!")).queue();

        }
        else
        {
            if(event.getMember().hasPermission(Permission.ADMINISTRATOR)){
                try {
                    int limit = Integer.parseInt(args[0]) + 1;
                    MessageChannel chanel = event.getChannel();

                    chanel.getIterableHistory().takeAsync(limit).thenAccept(chanel::purgeMessages);
                    

                }catch (NumberFormatException e){
                    event.getTextChannel().sendMessage(EmbedMessageUtils.getFlushError("Argument unknown!")).queue();
                }
            }
            else
            {
                event.getTextChannel().sendMessage(EmbedMessageUtils.getFlushError("You are not a supreme being, you cannot do that !")).queue();
            }



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
        return false;
    }
}
