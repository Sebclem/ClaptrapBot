package net.Broken;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
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
    public static HashMap<String, Integer> mutualGuildCount = new HashMap<>();
    public static boolean roleFlag = false;
    public static JDA jda;
    public static boolean ready = false;
    public static boolean dev = false;

    public static String url = "claptrapbot.com";


    public static int messageTimeOut = 10;
    public static int gifMessageTimeOut = 30;


    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {


        if (!Init.checkEnv())
            System.exit(1);

        logger.info("=======================================");
        logger.info("--------------Starting Bot-------------");
        logger.info("=======================================");
        if (System.getenv("DEV") != null) {
            dev = Boolean.parseBoolean(System.getenv("DEV"));
        }


        String token = System.getenv("DISCORD_TOKEN");

        jda = Init.initJda(token);


        ConfigurableApplicationContext ctx = SpringApplication.run(MainBot.class, args);
        if (jda == null) {
            System.exit(SpringApplication.exit(ctx, (ExitCodeGenerator) () -> {
                logger.fatal("Init error! Close application!");
                return 1;
            }));
        }

        Init.polish(jda);
        ready = true;

    }
}
