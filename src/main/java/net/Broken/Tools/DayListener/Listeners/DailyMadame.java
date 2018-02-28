package net.Broken.Tools.DayListener.Listeners;

import net.Broken.MainBot;
import net.Broken.Tools.DayListener.NewDayListener;
import net.Broken.Tools.Redirection;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Daily Listener for DailyMadame
 */
public class DailyMadame implements NewDayListener{
    private Logger logger = LogManager.getLogger();
    @Override
    public void onNewDay() {
        Redirection redirect = new Redirection();
        boolean success=false;
        boolean error=false;
        int errorCp=0;
        TextChannel chanel = MainBot.jda.getTextChannelsByName("nsfw-over18", true).get(0);
        while(!success && !error)
        {
            try {
                chanel.sendMessage("Le Daily Madame mes petits cochons :kissing_heart:\n" + redirect.get("http://dites.bonjourmadame.fr/random")).queue();
                success=true;
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
}
