package net.Broken.Commands;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.Broken.audio.Youtube.SearchResult;
import net.Broken.audio.Youtube.YoutubeTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

public class YtSearch implements Commande {

    private Logger logger = LogManager.getLogger();

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        YoutubeTools youtubeT = YoutubeTools.getInstance();

        if(args.length < 1){
            logger.info("YtSearch: Missing args, user: " + event.getAuthor().getName());
            Message message = event.getChannel().sendMessage(EmbedMessageUtils.buildStandar(EmbedMessageUtils.getError("Missing search query!"))).complete();
            new MessageTimeOut(MainBot.messageTimeOut, message, event.getMessage()).start();
        }else {
            try {
                ArrayList<SearchResult> result = youtubeT.search(args[0], 5, false);
                for(SearchResult item : result){
                    event.getChannel().sendMessage(EmbedMessageUtils.searchResult(item)).queue();
                }

            } catch (GoogleJsonResponseException e) {
                logger.error("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
                event.getChannel().sendMessage(EmbedMessageUtils.getInternalError()).queue();
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

    @Override
    public boolean isNSFW() {
        return false;
    }
}
