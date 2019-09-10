package net.Broken.Tools;

import net.Broken.MainBot;
import net.Broken.Commands.Move;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.managers.GuildManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


/**
 * AntiSpam punishment system
 */
public class AntiSpam {

    Logger logger = LogManager.getLogger();
    public Move move = new Move();
    public AntiSpam() {

    }

    /**
     * Send user to Spam role
     * @param user User to punish
     * @param guild Guild
     * @param guildManager GuildManager
     * @param incrMulti True for increment punishment time
     * @param event Message Received Event
     */
    public void extermine(Member user, Guild guild, GuildManager guildManager, Boolean incrMulti, MessageReceivedEvent event){
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // On créer un nouvelle case dans le tableau des statuts si il n'y est pas
        if(!MainBot.spamUtils.containsKey(user))
        {
            List<Message> messages = new ArrayList<>();
            messages.addAll(MainBot.historique.get(user));
            MainBot.spamUtils.put(user,new UserSpamUtils(user,messages));
        }
        // On verrifie que l'uttilisateur n'est pas deja en spam
        if(!MainBot.spamUtils.get(user).isOnSpam())
        {
            //l'utilisateur n'est pas deja en spam
            if(MainBot.spamUtils.get(user).getMultip() != 0)
            {
                if(MainBot.spamUtils.get(user).getMultip()<45 && incrMulti)
                {
                    MainBot.spamUtils.get(user).setMultip(MainBot.spamUtils.get(user).getMultip()*2);
                }
            }
            else
                MainBot.spamUtils.get(user).setMultip(1);

           logger.info("Starting protocol 66 on "+user.getEffectiveName()+" with a multiplicator of "+MainBot.spamUtils.get(user));

            if(!MainBot.spamUtils.get(user).isOnSpam())
            {
                MainBot.spamUtils.get(user).setOnSpam(true);
                List<Role> spm = guild.getRolesByName("Spammer", false);
                if(spm.size() != 0){
                    try{
                        move.exc(user, spm, true, guild, guildManager);
                        MainBot.spamUtils.get(user).addMessage(event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamExtermine(user,MainBot.spamUtils.get(user.getUser()).getMultip())).complete());
                        MainBot.spamUtils.get(user).setMinuteur(new Minuteur(MainBot.spamUtils.get(user).getMultip(), move.user, move.saveRoleUser, move.serveur, move.serveurManager,event));
                        MainBot.spamUtils.get(user).launchMinuteur();
                    }catch (HierarchyException e){
                        Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getMoveError("You cannot move a "+user.getRoles().get(0).getAsMention())).complete();
                        List<Message> messages = new ArrayList<Message>(){{
                            add(rest);
                            add(event.getMessage());
                        }};
                        new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                        MainBot.spamUtils.get(user).setOnSpam(false);
                    }
                }
                else {
                    MessageEmbed msg = EmbedMessageUtils.buildStandar(EmbedMessageUtils.getError("\nSpam role not found, you need to create it!"));
                    event.getTextChannel().sendMessage(msg).complete();
                }


            }
        }




    }

    /**
     * Timer to auto remove user from Spam role
     */
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
            logger.info("["+user.getEffectiveName()+"] Start for "+multip+"min");
            while (MainBot.spamUtils.get(user).isOnSpam())
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
            logger.info("["+user.getEffectiveName()+"] End of spam for "+user.getEffectiveName()+" after "+multip+"min.");
            try {
                move.exc(user, saveRoleUser, true, serveur, serveurManager);    //aSaveroleUser=saveRoleUser.get(i)
            }catch (HierarchyException e){
                Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getMoveError("You cannot move "+user.getRoles().get(0).getAsMention())).complete();
                List<Message> messages = new ArrayList<Message>(){{
                    add(rest);
                    add(event.getMessage());
                }};
                new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                logger.error("Hierarchy error");
            }
            logger.info("["+user.getEffectiveName()+"] End for "+multip+"min");
            new MessageTimeOut(new ArrayList<>(MainBot.spamUtils.get(user).getMessages()),0).start();
            MainBot.spamUtils.get(user).clearAndAdd(chanel.sendMessage(EmbedMessageUtils.getSpamPardon(user)).complete());
            new MessageTimeOut(MainBot.spamUtils.get(user).getMessages(),60).start();

            //                                                                                                                                                                                        #-----------------------------------------------#


        }
    }





}
