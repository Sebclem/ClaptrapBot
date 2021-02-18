package net.Broken;

import net.Broken.Commands.Move;
import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.Tools.*;
import net.Broken.Tools.Command.CommandParser;
import net.Broken.Tools.UserManager.Stats.UserStatsUtils;
import net.Broken.audio.AudioM;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.GuildManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Bot Listener
 */
public class BotListener extends ListenerAdapter {
    private AntiSpam antispam = new AntiSpam();
    private Moderateur modo = new Moderateur();

    private GuildPreferenceRepository guildPreferenceRepository;
    private UserRepository userRepository;

    private Logger logger = LogManager.getLogger();

    public BotListener() {

        ApplicationContext context = SpringContext.getAppContext();
        guildPreferenceRepository = (GuildPreferenceRepository) context.getBean("guildPreferenceRepository");
        userRepository = (UserRepository) context.getBean("userRepository");

    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("Connection succees");
    }


    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        GuildPreferenceEntity guildPref = getPreference(event.getGuild());

        if (guildPref.isDefaultRole()) {

            logger.info(event.getUser().getName() + "join the guild, move it!");

            List<Role> roles = new ArrayList<>();
            roles.add(event.getGuild().getRoleById(guildPref.getDefaultRoleId()));


            new Move().exc(event.getMember(), roles, false, event.getGuild(), event.getGuild().getManager());
        }


        if (guildPref.isWelcome()) {

            TextChannel chanel = event.getGuild().getTextChannelById(guildPref.getWelcomeChanelID());
            if (chanel != null) {
                String message = guildPref.getWelcomeMessage();
                message = message.replaceAll("@name", event.getMember().getAsMention());
                logger.debug(message);
                chanel.sendMessage(message).complete();
            }

            MainBot.roleFlag = false;
        }


    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {

        GuildPreferenceEntity guildPref = getPreference(event.getGuild());
        if (guildPref.isDefaultRole()) {

            if (!MainBot.roleFlag) {

                if (event.getMember().getRoles().size() == 0) {

                    logger.info(event.getUser().getName() + "have no roles, move it!");
                    List<Role> roles = new ArrayList<>();
                    roles.add(event.getGuild().getRoleById(guildPref.getDefaultRoleId()));


                    new Move().exc(event.getMember(), roles, false, event.getGuild(), event.getGuild().getManager());
                    MainBot.roleFlag = false;
                }
            } else {
                logger.debug("ignore it");
                MainBot.roleFlag = false;
            }

        }


    }


    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        super.onGuildVoiceJoin(event);
        if (!event.getMember().getUser().isBot()) {
            UserStatsUtils userStatsUtils = UserStatsUtils.getINSTANCE();
            if (!userStatsUtils.runningCounters.containsKey(event.getMember().getId())) {
                UserStatsUtils.VoicePresenceCounter temp = new UserStatsUtils.VoicePresenceCounter(event.getMember());
                temp.start();
                userStatsUtils.runningCounters.put(event.getMember().getId(), temp);

            }
            AutoVoiceChannel autoVoiceChannel = AutoVoiceChannel.getInstance(event.getGuild());
            autoVoiceChannel.join(event.getChannelJoined());
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        super.onGuildVoiceLeave(event);
        if (event.getGuild().getAudioManager().isConnected()) {
            logger.trace("User disconnected from voice channel.");

            if (event.getGuild().getAudioManager().getConnectedChannel().getMembers().size() == 1) {
                logger.debug("I'm alone, close audio connection.");
                AudioM.getInstance(event.getGuild()).stop();
            }
        }
        AutoVoiceChannel autoVoiceChannel = AutoVoiceChannel.getInstance(event.getGuild());
        autoVoiceChannel.leave(event.getChannelLeft());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (!event.getAuthor().isBot()) {
            UserStatsUtils.getINSTANCE().addMessageCount(event.getMember());
        }
        try {
            if (event.getMessage().getContentRaw().startsWith("//") && !event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
                //On a detecter que c'etait une commande
                //System.out.println(event.getMessage().getContent());
                List<UserEntity> users = userRepository.findByJdaId(event.getAuthor().getId());
                UserEntity user = users.size() == 0 ? null : users.get(0);
                MainBot.handleCommand(new CommandParser().parse(event.getMessage().getContentRaw(), event), user);

            } else if (!event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {

                if (!event.isFromType(ChannelType.PRIVATE)) {


                    Guild serveur = event.getGuild();
                    GuildPreferenceEntity guildPref = getPreference(serveur);

                    if (!guildPref.isAntiSpam())
                        return;
                    try {
                        GuildManager guildManager = serveur.getManager();
                        Member user = event.getMember();

                        // appel de la methode d'analyse de message de "Moderateur"
                        if (event.getMessage().getContentRaw().length() > 0) {

                            if (modo.analyse(user, serveur, guildManager, event) == 1) {
                                antispam.extermine(user, serveur, guildManager, true, event);
                            }
                        } else if (event.getMessage().getContentRaw().length() == 0)
                            logger.error("Image detected, ignoring it.");

                    } catch (InsufficientPermissionException e) {
                        logger.warn("Insufficient permission for guild " + e.getGuild(MainBot.jda).getName() + " Missing " + e.getPermission() + " permission.");
                    }

                }


            }
        } catch (Exception e) {
            logger.catching(e);

            if (event.isFromType(ChannelType.PRIVATE))
                PrivateMessage.send(event.getAuthor(), EmbedMessageUtils.getInternalError(), logger);
            else
                event.getTextChannel().sendMessage(EmbedMessageUtils.getInternalError()).queue();
        }


    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        logger.info("Join new guild! (" + event.getGuild().getName() + " " + event.getGuild().getMembers().size() + " Members)");
        super.onGuildJoin(event);
        getPreference(event.getGuild());
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.GREEN)
                .setTitle("Hello there !")
                .setDescription("Allow me to introduce myself -- I am a CL4P-TP the discord bot, but my friends call me Claptrap ! Or they would, if any of them were real...\n" +
                        "\nYou can access to my web UI with: " + MainBot.url)
                .setImage("https://i.imgur.com/Anf1Srg.gif");

        TextChannel defaultChan = event.getGuild().getDefaultChannel();
        if (defaultChan != null && defaultChan.canTalk())
            defaultChan.sendMessage(EmbedMessageUtils.buildStandar(eb)).queue();
        else {
            for(TextChannel chan : event.getGuild().getTextChannels()){
                if(chan.canTalk()){
                    chan.sendMessage(EmbedMessageUtils.buildStandar(eb)).queue();
                }
            }
        }
    }

    private GuildPreferenceEntity getPreference(Guild guild) {
        List<GuildPreferenceEntity> guildPrefList = guildPreferenceRepository.findByGuildId(guild.getId());
        GuildPreferenceEntity guildPref;
        if (guildPrefList.isEmpty()) {
            logger.info("Generate default pref");
            guildPref = GuildPreferenceEntity.getDefault(guild);
            guildPreferenceRepository.save(guildPref);
        } else
            guildPref = guildPrefList.get(0);
        return guildPref;
    }

}
