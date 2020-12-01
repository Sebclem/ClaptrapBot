package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.format.DateTimeFormatter;

public class ChannelsReview implements Commande {
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        StringBuilder messageToSend= new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        for( TextChannel textChannel: event.getGuild().getTextChannels()){
            if(textChannel.hasLatestMessage()){
                messageToSend.append("\nChannel : ").append(textChannel.getName()).append(" Date :").append((textChannel.retrieveMessageById(textChannel.getLatestMessageId()).complete().getTimeCreated()).toLocalDate().format(formatter));
            }
            else{
                messageToSend.append("\nChannel : ").append(textChannel.getName()).append(" was never used.");
            }
        }
        event.getTextChannel().sendMessage(EmbedMessageUtils.getLastMessageFromTextChannel(messageToSend.toString())).queue();
    }

    @Override
    public boolean isPrivateUsable() {
        return false;
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
