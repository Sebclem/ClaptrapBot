package net.Broken.Outils;

import net.Broken.MainBot;
import net.Broken.Commandes.Move;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.List;

import static java.lang.Thread.sleep;


/**
 * Created by seb65 on 20/10/2016.
 */
public class AntiSpam {
    public Move move = new Move();
    Logger logger = LogManager.getLogger();

    public AntiSpam() {




        //Constructeur

    }

    public void extermine(Member user, Guild serveur, GuildManager serveurManger, Boolean incrMulti, MessageReceivedEvent event){
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // On créer un nouvelle case dans le tableau des statuts si il n'y est pas
        if(!MainBot.spamUtils.containsKey(user.getUser()))
        {
            MainBot.spamUtils.put(user.getUser(),new UserSpamUtils(user));
        }
        // On verrifie que l'uttilisateur n'est pas deja en spam
        if(!MainBot.spamUtils.get(user.getUser()).isOnSpam())
        {
            //l'utilisateur n'est pas deja en spam
            if(MainBot.spamUtils.get(user.getUser()).getMultip() != 0)
            {
                if(MainBot.spamUtils.get(user.getUser()).getMultip()<45 && incrMulti)
                {
                    MainBot.spamUtils.get(user.getUser()).setMultip(MainBot.spamUtils.get(user.getUser()).getMultip()*2);
                }
            }
            else
                MainBot.spamUtils.get(user.getUser()).setMultip(1);

           logger.info("Punition de "+user.getEffectiveName()+" avec un multiplicateur de "+MainBot.spamUtils.get(user.getUser()));

            event.getTextChannel().sendMessage(user.getAsMention()+"\n```markdown\n#-----------------SPAM DETECTEUR----------------#\n#                                               #\n#    La prochaine fois tu fermeras ta gueule!   #\n#                                               #\n#            On te revoit dans "+MainBot.spamUtils.get(user.getUser()).getMultip()+"min             #\n#                                               #\n#-----------------------------------------------#```").queue();

            if(!MainBot.spamUtils.get(user.getUser()).isOnSpam())
            {
                MainBot.spamUtils.get(user.getUser()).setOnSpam(true);
                List<Role> spm = serveur.getRolesByName("Spammer", false);
                move.exc(user, spm.get(0), true, serveur, serveurManger);
                MainBot.spamUtils.get(user.getUser()).setMinuteur(new Minuteur(MainBot.spamUtils.get(user.getUser()).getMultip(), move.user, move.saveRoleUser, move.serveur, move.serveurManager,event));
                MainBot.spamUtils.get(user.getUser()).launchMinuteur();
            }
        }




    }

    public class Minuteur extends Thread{
        public TextChannel chanel;
        public List<Role> saveRoleUser;
        public Member user;
        public Guild serveur;
        public GuildManager serveurManager;
        public int multip;
        public MessageReceivedEvent event;
        public int timeLeft;


        public Minuteur(int multip, Member user, List<Role> saveRoleUser, Guild serveur, GuildManager serveurManager, MessageReceivedEvent event )
        {
            this.multip=multip;
            this.user=user;
            this.saveRoleUser=saveRoleUser;
            this.serveur=serveur;
            this.serveurManager=serveurManager;
            this.event=event;
            this.chanel=event.getTextChannel();
            this.timeLeft = 60*multip;
        }


        @Override
        public void run() {
            logger.info("["+user.getEffectiveName()+"] Démarage pour "+multip+"min");
            while (MainBot.spamUtils.get(user.getUser()).isOnSpam())
            {
                try {
                sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(timeLeft<=0)
                {
                    MainBot.spamUtils.get(user.getUser()).setOnSpam(false);
                }
                timeLeft--;
            }
            logger.info("["+user.getEffectiveName()+"] Fin de spam pour "+user.getEffectiveName()+" apres "+multip+"min.");
            move.exc(user, saveRoleUser.get(0), true, serveur, serveurManager);    //aSaveroleUser=saveRoleUser.get(i)
            logger.info("["+user.getEffectiveName()+"] Fin des "+multip+"min");
            chanel.sendMessage(user.getAsMention()+"\n```markdown\n#-----------------SPAM DETECTEUR----------------#\n#                                               #\n#     Un spammeur est de retour, fais gaffe!    #\n#               Je te surveille!                #\n#                                               #\n#-----------------------------------------------#```").queue();


            //                                                                                                                                                                                        #-----------------------------------------------#


        }
    }



}
