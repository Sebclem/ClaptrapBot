package net.Broken.Commands.Over18;

import net.Broken.Commande;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.FindContentOnWebPage;
import net.Broken.Tools.Redirection;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Suicide implements Commande{

    private Logger logger = LogManager.getLogger();


    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Redirection redirection = new Redirection();

        String base = "http://suicidegirls.tumblr.com";
        String redirectUrl = null;
        try {

            Boolean success = false;
            int tryCount = 0;
            while(!success && tryCount < 10 ){
                redirectUrl = redirection.get(base + "/random");

                String img;
                try{
                    img = FindContentOnWebPage.doYourJob(redirectUrl, "post photo_nav_caption", "img");
                    event.getTextChannel().sendMessage(img).queue();
                    success = true;

                }catch (StringIndexOutOfBoundsException | IOException e){
                    logger.debug("Photo_nav not found try photoset");

                    try {
                        String mid = FindContentOnWebPage.doYourJob(redirectUrl, "html_photoset", "iframe");
                        img = FindContentOnWebPage.doYourJob(base + mid, "photoset_row", "img");
                        event.getTextChannel().sendMessage(img).queue();
                        success = true;
                    } catch (StringIndexOutOfBoundsException | IOException e1) {
                        logger.debug("Nothing found, assume it's a comment.");
                    }


                }
                tryCount ++;
            }
        } catch (IOException e) {
            logger.catching(e);
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
        return true;
    }
}
