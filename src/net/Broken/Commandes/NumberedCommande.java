package net.Broken.Commandes;
import net.Broken.Commande;
import net.Broken.Outils.FindContentOnWebPage;
import net.Broken.Outils.LimitChecker;
import net.Broken.Outils.Redirection;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by seb65 on 07/11/2016.
 */
public abstract class NumberedCommande implements Commande{
    Logger logger = LogManager.getLogger();
    public String HELP="T'es sérieux la?";
    int minNumber = 1;
    int maxNumber = -1;
    String baseURL;
    String divClass;
    String htmlType;


    public NumberedCommande(Logger logger, String baseURL, String divClass, String htmlType) {
        this.logger = logger;
        this.baseURL = baseURL;
        this.divClass = divClass;
        this.htmlType = htmlType;
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
        try
        {
            if(args.length == 0)
            {
                if(event.getTextChannel().getName().equals("nsfw-over18")) {
                    Redirection redirect= new Redirection();
                    int randomResult = (int) (minNumber + (Math.random() * (maxNumber - minNumber)));
                    String result = FindContentOnWebPage.doYourJob(baseURL + randomResult + "-2", divClass, htmlType);
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n"+result).queue();
                }
                else
                {
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Channel règlementé! Go sur over18!__**:warning: ").queue();

                    logger.warn("Erreur chanel.");
                }
            }
            else
            {
                if(args[0].toLowerCase().equals("update"))
                {
                    logger.info("update commande from "+event.getMessage().getAuthor().getName());
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:arrow_right: Updating...").queue();

                    int newNumber = maxNumber;
                    try {
                        newNumber = LimitChecker.doYourJob(baseURL,maxNumber);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(newNumber == maxNumber)
                    {
                        event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:arrow_right: Aucune nouvelle image n'a était trouver :cry:").queue();
                    }
                    else if(newNumber-maxNumber == 1)  event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:arrow_right: "+(newNumber-maxNumber)+" nouvelle image a été trouvé :kissing_heart:").queue();
                    else
                        event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:arrow_right: "+(newNumber-maxNumber)+" nouvelles images on été trouvé :kissing_heart:").queue();
                    logger.info((newNumber-maxNumber)+" new image(s) found.");
                    maxNumber = newNumber;
                }
                else if(args[0].toLowerCase().equals("get")) {
                    if (args.length >= 2)
                    {

                        int number = -1;
                        try {
                            number = Integer.parseInt(args[1]);
                            URL url = new URL(baseURL + number + "-2/");
                            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                            huc.setRequestMethod("GET");
                            huc.connect();
                            int result = huc.getResponseCode();
                            if (result == 200) {
                                event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n" + baseURL + number + "-2/").queue();
                            } else {
                                event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Erreur__** :warning:\n:arrow_right: Page introuvable (404)").queue();
                            }

                        } catch (NumberFormatException e) {
                            event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Erreur__** :warning:\n:arrow_right: Erreur d'argument. `//help " + this.toString().toLowerCase() + "` pour plus d'info ").queue();
                        } catch (IOException e) {
                            logger.catching(e);
                            event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Erreur__** :warning:\n:arrow_right: Erreur interne...").queue();
                        }
                    }
                    else{
                        event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Erreur__** :warning:\n:arrow_right: Erreur d'argument. `//help " + this.toString().toLowerCase() + "` pour plus d'info ").queue();
                        logger.warn("Bad Argument: "+event.getMessage().getContent()+" From "+event.getAuthor().getName());
                    }
                }
                else
                {
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Erreur__** :warning:\n:arrow_right: Erreur d'argument. `//help "+this.toString().toLowerCase()+"` pour plus d'info ").queue();
                    logger.warn("Bad Argument: "+event.getMessage().getContent()+" From "+event.getAuthor().getName());
                }
            }
        } catch (IOException e) {
            logger.catching(e);
        }


    }

    @Override
    public String help(String[] args) {
        return"`//"+this.toString().toLowerCase() +"update\n:arrow_right: *Rafraichi la liste des images.*\n`//"+this.toString().toLowerCase()+" get <numero>`\n:arrow_right: *Affiche l'image portant le numero donné*";
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }


}
