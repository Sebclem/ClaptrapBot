package net.borken.commandes.Over18;

import net.borken.Commande;
import net.borken.Outils.Redirection;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.io.IOException;

import static net.borken.MainBot.entete;

/**
 * Created by seb65 on 11/11/2016.
 */
public class Madame implements Commande{
    public String HELP="T'es sérieux la?";
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(!event.getTextChannel().getName().equals("le_cours")) {
            Redirection redirect = new Redirection();
            try {
                event.getTextChannel().sendMessage(redirect.get("http://dites.bonjourmadame.fr/random")).queue();
            } catch (IOException e) {
                System.out.println(entete.get("ERREUR", "Madame") + "Erreur de redirection.");

                event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n:warning: **__Erreur de redirection, Réessayez__**:warning: ").queue();
            }
        }
        else
        {
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Channel règlementé!__**:warning: ").queue();

            System.out.println(entete.get("ERREUR","Madame")+"Erreur chanel.");
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
