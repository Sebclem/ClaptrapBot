package net.Broken;

import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Entity.UserStats;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.RestApi.ApiCommandLoader;
import net.Broken.Tools.Command.CommandLoader;
import net.Broken.Tools.DayListener.DayListener;
import net.Broken.Tools.DayListener.Listeners.DailyMadame;
import net.Broken.Tools.DayListener.Listeners.ResetSpam;
import net.Broken.Tools.UserManager.UserStatsUtils;
import net.Broken.audio.Youtube.YoutubeTools;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.RichPresence;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.List;


public class Init {
    static private Logger logger = LogManager.getLogger();

    /**
     * Initialize all bot functionality
     * @param token bot user token
     * @return JDA object
     */
    static JDA initJda(String token){
        JDA jda = null;
        logger.info("-----------------------INIT-----------------------");

        //Bot démarrer sans token
        if (token == null) {
            logger.fatal("Veuilliez indiquer le token du bot en argument...");
        }
        else
        {
            //Token présent
            try
            {

                logger.info("Connecting to Discord api...");
                //connection au bot
                jda = new JDABuilder(AccountType.BOT).setToken(token).setBulkDeleteSplittingEnabled(false).buildBlocking();
                MainBot.jda = jda;
                jda.setAutoReconnect(true);


                /*************************************
                 *      Definition des commande      *
                 *************************************/
                jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Game.playing("Loading..."));
                jda.getTextChannels().forEach(textChannel -> textChannel.sendTyping().queue());


                //On recupere le l'id serveur

                logger.info("Connected on " + jda.getGuilds().size() + " Guilds:");

                for( Guild server : jda.getGuilds()){
                    //on recupere les utilisateur
                    logger.info("... " + server.getName() + " " + server.getMembers().size() + " Members");
                }


            }
            catch (LoginException | InterruptedException e)
            {
                logger.catching(e);
            }
        }

        return jda;
    }


    static void polish(JDA jda){
        logger.info("Check database...");
        checkDatabase();
        CommandLoader.load();
        ApiCommandLoader.load();
        DayListener dayListener = DayListener.getInstance();
        dayListener.addListener(new ResetSpam());
        dayListener.addListener(new DailyMadame());
        dayListener.start();
        jda.addEventListener(new BotListener());
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.playing(MainBot.url));


        logger.info("-----------------------END INIT-----------------------");


    }


    private static void checkDatabase(){
        ApplicationContext context = SpringContext.getAppContext();
        UserRepository userRepository = (UserRepository) context.getBean("userRepository");
        List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
        UserStatsUtils userStatsUtils = UserStatsUtils.getINSTANCE();
        logger.debug("Stats...");
        for(UserEntity userEntity : users){
            logger.debug("..." + userEntity.getName());
            userStatsUtils.getUserStats(userEntity);

        }


    }
}
