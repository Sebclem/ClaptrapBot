package net.Broken;

import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.RestApi.ApiCommandLoader;
import net.Broken.Tools.Command.CommandLoader;
import net.Broken.Tools.DayListener.DayListener;
import net.Broken.Tools.DayListener.Listeners.DailyMadame;
import net.Broken.Tools.DayListener.Listeners.ResetSpam;
import net.Broken.Tools.UserManager.Stats.UserStatsUtils;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import javax.security.auth.login.LoginException;
import java.util.List;


public class Init {
    static private Logger logger = LogManager.getLogger();

    /**
     * Initialize all bot functionality
     *
     * @param token bot user token
     * @return JDA object
     */
    static JDA initJda(String token) {
        JDA jda = null;
        logger.info("-----------------------INIT-----------------------");

        //Bot démarrer sans token
        if (token == null) {
            logger.fatal("Please enter bot token as an argument.");
        } else {
            //Token présent
            try {

                logger.info("Connecting to Discord api...");
                //connection au bot
                jda = new JDABuilder(AccountType.BOT).setToken(token).setBulkDeleteSplittingEnabled(false).build();
                jda = jda.awaitReady();
                MainBot.jda = jda;
                jda.setAutoReconnect(true);


                /*************************************
                 *      Definition des commande      *
                 *************************************/
                jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.playing("Loading..."));

                jda.getTextChannels().forEach(textChannel -> {
                    if (textChannel.canTalk())
                        textChannel.sendTyping().complete();


                });


                //On recupere le l'id serveur

                logger.info("Connected on " + jda.getGuilds().size() + " Guilds:");

                for (Guild server : jda.getGuilds()) {
                    //on recupere les utilisateur
                    logger.info("... " + server.getName() + " " + server.getMembers().size() + " Members");
                }


            } catch (LoginException | InterruptedException e) {
                logger.catching(e);
            }
        }

        return jda;
    }


    static void polish(JDA jda) {
        logger.info("Check database...");
        checkDatabase();
        CommandLoader.load();
        ApiCommandLoader.load();
        DayListener dayListener = DayListener.getInstance();
        dayListener.addListener(new ResetSpam());
        dayListener.addListener(new DailyMadame());
        dayListener.start();
        jda.addEventListener(new BotListener());
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(MainBot.url));


        logger.info("-----------------------END INIT-----------------------");


    }


    private static void checkDatabase() {
        ApplicationContext context = SpringContext.getAppContext();
        UserRepository userRepository = (UserRepository) context.getBean("userRepository");
        List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
        UserStatsUtils userStatsUtils = UserStatsUtils.getINSTANCE();
        logger.debug("Stats...");
        for (UserEntity userEntity : users) {
            logger.debug("..." + userEntity.getName());
            userStatsUtils.getUserStats(userEntity);

        }


    }


    public static boolean checkEnv() {
        boolean ok = true;


        if (System.getenv("PORT") == null) {
            logger.fatal("Missing PORT ENV variable.");
            ok = false;
        }

        if (System.getenv("DB_URL") == null) {
            logger.fatal("Missing DB_URL ENV variable.");
            ok = false;
        }

        if (System.getenv("DB_USER") == null) {
            logger.fatal("Missing DB_USER ENV variable.");
            ok = false;
        }


        if (System.getenv("DB_PWD") == null) {
            logger.fatal("Missing DB_PWD ENV variable.");
            ok = false;
        }

        if (System.getenv("OAUTH_URL") == null) {
            logger.fatal("Missing OAUTH_URL ENV variable.");
            ok = false;
        }

        if (System.getenv("DISCORD_TOKEN") == null) {
            logger.fatal("Missing DISCORD_TOKEN ENV variable.");
            ok = false;
        }

        if (System.getenv("GOOGLE_API_KEY") == null) {
            logger.fatal("Missing GOOGLE_API_KEY ENV variable.");
            ok = false;
        }

        if (System.getenv("RANDOM_API_KEY") == null) {
            logger.fatal("Missing GOOGLE_API_KEY ENV variable.");
            ok = false;
        }

        if (System.getenv("LOG_LEVEL") == null) {
            logger.fatal("Missing LOG_LEVEL ENV variable.");
            ok = false;
        }

        return ok;
    }
}
