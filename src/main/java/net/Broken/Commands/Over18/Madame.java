package net.Broken.Commands.Over18;

import net.Broken.Commande;
import net.Broken.Tools.Command.NumberedCommande;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.FindContentOnWebPage;
import net.Broken.Tools.Redirection;
import net.Broken.Tools.TrueRandom;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


/**
 * Madame command that return random picture from dites.bonjourmadame.fr
 */
public class Madame extends NumberedCommande {
    Logger logger = LogManager.getLogger();
    MessageReceivedEvent event;
    public String HELP = "T'es sérieux la?";

    public Madame() {
        super(LogManager.getLogger(), "http://www.bonjourmadame.fr/page/", "/");
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
     *
     * @param url
     * @return true is Tepeee link is detected
     * @throws StringIndexOutOfBoundsException
     * @throws IOException
     */
    public static boolean scanPageForTipeee(String url, Logger logger) throws StringIndexOutOfBoundsException, IOException {
        String content = FindContentOnWebPage.getSourceUrl(url);
        String imgClickLink = content.substring(content.indexOf("class=\"post-content"));
        imgClickLink = imgClickLink.substring(imgClickLink.indexOf("<a"));
        imgClickLink = imgClickLink.substring(imgClickLink.indexOf("\""));
        imgClickLink = imgClickLink.substring(0, imgClickLink.indexOf("\">"));
        imgClickLink = imgClickLink.substring(1);
        logger.trace("Image link: " + imgClickLink);
        if (imgClickLink.contains("tipeee")) {
            logger.trace("Detect tipeee link! ");
            return true;
        } else
            return false;
    }


    @Override
    public String poll() throws IOException {
        boolean success = false;
        String imgUrl = null;
        while (!success ) {

            checkRandom();
            int randomResult = randomQueue.poll();
            String url = baseURL + randomResult + urlSuffix;
            logger.debug("URL: " + url);
            if (scanPageForTipeee(url, logger)) {
                logger.debug("Advertisement detected! Retry! (" + url + ")");
            } else {
                imgUrl = FindContentOnWebPage.doYourJob(url, "post-content", "img");

                success = true;
            }

        }
        return imgUrl;
    }
}
