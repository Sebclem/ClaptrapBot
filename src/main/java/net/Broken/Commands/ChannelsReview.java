package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.format.DateTimeFormatter;

public class ChannelsReview implements Commande {
    Logger logger = LogManager.getLogger();
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        StringBuilder messageToSend= new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        for( TextChannel textChannel: event.getGuild().getTextChannels()){
            if(textChannel.hasLatestMessage()){
                String lastMessageId = textChannel.getLatestMessageId();
                logger.debug("Last message in chanel " + textChannel.toString() + " is " + lastMessageId );
                try {
                    Message lastMessage = textChannel.retrieveMessageById(lastMessageId).complete();
                    String date = lastMessage.getTimeCreated().toLocalDate().format(formatter);
                    messageToSend.append("\nChannel : ").append(textChannel.getName()).append(" Date :").append(date);
                }catch (RuntimeException e){
                    logger.warn("Can't find message with id: " + lastMessageId);
                    messageToSend.append("\nChannel : ERROR");
                }


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
