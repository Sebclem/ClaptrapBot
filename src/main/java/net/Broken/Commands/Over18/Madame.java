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
 * Madame command that return random picture from dites.bonjourmadame.fr
 */
public class Madame implements Commande{
    Logger logger = LogManager.getLogger();
    MessageReceivedEvent event;
    public String HELP="T'es sérieux la?";

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        this.event = event;
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
        return true;
    }


    /**
     * Detect if picture link go to Tepeee
     * @param url
     * @return true is Tepeee link is detected
     * @throws StringIndexOutOfBoundsException
     * @throws IOException
     */
    private boolean scanPageForTipeee(String url) throws StringIndexOutOfBoundsException, IOException{
            String content = FindContentOnWebPage.getSourceUrl(url);
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
