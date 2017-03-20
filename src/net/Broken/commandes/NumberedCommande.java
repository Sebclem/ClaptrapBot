package net.Broken.commandes;
import net.Broken.Commande;
import net.Broken.Outils.LimitChecker;
import net.Broken.Outils.Redirection;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by seb65 on 07/11/2016.
 */
public abstract class NumberedCommande implements Commande{
    Logger logger = LogManager.getLogger();
    public String HELP="T'es sérieux la?";
    int minNumber = 1;
    int maxNumber = -1;
    String baseURL;


    public NumberedCommande(Logger logger, String baseURL) {
        this.logger = logger;
        this.baseURL = baseURL;
        try {
            logger.info("Checking max...");
            maxNumber = LimitChecker.doYourJob(baseURL, minNumber);
            logger.info("New limit is "+maxNumber);
        } catch (IOException e) {
            logger.catching(e);
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(event.getTextChannel().getName().equals("over18")) {
            Redirection redirect= new Redirection();
            int randomResult = (int) (minNumber + (Math.random() * (maxNumber - minNumber)));
            event.getTextChannel().sendMessage(baseURL+randomResult+"-2/").queue();
        }
        else
        {
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Channel règlementé! Go sur over18!__**:warning: ").queue();

            logger.warn("Erreur chanel.");
        }
    }

    @Override
    public String help(String[] args) {
        return HELP;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }


}
