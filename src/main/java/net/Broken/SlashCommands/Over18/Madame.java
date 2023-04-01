package net.Broken.SlashCommands.Over18;

import net.Broken.Tools.Command.NoDev;
import net.Broken.Tools.Command.NumberedSlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.Broken.Tools.FindContentOnWebPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@NoDev
public class Madame extends NumberedSlashCommand {
    final Logger logger = LogManager.getLogger();

    public Madame() {
        super(LogManager.getLogger(), "https://www.bonjourmadame.fr/page/", "/");
    }
    /**
     * Detect if picture link go to Tepeee
     *
     * @param url
     * @return true is Tepeee link is detected
     * @throws StringIndexOutOfBoundsException
     * @throws IOException
     */
    private boolean scanPageForTipeee(String url, Logger logger) throws StringIndexOutOfBoundsException, IOException, InterruptedException {
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

    private String removeParams(String url) {
        int par = url.indexOf('?');
        if (par != -1) {
            url = url.substring(0, par);
        }
        return url;
    }


    @Override
    public String poll() throws IOException, InterruptedException {
        boolean success = false;
        String imgUrl = null;
        while (!success) {

            checkRandom();
            int randomResult = randomQueue.poll();
            String url = baseURL + randomResult + urlSuffix;

            logger.debug("URL: {}",url);


            if (scanPageForTipeee(url, logger)) {
                logger.debug("Advertisement detected! Retry! ({})", url);
            } else {
                imgUrl = FindContentOnWebPage.doYourJob(url, "post-content", "img");

                success = true;
            }

        }
        imgUrl = removeParams(imgUrl);
        return imgUrl;
    }

    @Override
    public String getDescription() {
        return "Return random image from bonjourmadame.fr";
    }

    @Override
    public boolean isBotAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return true;
    }

    @Override
    public boolean isPrivateUsable() {
        return true;
    }

    @Override
    public DefaultMemberPermissions getDefaultPermissions() {
        return DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND);
    }
}
