package net.Broken;

import net.Broken.Outils.CommandParser;
import net.Broken.Outils.EmbedMessageUtils;
import net.Broken.Outils.UserSpamUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by seb65 on 19/10/2016.
 */
@SpringBootApplication
public class MainBot {


    public static final CommandParser parser =new CommandParser();
    public static HashMap<String, Commande> commandes = new HashMap<>();
    public static HashMap<User, ArrayList<Message>> historique =new HashMap<>();
    public static HashMap<User, Integer> message_compteur =new HashMap<>();
    public static boolean roleFlag = false;
    public static HashMap<User, UserSpamUtils> spamUtils = new HashMap<>();

    public static int messageTimeOut = 10;


    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(MainBot.class, args);
        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");

        boolean dev = false;
        String token = null;
        int i = 0;
        for(String aArg: args){
            logger.debug(aArg);
            if(aArg.startsWith("--") || aArg.startsWith("-")){
                aArg = aArg.replaceAll("-","");
                if(aArg.equals("token") || aArg.equals("t")){
                    token = args[i+1];
                }
                else if(aArg.equals("dev") || aArg.equals("d")){
                    dev = true;
                }
            }
            i++;
        }

        JDA jda = Init.initBot(token, dev);
        if(jda == null) {
            System.exit(SpringApplication.exit(ctx, (ExitCodeGenerator) () -> {
                logger.fatal("Init error! Close application!");
                return 1;
            }));
        }
    }

    /***************************************
     *      Traitement de la commande      *
     ***************************************/
    public static void handleCommand(CommandParser.CommandContainer cmd)
    {
        //On verifie que la commande existe

        if (commandes.containsKey(cmd.commande))
        {
            if(cmd.event.isFromType(ChannelType.PRIVATE) && commandes.get(cmd.commande).isPrivateUsable())
            {

                commandes.get(cmd.commande).action(cmd.args, cmd.event);
                commandes.get(cmd.commande).executed(true, cmd.event);
            }
            else if (!cmd.event.isFromType(ChannelType.PRIVATE))
            {
                commandes.get(cmd.commande).action(cmd.args, cmd.event);
                commandes.get(cmd.commande).executed(true, cmd.event);
            }
            else
                cmd.event.getPrivateChannel().sendMessage(EmbedMessageUtils.getNoPrivate()).queue();


        }
        else
        {
            MessageReceivedEvent event = cmd.event;
            if(event.isFromType(ChannelType.PRIVATE))
                event.getPrivateChannel().sendMessage(EmbedMessageUtils.getUnknowCommand()).queue();
            else
                event.getTextChannel().sendMessage(EmbedMessageUtils.getUnknowCommand()).queue();
            logger.warn("Commande inconnue");
        }

    }
    /*******************************
     *      RAZ Compteur Spam      *
     *******************************/
    public static class ModoTimer extends Thread{


        public ModoTimer()
        {

        }


        @Override
        public void run()
        {
            while(true)
            {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (User unUser: message_compteur.keySet())
                {
                    MainBot.message_compteur.put(unUser, 0);
                }
            }

        }
    }

}
