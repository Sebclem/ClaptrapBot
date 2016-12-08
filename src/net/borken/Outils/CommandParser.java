package net.borken.Outils;

import net.dv8tion.jda.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by seb65 on 19/10/2016.
 */

public class CommandParser {
    public static Entete entete=new Entete();
    public CommandContainer parse(String brt, MessageReceivedEvent e)
    {
        ArrayList<String> split =new ArrayList<String>();
        String brut =brt;
        String sansTete = brut.replaceFirst("//","");   // on retire l'entete
        String[] splitSansTete = sansTete.split(" ");   // on prend l'espace comme separateur cmd/arg
        for(String s : splitSansTete){                  //= pourchaque
            split.add(s);                               // on fait un truc que je comprend pas trop x)
        }
        String commande = split.get(0);                 // on recuperre la 1er partie qui correspond a la cmd
        String[] args = new String[split.size()-1];
        split.subList(1,split.size()).toArray(args);
        for(int i=0;i<args.length;i++)
        {
            args[i]=args[i].replace('$',' ');


        }
        System.out.println(entete.get("Info","CMD")+"Auteur: "+e.getMember().getEffectiveName()+", Commande: "+commande+", args: "+ Arrays.toString(args));
        return new CommandContainer(brut, sansTete, splitSansTete, commande, args, e);   //On Save toute les info dans le container

    }
    public class CommandContainer{
        public final String brut;
        public final String sansTete;
        public final String[] splitSansTete;
        public final String commande;
        public final String[] args;
        public final MessageReceivedEvent event;

        public CommandContainer(String brut, String sansTete, String[] splitSansTete, String commande, String[] args, MessageReceivedEvent e)
        {
            this.brut=brut;
            this.sansTete=sansTete;
            this.splitSansTete=splitSansTete;
            this.commande=commande;
            this.args=args;
            this.event= e;
        }
    }
}
