package net.borken.commandes.Over18;

import net.borken.Commande;
import net.borken.Outils.Redirection;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by seb65 on 07/11/2016.
 */
public class Pipe implements Commande {
    Logger logger = LogManager.getLogger();
    public String HELP="T'es sérieux la?";
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        Redirection redirect= new Redirection();
        if(event.getTextChannel().getName().equals("over18"))
        {

            try {
                System.out.println("ok");
                String pipeLine=null;
                URL pipeURL = new URL(redirect.get("http://feelation.com/random"));
                URLConnection cc = pipeURL.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(cc.getInputStream(), "UTF-8"));
                String inputLine;


                while ((inputLine = in.readLine()) != null)
                {
                    System.out.println(inputLine);
                    if(inputLine.contains("class=\"post photo\""))
                    {
                        String[] splited = inputLine.split("<");
                        for(String aString:splited)
                        {
                            if(aString.startsWith("<img src"))
                            {
                                pipeLine=aString;
                                pipeLine=inputLine.replaceAll("<img src=\"","");
                                pipeLine=pipeLine.substring(0,pipeLine.indexOf("\""));
                                System.out.println(pipeLine);
                                break;

                            }
                        }
                    }

                }

                in.close();
                event.getTextChannel().sendMessage(redirect.get("http://feelation.com/random")).queue();
            } catch (IOException e) {
                logger.warn("Erreur de redirection.");
                event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Erreur de redirection (5 essais), Réessayez__**:warning: ").queue();
            }
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
