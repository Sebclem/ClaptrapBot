package net.Broken.Outils;

import net.Broken.MainBot;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Parayre on 24/10/2016.
 */
public class Moderateur {

    Logger logger = LogManager.getLogger();
    private String[] tabMessages;

    public Moderateur() {}

    // Cette méthode récupère le dernier message est le rajoute à "historique"
    // SI (spam) retourne 1 (si l'user spam)
    // SINON     retourne 0
    public int analyse(Member user, Guild serveur, GuildManager serveurManager, MessageReceivedEvent event){

        String[] toi = new String[5];//Creer tableau pour la copie
        int i = 0; // variable de parcours de "historique"
        int nbMessage = 3;
        int spam = 0;

        /********************************************
         *      si l'USER a deja envoyé un message  *
         ********************************************/
        if(MainBot.historique.containsKey(user))// Si le user a deja posté un message
        {
            /********************************************
             * COPIE des infos d"historique" vers TOI[] *
             ********************************************/
            i = 0;
            while( (MainBot.historique.get(user)[i] != null) && (i < nbMessage) ){
                toi[i+1] = MainBot.historique.get(user)[i];
                i++;
            }
            /********************************************
             * Ajout dernier message recu + dans histo' *
             ********************************************/
            toi[0] = event.getMessage().getContent();// On stocke en position [0] le nouveau message
            MainBot.historique.put(user, toi);// On ajoute dans l'historique TOI

            /*****************************
             * ANALYSE des messages      *
             *****************************/
            if(toi[3].equals(toi[2])&&toi[2].equals(toi[1]) && toi[1].equals(toi[0]) ){

                spam = 1;
               logger.info("Detection de spam pour "+user.getEffectiveName()+"avec 3 messages identique: ");
                for(int j=0;MainBot.historique.get(user).length-1>j;j++)
                {
                    logger.info("\t"+j+". "+MainBot.historique.get(user)[j]);
                }
                toi[0] = "";
                toi[1] = "";
                toi[2] = "";
                toi[3] = "";
                MainBot.historique.put(user, toi);
            }

        } else {
            /********************************************
             * SI c'est le 1er message de l'USER       *
             ********************************************/
            // si le user n'a pas encore posté de message
            // on ajoute le dernier message dans "historique"
            toi[0] = event.getMessage().getContent();
            toi[1] = "";
            toi[2] = "";
            toi[3] = "";
            MainBot.historique.put(user, toi);
        }
        /**********************************
         * AFFICHAGE DE HISTORIQUE        *
         **********************************/




        /********************************************
         *      Comptage du nombre de message       *
         ********************************************/
        if(MainBot.message_compteur.containsKey(user))// Si le user a deja posté un message
        {
            int cpt = MainBot.message_compteur.get(user);
            cpt++;
            //System.out.println("compteur : "+cpt);
            MainBot.message_compteur.put(user, cpt);
            if(cpt > 5){
                MainBot.message_compteur.put(user,0);
                spam = 1;
                logger.info("Detection de spam pour "+user.getEffectiveName()+"avec 5 message en 5seg: ");
                String[] histo = MainBot.historique.get(user);
                for (String unMessage:histo )         //=for(int i=0; i<saveRoleUser.size(); i++)
                {
                   logger.debug("\t*"+unMessage);
                }
            }
        }else{
            MainBot.message_compteur.put(user, 1);
        }

        return spam;
    }
}
