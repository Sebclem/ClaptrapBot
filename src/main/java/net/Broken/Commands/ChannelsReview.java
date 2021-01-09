package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ChannelsReview implements Commande {
    Logger logger = LogManager.getLogger();

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        event.getTextChannel().sendMessage("Number of channels found in total : " + event.getGuild().getTextChannels().size()).queue();
        if(args.length>=1){
            try {
                SendBackBefore(format.parse(args[0]), event, format );
            } catch (ParseException e) {
                logger.warn("Can't parse date : " + e.getMessage());
            }
        }
        else{
            SendBack(event);
        }
    }


    private void SendBackBefore(Date beforeDate,MessageReceivedEvent event,DateFormat format ){
        HashMap<String, String> result = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.ENGLISH);
        int charCtl = 0;
        for (TextChannel textChannel : event.getGuild().getTextChannels()) {
            if (textChannel.hasLatestMessage()) {
                String lastMessageId = textChannel.getLatestMessageId();
                try {
                    Message lastMessage = textChannel.retrieveMessageById(lastMessageId).complete();
                    if(beforeDate.compareTo(format.parse(lastMessage.getTimeCreated().format(formatter)))>0){
                        logger.debug("Last message in channel " + textChannel.toString() + " is " + lastMessageId);
                        String date = lastMessage.getTimeCreated().format(formatter);
                        charCtl += textChannel.getName().length() + date.length();
                        result.put(textChannel.getName(), date);
                    }
                } catch (RuntimeException | ParseException e) {
                    logger.warn("Can't find message with id: " + lastMessageId);
                    result.put(textChannel.getName(), "ERROR");
                    charCtl += textChannel.getName().length() + 5;
                }

            } else {
                result.put(textChannel.getName(), "No message or access denied.");
                charCtl += textChannel.getName().length() + 30;
            }
            if (charCtl > 3000) {
                event.getTextChannel().sendMessage(EmbedMessageUtils.getLastMessageFromTextChannel(result)).complete();
                result = new HashMap<>();
                charCtl = 0;
            }
        }
        if (charCtl != 0)
            event.getTextChannel().sendMessage(EmbedMessageUtils.getLastMessageFromTextChannel(result)).queue();
    }

    private void SendBack(MessageReceivedEvent event){
        HashMap<String, String> result = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale.ENGLISH);
        int charCtl = 0;
        for (TextChannel textChannel : event.getGuild().getTextChannels()) {
            if (textChannel.hasLatestMessage()) {
                String lastMessageId = textChannel.getLatestMessageId();
                logger.debug("Last message in channel " + textChannel.toString() + " is " + lastMessageId);

                try {
                    Message lastMessage = textChannel.retrieveMessageById(lastMessageId).complete();
                    String date = lastMessage.getTimeCreated().format(formatter);
                    charCtl += textChannel.getName().length() + date.length();
                    result.put(textChannel.getName(), date);
                } catch (RuntimeException e) {
                    logger.warn("Can't find message with id: " + lastMessageId);
                    result.put(textChannel.getName(), "ERROR");
                    charCtl += textChannel.getName().length() + 5;
                }

            } else {
                result.put(textChannel.getName(), "No message or access denied.");
                charCtl += textChannel.getName().length() + 30;
            }
            if (charCtl > 3000) {
                event.getTextChannel().sendMessage(EmbedMessageUtils.getLastMessageFromTextChannel(result)).queue();
                event.getTextChannel().sendTyping().queue();
                result = new HashMap<>();
                charCtl = 0;
            }
        }
        if (charCtl != 0)
            event.getTextChannel().sendMessage(EmbedMessageUtils.getLastMessageFromTextChannel(result)).queue();
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
