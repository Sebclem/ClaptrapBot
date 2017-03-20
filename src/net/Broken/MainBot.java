package net.Broken;

import net.Broken.Outils.CommandParser;
import net.Broken.Outils.DayListener;
import net.Broken.Outils.Redirection;
import net.Broken.commandes.*;
import net.Broken.commandes.Over18.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.managers.GuildManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

/**
 * Created by seb65 on 19/10/2016.
 */
public class MainBot {

    private static JDA jda;
    public static final CommandParser parser =new CommandParser();
    public static HashMap<String, Commande> commandes = new HashMap<>();
    public static boolean okInit=false;
    public static HashMap<Member, String[]> historique =new HashMap<>();
    public static HashMap<Member, Integer> message_compteur =new HashMap<>();

    public static Hashtable<Member,Integer> userMulti = new Hashtable();
    public static Hashtable<Member,Boolean> minuteurStatut = new Hashtable<>();

    static Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
        Stop stopTh=new Stop();
        stopTh.start();
        /****************************
         *      Initialisation      *
         ****************************/
        logger.info("-------------------INITIALISATION-------------------");
        //Bot démarrer sans token
        if (args.length < 1) {
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
                jda = new JDABuilder(AccountType.BOT).addListener(new BotListener()).setToken(args[0]).setBulkDeleteSplittingEnabled(false).buildBlocking();
                jda.setAutoReconnect(true);
                jda.addEventListener();
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
            commandes.put("ping", new PingCommande());
            commandes.put("help",new Help());
            commandes.put("move", new Move());
            commandes.put("spam", new Spam());
            commandes.put("ass",new Ass());
            commandes.put("boobs",new Boobs());
            commandes.put("pipe",new Pipe());
            commandes.put("sm",new SM());
            commandes.put("madame",new Madame());
            commandes.put("cat",new Cat());
            //On recupere le l'id serveur
            Guild serveur = jda.getGuilds().get(0);

            //On recupere le serveur manageur
            GuildManager guildManager = serveur.getManager();

            //on recupere les utilisateur conecter
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

            /*List<User> userAction = serveur.getUsersByName("Broken_Fire");
            new Move().exc(userAction.get(0),"Big Daddy",true,serveur,serveur.getManager());*/
            Redirection urlRedirect=new Redirection();

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
                commandes.get(cmd.commande).action(cmd.args, cmd.event);
                commandes.get(cmd.commande).executed(true, cmd.event);

        }
        else
        {
            MessageReceivedEvent event = cmd.event;
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Commande inconnue!__** :warning:\n:arrow_right: Utilisez `//help` pour voirs les commandes disponible. ").queue();
            logger.info("Commande inconnue");
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
                for (Member unUser: message_compteur.keySet() )         //=for(int i=0; i<saveRoleUser.size(); i++)
                {
                    MainBot.message_compteur.put(unUser, 0);
                }
            }

        }
    }


    public static class Stop extends Thread
    {

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            String txtEntré = "";
            while(!txtEntré.equals("o")&&!txtEntré.equals("O"))
            {

                while (!txtEntré.equals("stop"))
                {
                    txtEntré = scanner.nextLine();
                }

                logger.warn("Etes-vous sur de vouloir arréter le Bot? (o/n)");
                txtEntré = scanner.nextLine();

                if(txtEntré.equals("n")||txtEntré.equals("N"))
                {
                    logger.info("Arret du Bot annulé.");
                }
            }
            Runtime.getRuntime().exit(0);


        }
    }
}
