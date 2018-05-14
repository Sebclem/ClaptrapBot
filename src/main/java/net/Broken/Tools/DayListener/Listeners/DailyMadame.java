package net.Broken.Tools.DayListener.Listeners;

import net.Broken.Commands.Over18.Madame;
import net.Broken.MainBot;
import net.Broken.Tools.DayListener.NewDayListener;
import net.Broken.Tools.Redirection;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Daily Listener for DailyMadame
 */
public class DailyMadame implements NewDayListener{
    private Logger logger = LogManager.getLogger();
    @Override
    public void onNewDay() {
        Redirection redirect = new Redirection();

        List<Guild> guilds = MainBot.jda.getGuilds();

        for(Guild guild : guilds){
            TextChannel chanel = null;
            boolean success=false;
            boolean error=false;
            int errorCp=0;
            logger.debug(guild.getName());
            for(TextChannel iterator : guild.getTextChannels())
            {
                if(iterator.isNSFW()){
                    chanel = iterator;
                    logger.debug("break: " + chanel.getName());
                    break;
                }
            }
            if(chanel != null){
                while(!success && !error)
                {
                    try {

                        String url = redirect.get("http://dites.bonjourmadame.fr/random");
                        logger.debug("URL: "+url);
                        if(Madame.scanPageForTipeee(url, logger)){
                            logger.debug("Advertisement detected! Retry! ("+url+")");
                        }
                        else{
                            chanel.sendMessage("Le Daily Madame mes petits cochons :kissing_heart:\n" + url).queue();
                            success=true;
                        }
                    } catch (IOException e) {
                        errorCp++;
                        logger.warn("Erreur de redirection. (Essais n°"+errorCp+")");
                        if(errorCp>5)
                        {
                            logger.error("5 Erreur de redirection.");
                            error=true;

                        }

                    }
                }
            }
            else {
                logger.info("No NSFW chanel found for " + guild.getName() + ", ignoring it!");
            }

        }


    }
}
