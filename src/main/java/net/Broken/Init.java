package net.Broken;

import net.Broken.Commandes.*;
import net.Broken.Commandes.Over18.*;
import net.Broken.Outils.DayListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.managers.GuildManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.List;

public class Init {
    static private Logger logger = LogManager.getLogger();

    static JDA initBot(String token, boolean dev){
        boolean okInit;
        JDA jda = null;
        logger.info("-------------------INITIALISATION-------------------");

        //Bot démarrer sans token
        if (token == null) {
            logger.fatal("Veuilliez indiquer le token du bot en argument...");
            okInit=false;
        }
        else
        {
            //Token présent
            try
            {

                logger.info("Connection au serveur...");
                //connection au bot
                jda = new JDABuilder(AccountType.BOT).addEventListener(new BotListener()).setToken(token).setBulkDeleteSplittingEnabled(false).buildBlocking();
                jda.setAutoReconnect(true);
                jda.addEventListener();

                /*************************************
                 *      Definition des commande      *
                 *************************************/
                jda.getPresence().setGame(Game.of("Statut: Loading..."));
                jda.getTextChannels().forEach(textChannel -> textChannel.sendTyping().queue());
                MainBot.commandes.put("ping", new PingCommande());
                MainBot.commandes.put("help", new Help());
                MainBot.commandes.put("move", new Move());
                MainBot.commandes.put("spam", new Spam());
                MainBot.commandes.put("spaminfo", new SpamInfo());
                MainBot.commandes.put("flush", new Flush());
                MainBot.commandes.put("music", new Music());

                if (!dev) {
                    MainBot.commandes.put("ass", new Ass());
                    jda.getTextChannels().forEach(textChannel -> textChannel.sendTyping().queue());
                    MainBot.commandes.put("boobs", new Boobs());
                    jda.getTextChannels().forEach(textChannel -> textChannel.sendTyping().queue());
                    MainBot.commandes.put("pipe", new Pipe());
                    jda.getTextChannels().forEach(textChannel -> textChannel.sendTyping().queue());
                    MainBot.commandes.put("sm", new SM());
                    MainBot.commandes.put("madame", new Madame());
                    MainBot.commandes.put("cat", new Cat());
                }


                //On recupere le l'id serveur
                Guild serveur = jda.getGuilds().get(0);

                //on recupere les utilisateur
                List<Member> utilisateurCo = serveur.getMembers();

                logger.info("Utilisatieur connecté: ");
                for (Member anUtilisateurCo : utilisateurCo)
                {
                    if (anUtilisateurCo.getOnlineStatus().equals(OnlineStatus.ONLINE))
                        logger.info("\t*" + anUtilisateurCo.getEffectiveName());
                }

                logger.info("Utilisatieur absent: ");
                for (Member anUtilisateurCo : utilisateurCo)
                {
                    if (anUtilisateurCo.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB))
                        logger.info("\t*" + anUtilisateurCo.getEffectiveName());
                }

                logger.info("Utilisatieur hors ligne: ");
                for (Member anUtilisateurCo : utilisateurCo)
                {
                    if (anUtilisateurCo.getOnlineStatus().equals(OnlineStatus.OFFLINE))
                        logger.info("\t*" + anUtilisateurCo.getEffectiveName());
                }

                MainBot.ModoTimer modotimer = new MainBot.ModoTimer();
                modotimer.start();
                DayListener dayListener = new DayListener();
                dayListener.start();
                logger.info("-----------------FIN INITIALISATION-----------------");

                jda.getPresence().setGame(Game.of("Statut: Ok!"));

            }
            catch (LoginException | InterruptedException | RateLimitedException e)
            {
                logger.catching(e);
            }
        }

        return jda;
    }
}
