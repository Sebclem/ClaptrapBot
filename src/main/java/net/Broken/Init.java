package net.Broken;

import net.Broken.RestApi.ApiCommandLoader;
import net.Broken.Tools.Command.CommandLoader;
import net.Broken.Tools.DayListener.DayListener;
import net.Broken.Tools.DayListener.Listeners.DailyMadame;
import net.Broken.Tools.DayListener.Listeners.ResetSpam;
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

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.List;


public class Init {
    static private Logger logger = LogManager.getLogger();

    /**
     * Initialize all bot functionality
     * @param token bot user token
     * @param dev dev Mode or not
     * @return JDA object
     */
    static JDA initJda(String token, boolean dev){
        JDA jda = null;
        logger.debug("-------------------INITIALISATION-------------------");

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
                jda = new JDABuilder(AccountType.BOT).addEventListener(new BotListener()).setToken(token).setBulkDeleteSplittingEnabled(false).buildBlocking();
                MainBot.jda = jda;
                jda.setAutoReconnect(true);
                jda.addEventListener();

                /*************************************
                 *      Definition des commande      *
                 *************************************/
                jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Game.playing("Loading..."));
                jda.getTextChannels().forEach(textChannel -> textChannel.sendTyping().queue());


                //On recupere le l'id serveur
                Guild serveur = jda.getGuilds().get(0);

                //on recupere les utilisateur
                List<Member> utilisateurCo = serveur.getMembers();

                logger.info("Online users: "+utilisateurCo.size());
                for (Member anUtilisateurCo : utilisateurCo)
                {
                    if (anUtilisateurCo.getOnlineStatus().equals(OnlineStatus.ONLINE))
                        logger.debug("\t*" + anUtilisateurCo.getEffectiveName());
                }

                logger.debug("Do not disturb users: ");
                for (Member anUtilisateurCo : utilisateurCo)
                {
                    if (anUtilisateurCo.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB))
                        logger.debug("\t*" + anUtilisateurCo.getEffectiveName());
                }

                logger.debug("Offline users: ");
                for (Member anUtilisateurCo : utilisateurCo)
                {
                    if (anUtilisateurCo.getOnlineStatus().equals(OnlineStatus.OFFLINE))
                        logger.debug("\t*" + anUtilisateurCo.getEffectiveName());
                }


                DayListener dayListener = DayListener.getInstance();
                dayListener.addListener(new ResetSpam());
                dayListener.addListener(new DailyMadame());
                dayListener.start();


                logger.debug("-----------------FIN INITIALISATION-----------------");


            }
            catch (LoginException | InterruptedException e)
            {
                logger.catching(e);
            }
        }

        return jda;
    }


    static void polish(JDA jda){
        CommandLoader.load();
        ApiCommandLoader.load();
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.playing("bot.seb6596.ovh"));


    }
}
