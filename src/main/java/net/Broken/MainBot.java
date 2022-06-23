package net.Broken;

import net.dv8tion.jda.api.JDA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;

/**
 * Main Class
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MainBot {

    public static HashMap<String, Commande> commandes = new HashMap<>();
    public static final HashMap<String, SlashCommand> slashCommands = new HashMap<>();
    public static HashMap<String, Integer> mutualGuildCount = new HashMap<>();
    public static boolean roleFlag = false;
    public static JDA jda;
    public static boolean ready = false;

    public static int messageTimeOut = 10;
    public static int gifMessageTimeOut = 30;


    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(MainBot.class, args);
        BotConfigLoader config = ctx.getBean(BotConfigLoader.class);

        logger.info("=======================================");
        logger.info("--------------Starting Bot-------------");
        logger.info("=======================================");

        jda = Init.initJda(config);
        if (jda == null) {
            System.exit(SpringApplication.exit(ctx, () -> {
                logger.fatal("Init error! Close application!");
                return 1;
            }));
        }

        Init.polish(jda, config);
        ready = true;
    }
}
