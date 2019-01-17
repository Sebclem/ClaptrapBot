package net.Broken.Tools.Command;
import net.Broken.Commande;
import net.Broken.Tools.FindContentOnWebPage;
import net.Broken.Tools.LimitChecker;
import net.Broken.Tools.TrueRandom;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Abstact class used for all command that need to find the max number of page on a web site.
 */
@Ignore
public abstract class NumberedCommande implements Commande{
    private Logger logger = LogManager.getLogger();
    protected int minNumber = 1;
    protected int maxNumber = -1;
    protected String baseURL;
    protected String divClass;
    protected String htmlType;
    protected String urlSuffix;
    protected LinkedBlockingQueue<Integer> randomQueue = new LinkedBlockingQueue<>();

    /**
     * Default constructor
     * @param logger Logger used for logs
     * @param baseURL WebSite base url
     * @param divClass DivClass to search to extract image
     * @param htmlType HTML tag to extract image (img)
     */
    public NumberedCommande(Logger logger, String baseURL, String urlSuffix, String divClass, String htmlType) {
        this.logger = logger;
        this.baseURL = baseURL;
        this.divClass = divClass;
        this.htmlType = htmlType;
        this.urlSuffix = urlSuffix;
        try {
            logger.debug("Checking max...");
            maxNumber = LimitChecker.doYourJob(baseURL, 2, urlSuffix);
            logger.info("Limit is "+maxNumber);
        } catch (IOException e) {
            logger.catching(e);
        }
    }

    public NumberedCommande(Logger logger, String baseURL, String urlSuffix){
        this(logger, baseURL, urlSuffix, null, null);

    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        try
        {
            if(args.length == 0)
            {
               String result = poll();
               event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n"+result).queue();

            }
            else
            {
                switch (args[0].toLowerCase()) {
                    case "update":
                        logger.info("update commande from " + event.getMessage().getAuthor().getName());
                        event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:arrow_right: Updating...").queue();

                        int newNumber = maxNumber;
                        try {
                            newNumber = LimitChecker.doYourJob(baseURL, maxNumber, urlSuffix);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (newNumber == maxNumber) {
                            event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:arrow_right: 0 new picture found :cry:").queue();
                        } else if (newNumber - maxNumber == 1)
                            event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:arrow_right: " + (newNumber - maxNumber) + " picture has been found :kissing_heart:").queue();
                        else
                            event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:arrow_right: " + (newNumber - maxNumber) + " pictures has been found :kissing_heart:").queue();
                        logger.info((newNumber - maxNumber) + " new image(s) found.");
                        maxNumber = newNumber;
                        break;
                    case "get":
                        if (args.length >= 2) {

                            int number = -1;
                            try {
                                number = Integer.parseInt(args[1]);
                                URL url = new URL(baseURL + number + "-2/");
                                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                                huc.setRequestMethod("GET");
                                huc.connect();
                                int result = huc.getResponseCode();
                                if (result == 200) {
                                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n" + baseURL + number + urlSuffix).queue();
                                } else {
                                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Error__** :warning:\n:arrow_right: Page not found (404)").queue();
                                }

                            } catch (NumberFormatException e) {
                                event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Error__** :warning:\n:arrow_right: Bad argument. `//help " + this.toString().toLowerCase() + "` for more info ").queue();
                            } catch (IOException e) {
                                logger.catching(e);
                                event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Error__** :warning:\n:arrow_right: Internal error...").queue();
                            }
                        } else {
                            event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Error__** :warning:\n:arrow_right: Bad argument. `//help " + this.toString().toLowerCase() + "` for more info ").queue();
                            logger.warn("Bad Argument: " + event.getMessage().getContentRaw() + " From " + event.getAuthor().getName());
                        }
                        break;
                    default:
                        event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Error__** :warning:\n:arrow_right: Bad argument. `//help " + this.toString().toLowerCase() + "` for more info ").queue();
                        logger.warn("Bad Argument: " + event.getMessage().getContentRaw() + " From " + event.getAuthor().getName());
                        break;
                }
            }
        } catch (IOException e) {
            logger.catching(e);
        }


    }


    private void completeRandom() throws IOException {
        TrueRandom trueRandom = TrueRandom.getINSTANCE();
        ArrayList<Integer> numbers = trueRandom.getNumbers(minNumber, maxNumber);

        randomQueue.addAll(numbers);

    }

    protected void checkRandom() throws IOException {
        logger.trace("Queue size: " + randomQueue.size());
        if(randomQueue.isEmpty()){
            logger.debug("Queue empty, update it.");
            completeRandom();
        }
    }

    public String poll() throws IOException {
        checkRandom();
        int randomResult = randomQueue.poll();
        return FindContentOnWebPage.doYourJob(baseURL + randomResult + urlSuffix, divClass, htmlType);
    }


}
