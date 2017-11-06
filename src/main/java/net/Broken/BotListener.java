package net.Broken;

import net.Broken.Commandes.Move;
import net.Broken.Commandes.Music;
import net.Broken.Commandes.Spam;
import net.Broken.Outils.AntiSpam;
import net.Broken.Outils.MessageTimeOut;
import net.Broken.Outils.Moderateur;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ExceptionEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.security.tools.keytool.Main;


/**
 * Created by seb65 on 19/10/2016.
 */

public class BotListener extends ListenerAdapter {
    AntiSpam antispam=new AntiSpam();
    Moderateur modo = new Moderateur();
    Logger logger = LogManager.getLogger();


    @Override
    public void onReady(ReadyEvent event) {
        logger.info("Connection succees");
    }


    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        logger.info(event.getUser().getName()+ "join the guild, move it!");
        new Move().exc(event.getMember(),event.getJDA().getRolesByName("Newbies",true),false,event.getJDA().getGuilds().get(0),event.getJDA().getGuilds().get(0).getManager());
        TextChannel chanel = event.getGuild().getTextChannelsByName("accueil", true).get(0);
        chanel.sendMessage("Salut "+event.getUser().getAsMention()+"! Ecris ton nom, prénom, ta promotion et ton groupe ici! Un admin te donnera accées a ton groupe!").complete();
        MainBot.roleFlag = false;
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        logger.debug(event.getUser().getName()+" leave a role");
        if(!MainBot.roleFlag){

            if(event.getMember().getRoles().size() == 0){
                logger.info(event.getUser().getName()+ "have no roles, move it!");
                new Move().exc(event.getMember(),event.getJDA().getRolesByName("Rat_d'égout",true),false,event.getJDA().getGuilds().get(0),event.getJDA().getGuilds().get(0).getManager());
                MainBot.roleFlag = false;
            }
        }
        else
        {
            logger.debug("ignore it");
            MainBot.roleFlag = false;
        }

    }



    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        super.onGuildVoiceLeave(event);
        if(event.getGuild().getAudioManager().isConnected())
        {
            logger.debug("User disconnected from voice channel.");

            if(event.getGuild().getAudioManager().getConnectedChannel().getMembers().size() == 1){
                logger.debug("I'm alone, close audio connection.");

                Music music = (Music) MainBot.commandes.get("music");
                music.audio.stop(event);
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //                                                      ----------------------Test pour eviter eco de commande-------------------------


        if (event.getMessage().getContent().startsWith("//") && !event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            //On a detecter que c'etait une commande
            //System.out.println(event.getMessage().getContent());
            MainBot.handleCommand(MainBot.parser.parse(event.getMessage().getContent(), event));
        }
        else if (!event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId()))
        {

            if(!event.isFromType(ChannelType.PRIVATE)) {
                if (!event.getTextChannel().getName().equals("le_dongeon")) {
                    Guild serveur = event.getGuild();
                    GuildManager guildManager = serveur.getManager();
                    Member user = event.getMember();

                    // appel de la methode d'analyse de message de "Moderateur"
                    if (!event.getAuthor().getName().equals("Aethex") && event.getMessage().getContent().length() > 0) {

                        if (modo.analyse(user, serveur, guildManager, event) == 1) {
                            antispam.extermine(user, serveur, guildManager, true, event);
                        }
                    } else if (event.getMessage().getContent().length() == 0)
                        logger.error("Image detected, ignoring it.");

                }
            }





        }

    }
}
