package net.Broken.Commands.Over18;

import net.Broken.Commande;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.FindContentOnWebPage;
import net.Broken.Tools.Redirection;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


/**
 * Created by seb65 on 11/11/2016.
 */
public class Madame implements Commande{
    Logger logger = LogManager.getLogger();
    MessageReceivedEvent event;
    public String HELP="T'es sérieux la?";
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        this.event = event;

        if(event.getTextChannel().isNSFW()) {
            Redirection redirect = new Redirection();
            boolean success=false;
            boolean error=false;
            int errorCp=0;
            while(!success && !error)
            {
                try {

                    String url = redirect.get("http://dites.bonjourmadame.fr/random");
                    logger.debug("URL: "+url);
                    if(scanPageForTipeee(url)){
                        logger.debug("Advertisement detected! Retry! ("+url+")");
                    }
                    else{
                        event.getTextChannel().sendMessage(url).queue();
                        success=true;
                    }

                } catch (IOException e) {
                    errorCp++;
                    logger.warn("Erreur de redirection. (Essais n°"+errorCp+")");
                    if(errorCp>5)
                    {
                        logger.error("5 Erreur de redirection.");
                        error=true;
                        event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Erreur de redirection (5 essais), Réessayez__**:warning: ").queue();

                    }

                }catch (StringIndexOutOfBoundsException e){
                    logger.catching(e);
                    event.getTextChannel().sendMessage(EmbedMessageUtils.getInternalError()).queue();
                }
            }

        }
        else
        {
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Channel règlementé! Go sur over18!__**:warning: ").queue();

            logger.warn("Erreur chanel.");
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public boolean isAdminCmd() {
        return false;
    }


    private boolean isAdvertisementUrl(String url){
        //Scan url
        if(url.toLowerCase().contains("club") && (url.toLowerCase().contains("rejoindre") || url.toLowerCase().contains("rejoignez"))){
            logger.debug("Advertisement detected with \"club\" and \"rejoidre\" or \"rejoignez\"");
            return true;
        }
        else if(url.contains("samedi") && url.contains("dimanche")){
            logger.debug("Advertisement detected with \"samedi\" and \"dimanche\"");
            return true;
        }
        else{
            return  false;
        }
    }


    private boolean scanPageForTipeee(String url) throws StringIndexOutOfBoundsException, IOException{
            String content = FindContentOnWebPage.getUrlSource(url);
            String imgClickLink = content.substring(content.indexOf("photo post"));
            imgClickLink = imgClickLink.substring(imgClickLink.indexOf("<a"));
            imgClickLink = imgClickLink.substring(imgClickLink.indexOf("\""));
            imgClickLink = imgClickLink.substring(0, imgClickLink.indexOf("\">"));
            imgClickLink = imgClickLink.substring(1);
            logger.debug("Image link: " + imgClickLink);
            if(imgClickLink.contains("tipeee")){
                logger.debug("Detect tipeee link! ");
                return true;
            }
            else
                return false;
    }
}
