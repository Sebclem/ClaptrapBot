package net.Broken;

import net.Broken.Commandes.*;
import net.Broken.Commandes.Over18.*;
import net.Broken.Outils.CommandParser;
import net.Broken.Outils.DayListener;
import net.Broken.Outils.EmbedMessageUtils;
import net.Broken.Outils.UserSpamUtils;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.managers.GuildManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by seb65 on 19/10/2016.
 */
public class MainBot {

    private static boolean dev = false;
    private static String token = null;
    private static JDA jda;
    public static final CommandParser parser =new CommandParser();
    public static HashMap<String, Commande> commandes = new HashMap<>();
    public static boolean okInit=false;
    public static HashMap<User, ArrayList<Message>> historique =new HashMap<>();
    public static HashMap<User, Integer> message_compteur =new HashMap<>();

    public static HashMap<User, UserSpamUtils> spamUtils = new HashMap<>();


    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
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


        /****************************
         *      Initialisation      *
         ****************************/
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
                okInit=true;

            }
            catch (LoginException | InterruptedException | RateLimitedException e)
            {
                logger.catching(e);
                okInit=false;
            }
        }

        //Connection reussi
        if(okInit)
        {
            /*************************************
             *      Definition des commande      *
             *************************************/
            jda.getPresence().setGame(Game.of("Statut: Loading..."));
            jda.getTextChannels().forEach(textChannel -> textChannel.sendTyping().queue());
            commandes.put("ping", new PingCommande());
            commandes.put("help",new Help());
            commandes.put("move", new Move());
            commandes.put("spam", new Spam());

            if(!dev){
                commandes.put("ass",new Ass());
                jda.getTextChannels().forEach(textChannel -> textChannel.sendTyping().queue());
                commandes.put("boobs",new Boobs());
                jda.getTextChannels().forEach(textChannel -> textChannel.sendTyping().queue());
                commandes.put("pipe",new Pipe());
                jda.getTextChannels().forEach(textChannel -> textChannel.sendTyping().queue());
                commandes.put("sm",new SM());
                commandes.put("madame",new Madame());
                commandes.put("cat",new Cat());
            }
            commandes.put("spaminfo",new SpamInfo());


            //On recupere le l'id serveur
            Guild serveur = jda.getGuilds().get(0);

            //On recupere le serveur manageur
            GuildManager guildManager = serveur.getManager();

            //on recupere les utilisateur
            List<Member> utilisateurCo = serveur.getMembers();

            logger.info("Utilisatieur connecté: ");
            for (Member anUtilisateurCo : utilisateurCo)                      //= for(int i=0; i<utilisateurCo.size(); i++)
            {
                if(anUtilisateurCo.getOnlineStatus().equals(OnlineStatus.ONLINE))
                    logger.info("\t*" + anUtilisateurCo.getEffectiveName());    //anUtilisateurCo = utilisateurCo.get(i)
            }
            logger.info("Utilisatieur absent: ");
            for (Member anUtilisateurCo : utilisateurCo)                      //= for(int i=0; i<utilisateurCo.size(); i++)
            {
                if(anUtilisateurCo.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB))
                    logger.info("\t*" + anUtilisateurCo.getEffectiveName());    //anUtilisateurCo = utilisateurCo.get(i)
            }
            logger.info("Utilisatieur hors ligne: ");
            for (Member anUtilisateurCo : utilisateurCo)                      //= for(int i=0; i<utilisateurCo.size(); i++)
            {
                if(anUtilisateurCo.getOnlineStatus().equals(OnlineStatus.OFFLINE))
                    logger.info("\t*" + anUtilisateurCo.getEffectiveName());    //anUtilisateurCo = utilisateurCo.get(i)
            }
            ModoTimer modotimer = new ModoTimer();
            modotimer.start();
            DayListener dayListener =new DayListener();
            dayListener.start();
            logger.info("-----------------FIN INITIALISATION-----------------");

            jda.getPresence().setGame(Game.of("Statut: Ok!"));
//            MessageEmbed test = EmbedMessageUtils.getHelp("test",command)
//            jda.getTextChannels().get(0).sendMessage(test).queue();

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
                cmd.event.getPrivateChannel().sendMessage("\n:warning: **__Commande non disponible en privé!__** :warning:").queue();


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
            while (true)
            {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //System.out.println("\n5sec Ecoulées !");
                for (User unUser: message_compteur.keySet() )         //=for(int i=0; i<saveRoleUser.size(); i++)
                {
                    MainBot.message_compteur.put(unUser, 0);
                }
            }

        }
    }

}
