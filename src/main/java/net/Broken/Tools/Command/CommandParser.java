package net.Broken.Tools.Command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 */

public class CommandParser {
    private Logger logger = LogManager.getLogger();

    /**
     * Parse raw received string.
     * @param brt Raw command string.
     * @param e Event
     * @return Readable container that contain all useful data
     */
    public CommandContainer parse(String brt, MessageReceivedEvent e)
    {
        ArrayList<String> split =new ArrayList<String>();
        String brut =brt;
        String sansTete = brut.replaceFirst("//","");
        String[] splitSansTete = sansTete.split(" ");

        for(String s : splitSansTete){
            if(s.length()>0)
                split.add(s);
        }

        String commande = split.get(0);
        String[] args = new String[split.size()-1];
        split.subList(1,split.size()).toArray(args);

        for(int i=0;i<args.length;i++)
            args[i]=args[i].replace('$',' ');



        logger.info("Auteur: "+e.getAuthor().getName()+", Commande: "+commande+", args: "+ Arrays.toString(args));

        return new CommandContainer(brut, sansTete, splitSansTete, commande, args, e);

    }

    /**
     * Container
     */
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
