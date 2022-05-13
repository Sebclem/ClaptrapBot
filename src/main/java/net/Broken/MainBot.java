package net.Broken;

import net.Broken.DB.Entity.UserEntity;
import net.Broken.Tools.Command.CommandParser;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.PrivateMessage;
import net.Broken.Tools.UserSpamUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main Class
 */
@SpringBootApplication
@Controller
public class MainBot {

    public static HashMap<String, Commande> commandes = new HashMap<>();
    public static HashMap<String, SlashCommand> slashCommands = new HashMap<>();
    public static HashMap<Member, ArrayList<Message>> historique =new HashMap<>();
    public static HashMap<Member, Integer> message_compteur =new HashMap<>();
    public static HashMap<String, Integer> mutualGuildCount =new HashMap<>();
    public static boolean roleFlag = false;
    public static HashMap<Member, UserSpamUtils> spamUtils = new HashMap<>();
    public static JDA jda;
    public static boolean ready = false;
    public static boolean dev = false;

    public static String url= "claptrapbot.com";




    public static int messageTimeOut = 10;
    public static int gifMessageTimeOut = 30;


    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {


        if(!Init.checkEnv())
            System.exit(1);

        logger.info("=======================================");
        logger.info("--------------Starting Bot-------------");
        logger.info("=======================================");
        if(System.getenv("DEV")!= null){
            dev = Boolean.parseBoolean(System.getenv("DEV"));
        }


        String token = System.getenv("DISCORD_TOKEN");

        jda = Init.initJda(token);


        ConfigurableApplicationContext ctx = SpringApplication.run(MainBot.class, args);
        if(jda == null) {
            System.exit(SpringApplication.exit(ctx, (ExitCodeGenerator) () -> {
                logger.fatal("Init error! Close application!");
                return 1;
            }));
        }

        Init.polish(jda);
        ready = true;

    }

    /**
     * Perform test (admin, NSFW and private usable or not) and execute command or not
     * @param cmd Container whit all command info
     */
    public static void handleCommand(CommandParser.CommandContainer cmd, UserEntity user)
    {

        if(!ready){
            return;
        }

        if (commandes.containsKey(cmd.commande)){
            Commande cmdObj = commandes.get(cmd.commande);
            boolean isAdmin;
            boolean isBotAdmin = user != null && user.isBotAdmin();
            if(cmd.event.isFromType(ChannelType.PRIVATE)){
                isAdmin = false;
            }
            else
                isAdmin = cmd.event.getMember().hasPermission(Permission.ADMINISTRATOR);

            if((!cmdObj.isAdminCmd() || isAdmin) && (!cmdObj.isBotAdminCmd() || isBotAdmin)){

                    if(cmd.event.isFromType(ChannelType.PRIVATE) && commandes.get(cmd.commande).isPrivateUsable())
                    {

                        commandes.get(cmd.commande).action(cmd.args, cmd.event);
                    }
                    else if (!cmd.event.isFromType(ChannelType.PRIVATE))
                    {
                        if(!cmdObj.isNSFW() || cmd.event.getTextChannel().isNSFW()){
                            commandes.get(cmd.commande).action(cmd.args, cmd.event);
                        }
                        else{
                            cmd.event.getMessage().delete().queue();
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
                    cmd.event.getTextChannel().sendMessage(EmbedMessageUtils.getUnautorized()).complete();
                }
            }
        }
        else{
            logger.debug("Unknown command : " + cmd.commande);
        }
    }



}
