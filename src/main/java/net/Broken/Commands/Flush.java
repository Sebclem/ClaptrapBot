package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command to flush X last message on channel.
 */

public class Flush implements Commande {
    Logger logger = LogManager.getLogger();

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (args.length < 1) {
            event.getTextChannel().sendMessage(EmbedMessageUtils.getFlushError("Missing argument!")).queue();

        } else {
            if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                try {
                    int limit = Integer.parseInt(args[0]) + 1;
                    MessageChannel chanel = event.getChannel();

                    chanel.getIterableHistory().takeAsync(limit).thenAccept(chanel::purgeMessages);


                } catch (NumberFormatException e) {
                    event.getTextChannel().sendMessage(EmbedMessageUtils.getFlushError("Argument unknown!")).queue();
                }
            } else {
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
