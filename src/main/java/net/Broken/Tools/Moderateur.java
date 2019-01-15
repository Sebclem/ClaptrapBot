
package net.Broken.Tools;

import net.Broken.MainBot;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Auto spam utils TODO Rebuild all this shit!
 */
public class Moderateur {

    Logger logger = LogManager.getLogger();

    public Moderateur() {}

    // Cette méthode récupère le dernier message est le rajoute à "historique"
    // SI (spam) retourne 1 (si l'user spam)
    // SINON     retourne 0

    /**
     * Get last message and add it on history. After analyse for spam detection
     * @param user User
     * @param guild Guild
     * @param guildManager Guild manager
     * @param event Message received event
     * @return 1 if detected as spam, else 0
     */
    public int analyse(Member user, Guild guild, GuildManager guildManager, MessageReceivedEvent event){

        ArrayList<Message> thisUserHistory = new ArrayList<>();//Creer tableau pour la copie
        int i = 0; // variable de parcours de "historique"
        int nbMessage = 3;
        int spam = 0;

        if(MainBot.spamUtils.containsKey(user) && MainBot.spamUtils.get(user).isOnSpam()){
            MainBot.spamUtils.get(user).addMessage(event.getMessage());
        }

        /********************************************
         *      si l'USER a deja envoyé un message  *
         ********************************************/
        if(MainBot.historique.containsKey(user))// Si le user a deja posté un message
        {
            /********************************************
             * COPIE des infos d"historique" vers TOI[] *
             ********************************************/
            thisUserHistory = (ArrayList<Message>) MainBot.historique.get(user).clone();

            /********************************************
             * Ajout dernier message recu + dans histo' *
             ********************************************/
            thisUserHistory.add(0,event.getMessage());
            if(thisUserHistory.size()>nbMessage+1)
                thisUserHistory.remove(4);
            MainBot.historique.put(user, thisUserHistory);// On ajoute dans l'historique TOI

            /*****************************
             * ANALYSE des messages      *
             *****************************/
            int equalCont = 0;
            String oldMessage = "";
            for(Message aMessage : thisUserHistory){
                if(aMessage.getContentRaw().equals(oldMessage))
                    equalCont++;
                oldMessage = aMessage.getContentRaw();

            }
            if(equalCont >= nbMessage){
                spam = 1;
                logger.info("Spam detected for "+user.getEffectiveName()+"with 3 identical messages : ");
                for(Message aMessage : thisUserHistory)
                {
                    logger.info("\t - "+aMessage.getContentRaw());
                }
                MainBot.historique.put(user, new ArrayList<Message>());
            }

        } else {
            /********************************************
             * SI c'est le 1er message de l'USER       *
             ********************************************/
            // si le user n'a pas encore posté de message
            // on ajoute le dernier message dans "historique"
            thisUserHistory.add(0,event.getMessage());

            MainBot.historique.put(user, thisUserHistory);
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
                logger.info("Spam detected for "+user.getEffectiveName()+"with 5 messages in 5 secondes: ");
                ArrayList<Message> histo = MainBot.historique.get(user);
                for (Message aMessage:histo )
                {
//                   logger.debug("\t*"+aMessage.getContent());
                }
            }
        }else{
            MainBot.message_compteur.put(user, 1);
        }

        return spam;
    }
}
