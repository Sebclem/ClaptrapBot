package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.Broken.audio.Youtube.SearchResult;
import net.Broken.audio.Youtube.YoutubeSearchRework;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class YtSearch implements Commande {

    private Logger logger = LogManager.getLogger();

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        YoutubeSearchRework youtubeSearch = YoutubeSearchRework.getInstance();
        if (args.length < 1) {
            logger.info("YtSearch: Missing args, user: " + event.getAuthor().getName());
            Message message = event.getChannel().sendMessage(EmbedMessageUtils.buildStandar(EmbedMessageUtils.getError("Missing search query!"))).complete();
            new MessageTimeOut(MainBot.messageTimeOut, message, event.getMessage()).start();
        } else {
            try {

                StringBuilder builder = new StringBuilder();
                for (String arg : args) {
                    builder.append(arg);
                }
                List<SearchResult> result = youtubeSearch.searchVideo(builder.toString(), 5, false);
                for (SearchResult item : result) {
                    event.getChannel().sendMessage(EmbedMessageUtils.searchResult(item)).queue();
                }

            } catch (IOException t) {
                logger.catching(t);
                event.getChannel().sendMessage(EmbedMessageUtils.getInternalError()).queue();
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
