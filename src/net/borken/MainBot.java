package net.borken;

import enigma.console.Console;
import enigma.console.TextAttributes;
import enigma.core.Enigma;
import net.borken.Outils.CommandParser;
import net.borken.Outils.DayListener;
import net.borken.Outils.Entete;
import net.borken.Outils.Redirection;
import net.borken.commandes.*;
import net.borken.commandes.Over18.*;


import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.managers.GuildManager;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by seb65 on 19/10/2016.
 */
public class MainBot {

    private static JDA jda;
    public static final CommandParser parser =new CommandParser();
    public static HashMap<String, Commande> commandes = new HashMap<>();
    public static boolean okInit=false;
    public static Entete entete=new Entete();
    public static HashMap<Member, String[]> historique =new HashMap<>();
    public static HashMap<Member, Integer> message_compteur =new HashMap<>();

    public static Hashtable<Member,Integer> userMulti = new Hashtable();
    public static Hashtable<Member,Boolean> minuteurStatut = new Hashtable<>();

    public static TextAttributes txtColor;

    public static void main(String[] args) throws IOException {

        Stop stopTh=new Stop();
        stopTh.start();
        txtColor = new TextAttributes(Color.green, Color.black);
        s_console.setTextAttributes(txtColor);
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        /****************************
         *      Initialisation      *
         ****************************/
        System.out.println("-------------------INITIALISATION-------------------");
        txtColor = new TextAttributes(Color.blue, Color.black);
        s_console.setTextAttributes(txtColor);
        //Bot démarrer sans token
        if (args.length < 1) {
            System.out.println();
            System.err.println(entete.get("ERREUR","INIT")+"Veuilliez indiquer le token du bot en argument...");
            okInit=false;
        }
        else
        {
            //Token présent
            try
            {
                System.out.println();
                System.out.println(entete.get("Info","INIT")+"Connection au serveur...");
                //connection au bot
                jda = new JDABuilder(AccountType.BOT).addListener(new BotListener()).setToken(args[0]).setBulkDeleteSplittingEnabled(false).buildBlocking();
                jda.setAutoReconnect(true);
                okInit=true;

            }
            catch (LoginException | InterruptedException | RateLimitedException e)
            {
                System.out.println();
                System.err.println(entete.get("ERREUR","INIT")+e.getMessage());
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

            //On recupere le l'id serveur
            Guild serveur = jda.getGuilds().get(0);

            //On recupere le serveur manageur
            GuildManager guildManager = serveur.getManager();

            //on recupere les utilisateur conecter
            List<Member> utilisateurCo = serveur.getMembers();
            System.out.println();
            System.out.println(entete.get("Info","INIT")+"Utilisatieur connecté: ");
            for (Member anUtilisateurCo : utilisateurCo)                      //= for(int i=0; i<utilisateurCo.size(); i++)
            {
                if(anUtilisateurCo.getOnlineStatus().equals(OnlineStatus.ONLINE))
                    System.out.println(entete.get("Info", "INIT") + "    *" + anUtilisateurCo.getEffectiveName());    //anUtilisateurCo = utilisateurCo.get(i)
            }
            System.out.println();
            System.out.println(entete.get("Info","INIT")+"Utilisatieur absent: ");
            for (Member anUtilisateurCo : utilisateurCo)                      //= for(int i=0; i<utilisateurCo.size(); i++)
            {
                if(anUtilisateurCo.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB))
                    System.out.println(entete.get("Info", "INIT") + "    *" + anUtilisateurCo.getEffectiveName());    //anUtilisateurCo = utilisateurCo.get(i)
            }
            System.out.println();
            System.out.println(entete.get("Info","INIT")+"Utilisatieur hors ligne: ");
            for (Member anUtilisateurCo : utilisateurCo)                      //= for(int i=0; i<utilisateurCo.size(); i++)
            {
                if(anUtilisateurCo.getOnlineStatus().equals(OnlineStatus.OFFLINE))
                    System.out.println(entete.get("Info", "INIT") + "    *" + anUtilisateurCo.getEffectiveName());    //anUtilisateurCo = utilisateurCo.get(i)
            }
            txtColor = new TextAttributes(Color.green, Color.black);
            s_console.setTextAttributes(txtColor);
            ModoTimer modotimer = new ModoTimer();
            modotimer.start();
            DayListener dayListener =new DayListener();
            dayListener.start();
            System.out.println("-----------------FIN INITIALISATION-----------------");
            txtColor = new TextAttributes(Color.blue, Color.black);
            s_console.setTextAttributes(txtColor);

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

    public static final Console s_console;
    static
    {
        s_console = Enigma.getConsole("Bot Discord");
    }


    public static class Stop extends Thread
    {

        @Override
        public void run() {
            String txtEntré = "";
            while(!txtEntré.equals("o")&&!txtEntré.equals("O"))
            {

                while (!txtEntré.equals("stop"))
                {
                    txtEntré = s_console.readLine();
                }
                txtColor = new TextAttributes(Color.orange, Color.black);
                s_console.setTextAttributes(txtColor);
                System.out.println("Etes-vous sur de vouloir arréter le Bot? (o/n)");
                txtColor = new TextAttributes(Color.blue, Color.black);
                s_console.setTextAttributes(txtColor);
                txtEntré = s_console.readLine();

                if(txtEntré.equals("n")||txtEntré.equals("N"))
                {
                    txtColor = new TextAttributes(Color.green, Color.black);
                    s_console.setTextAttributes(txtColor);
                    System.out.println("Arret du Bot annulé.");
                    txtColor = new TextAttributes(Color.blue, Color.black);
                    s_console.setTextAttributes(txtColor);
                }
            }
            Runtime.getRuntime().exit(0);


        }
    }
}
