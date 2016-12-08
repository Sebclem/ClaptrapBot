package net.borken.Outils;

import net.borken.MainBot;
import net.borken.commandes.Move;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.managers.GuildManager;


import java.util.List;

import static java.lang.Thread.sleep;


/**
 * Created by seb65 on 20/10/2016.
 */
public class AntiSpam {
    public Move move = new Move();

    public Entete entete=new Entete();

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
        if(!MainBot.minuteurStatut.containsKey(user))
        {
            MainBot.minuteurStatut.put(user,false);
        }
        // On verrifie que l'uttilisateur n'est pas deja en spam
        if(!MainBot.minuteurStatut.get(user))
        {
            //l'utilisateur n'est pas deja en spam
            if(MainBot.userMulti.containsKey(user))
            {
                if(MainBot.userMulti.get(user)<45 && incrMulti)
                {
                    MainBot.userMulti.replace(user,MainBot.userMulti.get(user)*2);
                }
            }
            else
                MainBot.userMulti.put(user,1);

            System.out.println();
            System.out.println(entete.get("Info","ANTISPAM")+"Punition de "+user.getEffectiveName()+" avec un multiplicateur de "+MainBot.userMulti.get(user));

            event.getTextChannel().sendMessage(user.getAsMention()+"\n```markdown\n#-----------------SPAM DETECTEUR----------------#\n#                                               #\n#    La prochaine fois tu fermeras ta gueule!   #\n#                                               #\n#       On te revoit dans "+MainBot.userMulti.get(user)+"min connard !        #\n#                                               #\n#-----------------------------------------------#```\n https://cdn.meme.am/instances/64726692.jpg ").queue();

            if(!MainBot.minuteurStatut.get(user))
            {
                MainBot.minuteurStatut.replace(user,true);
                List<Role> spm = serveur.getRolesByName("Spammer", false);
                move.exc(user, spm.get(0), true, serveur, serveurManger);
                Thread minuteur =new Minuteur(MainBot.userMulti.get(user), move.user, move.saveRoleUser, move.serveur, move.serveurManager,event);
                minuteur.start();
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


        public Minuteur(int multip, Member user, List<Role> saveRoleUser, Guild serveur, GuildManager serveurManager, MessageReceivedEvent event )
        {
            this.multip=multip;
            this.user=user;
            this.saveRoleUser=saveRoleUser;
            this.serveur=serveur;
            this.serveurManager=serveurManager;
            this.event=event;
            this.chanel=event.getTextChannel();
        }


        @Override
        public void run() {
            int cSeg=0;
            System.out.println();
            System.out.println(entete.get("Info","MINUTEUR")+"["+user.getEffectiveName()+"] Démarage pour "+multip+"min");
            while (MainBot.minuteurStatut.get(user))
            {
                try {
                sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(cSeg>60*multip)
                {
                    MainBot.minuteurStatut.replace(user,false);
                }
                cSeg++;
            }
            System.out.println();
            System.out.println(entete.get("Info","MINUTEUR")+ "["+user.getEffectiveName()+"] Fin de spam pour "+user.getEffectiveName()+" apres "+multip+"min.");
            move.exc(user, saveRoleUser.get(0), true, serveur, serveurManager);    //aSaveroleUser=saveRoleUser.get(i)
            System.out.println();
            System.out.println(entete.get("Info","MINUTEUR")+"["+user.getEffectiveName()+"] Fin des "+multip+"min");
            chanel.sendMessage(user.getAsMention()+"\n```markdown\n#-----------------SPAM DETECTEUR----------------#\n#                                               #\n#     Un spammeur est de retour, fais gaffe!    #\n#               Je te surveille!                #\n#                                               #\n#-----------------------------------------------#```\n http://67.media.tumblr.com/tumblr_lvrf58vBkL1qibz0jo1_r1_500.png").queue();


            //                                                                                                                                                                                        #-----------------------------------------------#


        }
    }



}
