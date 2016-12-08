package net.borken.commandes.Over18;

import net.borken.Commande;
import net.borken.Outils.Entete;
import net.borken.Outils.Redirection;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.awt.*;
import java.io.IOException;

/**
 * Created by seb65 on 07/11/2016.
 */
public class Ass implements Commande{
    public String HELP="T'es sérieux la?";
    Entete entete= new Entete();

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(event.getTextChannel().getName().equals("over18")) {
            Redirection redirect= new Redirection();
            try {
                event.getTextChannel().sendMessage(redirect.get("http://les400culs.com/random")).queue();
                System.out.println(entete.get("ERREUR","Ass")+"Erreur de redirection.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Channel règlementé! Go sur over18!__**:warning: ").queue();

            System.out.println(entete.get("ERREUR","Ass")+"Erreur chanel.");
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
