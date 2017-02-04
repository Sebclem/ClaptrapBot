package net.borken.commandes;

import net.borken.Commande;
import net.borken.MainBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Created by seb65 on 23/10/2016.
 */
public class Help implements Commande {
    Logger logger = LogManager.getLogger();
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(args.length>=1)
        {
            String argsString = args[0];
            //System.out.println(argsString);
            if (MainBot.commandes.containsKey(argsString))
            {
                logger.info("Aide demmander pour la cmd "+argsString+" par "+event.getMember().getEffectiveName());
                event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n\n"+MainBot.commandes.get(argsString).help(args)).queue();


            }
            else
            {
                event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Commande Inconue!__** :warning:").queue();
                logger.info("Commande Inconnue!");
            }
        }
        else
        {
            String txt="";
            for (Map.Entry<String, Commande> e : MainBot.commandes.entrySet()) {
                txt=txt+"\n//"+e.getKey();

            }
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:arrow_right:\t**__commandes envoyées par message privé__**").queue();
            event.getAuthor().getPrivateChannel().sendMessage("Commandes du bot:\n\n```"+txt+"```\n\nUtilise `//help <commande>` pour plus de détails.").queue();




        }


    }

    @Override
    public String help(String[] args) {
        return null;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }
}
