package net.Broken;

import net.Broken.RestApi.ApiCommandLoader;
import net.Broken.Tools.Command.CommandParser;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.Broken.Tools.PrivateMessage;
import net.Broken.Tools.UserManager.UserRegister;
import net.Broken.Tools.UserSpamUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
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
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by seb65 on 19/10/2016.
 */
@SpringBootApplication
@Controller
public class MainBot {

    public static HashMap<String, Commande> commandes = new HashMap<>();
    public static HashMap<User, ArrayList<Message>> historique =new HashMap<>();
    public static HashMap<User, Integer> message_compteur =new HashMap<>();
    public static boolean roleFlag = false;
    public static HashMap<User, UserSpamUtils> spamUtils = new HashMap<>();
    public static JDA jda;




    public static int messageTimeOut = 10;
    public static int gifMessageTimeOut = 30;


    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        logger.info("=======================================");
        logger.info("--------------Starting Bot-------------");
        logger.info("=======================================");

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

        jda = Init.initBot(token, dev);
        ConfigurableApplicationContext ctx = SpringApplication.run(MainBot.class, args);
        if(jda == null) {
            System.exit(SpringApplication.exit(ctx, (ExitCodeGenerator) () -> {
                logger.fatal("Init error! Close application!");
                return 1;
            }));
        }


        ApiCommandLoader.load();







    }

    /***************************************
     *      Traitement de la commande      *
     ***************************************/
    public static void handleCommand(CommandParser.CommandContainer cmd)
    {
        //On verifie que la commande existe

        if (commandes.containsKey(cmd.commande))
        {
            Commande cmdObj = commandes.get(cmd.commande);
            boolean isAdmin;
            if(cmd.event.isFromType(ChannelType.PRIVATE)){
                isAdmin = jda.getGuilds().get(0).getMember(cmd.event.getAuthor()).hasPermission(Permission.ADMINISTRATOR);
            }
            else
                isAdmin = cmd.event.getMember().hasPermission(Permission.ADMINISTRATOR);

            if(!cmdObj.isAdminCmd() || isAdmin){

                if(cmd.event.isFromType(ChannelType.PRIVATE) && commandes.get(cmd.commande).isPrivateUsable())
                {

                    commandes.get(cmd.commande).action(cmd.args, cmd.event);
                    commandes.get(cmd.commande).executed(true, cmd.event);
                }
                else if (!cmd.event.isFromType(ChannelType.PRIVATE))
                {
                    if(!cmdObj.isNSFW() || cmd.event.getTextChannel().isNSFW()){
                        commandes.get(cmd.commande).action(cmd.args, cmd.event);
                        commandes.get(cmd.commande).executed(true, cmd.event);
                    }
                    else{
                        Message msg = cmd.event.getTextChannel().sendMessage(cmd.event.getAuthor().getAsMention() + "\n:warning: **__Channel règlementé! Go sur over18!__**:warning: ").complete();
                        new MessageTimeOut(messageTimeOut, msg, cmd.event.getMessage()).start();
                    }

                }
                else
                    cmd.event.getPrivateChannel().sendMessage(EmbedMessageUtils.getNoPrivate()).queue();
            }
            else{
                if(cmd.event.isFromType(ChannelType.PRIVATE)){
                    PrivateMessage.send(cmd.event.getAuthor(),EmbedMessageUtils.getUnautorized(), logger);
                }
                else{
                    Message msg = cmd.event.getTextChannel().sendMessage(EmbedMessageUtils.getUnautorized()).complete();
                    new MessageTimeOut(gifMessageTimeOut, msg, cmd.event.getMessage()).start();
                }
            }




        }
        else
        {
            MessageReceivedEvent event = cmd.event;
            if(event.isFromType(ChannelType.PRIVATE))
                event.getPrivateChannel().sendMessage(EmbedMessageUtils.getUnknowCommand()).queue();
            else {
                Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getUnknowCommand()).complete();
                new MessageTimeOut(messageTimeOut, message, event.getMessage());
            }
            logger.warn("Commande inconnue");
        }


    }
    /*******************************
     *      RAZ Compteur Spam      *
     *******************************/
    public static class ModoTimer extends Thread{


        public ModoTimer(){}


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
