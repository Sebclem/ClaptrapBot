package net.Broken;

import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.Tools.AutoVoiceChannel;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.UserManager.Stats.UserStatsUtils;
import net.Broken.audio.AudioM;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Bot Listener
 */
public class BotListener extends ListenerAdapter {
    private final GuildPreferenceRepository guildPreferenceRepository;

    private final Logger logger = LogManager.getLogger();

    public BotListener() {
        ApplicationContext context = SpringContext.getAppContext();
        guildPreferenceRepository = (GuildPreferenceRepository) context.getBean("guildPreferenceRepository");
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("Connection success");
    }


    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        GuildPreferenceEntity guildPref = getPreference(event.getGuild());
        if (guildPref.isDefaultRole()) {
            logger.info("[" + event.getGuild().getName() + "] : " + event.getUser().getName() + " join the guild, adding default role !");
            Role default_role = event.getGuild().getRoleById(guildPref.getDefaultRoleId());
            if (default_role != null) {
                event.getGuild().addRoleToMember(event.getMember(), default_role).queue();
            } else {
                logger.fatal("[" + event.getGuild().getName() + "] : Default role is null !");
            }
        }
        if (guildPref.isWelcome()) {
            TextChannel chanel = event.getGuild().getTextChannelById(guildPref.getWelcomeChanelID());
            if (chanel != null) {
                String message = guildPref.getWelcomeMessage().replaceAll("@name", event.getMember().getAsMention());
                logger.debug(message);
                chanel.sendMessage(message).queue();
            }else {
                logger.fatal("[" + event.getGuild().getName() + "] : Welcome chanel is null !");
            }
        }

    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        GuildPreferenceEntity guildPref = getPreference(event.getGuild());
        if (guildPref.isDefaultRole()) {
            if (event.getMember().getRoles().size() == 0) {
                logger.info("[" + event.getGuild().getName() + "] : " + event.getUser().getName() + " have no roles, reset to default !");
                Role default_role = event.getGuild().getRoleById(guildPref.getDefaultRoleId());
                if (default_role == null) {
                    logger.fatal("[" + event.getGuild().getName() + "] : Default role is null !");
                    return;
                }
                event.getGuild().addRoleToMember(event.getMember(), default_role).queue();
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
        else if (event.getMember().getUser() == MainBot.jda.getSelfUser()){
            AudioM.getInstance(event.getGuild()).clearLastButton();
        }
        AutoVoiceChannel autoVoiceChannel = AutoVoiceChannel.getInstance(event.getGuild());
        autoVoiceChannel.leave(event.getChannelLeft());
    }

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        super.onGuildVoiceMove(event);
        AutoVoiceChannel autoVoiceChannel = AutoVoiceChannel.getInstance(event.getGuild());
        autoVoiceChannel.leave(event.getChannelLeft());
        autoVoiceChannel.join(event.getChannelJoined());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            UserStatsUtils.getINSTANCE().addMessageCount(event.getMember());
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        super.onButtonClick(event);
        event.deferReply().queue();
        AudioM audioM = AudioM.getInstance(event.getGuild());
        switch (event.getComponentId()) {
            case "pause" -> audioM.pause(event);
            case "play" -> audioM.resume(event);
            case "next" -> audioM.skipTrack(event);
            case "stop" -> audioM.stop(event);
            case "disconnect" -> audioM.disconect(event);
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        HashMap<String, SlashCommand> commands = MainBot.slashCommands;
        super.onSlashCommand(event);
        if (commands.containsKey(event.getName())) {
            commands.get(event.getName()).action(event);
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
        Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.buildStandar(eb)).build();

        TextChannel defaultChan = event.getGuild().getDefaultChannel();
        if (defaultChan != null && defaultChan.canTalk()) {
            defaultChan.sendMessage(message).queue();
        } else {
            for (TextChannel chan : event.getGuild().getTextChannels()) {
                if (chan.canTalk()) {
                    chan.sendMessage(message).queue();
                }
            }
        }
    }

    private GuildPreferenceEntity getPreference(Guild guild) {
        List<GuildPreferenceEntity> guildPrefList = guildPreferenceRepository.findByGuildId(guild.getId());
        GuildPreferenceEntity guildPref;
        if (guildPrefList.isEmpty()) {
            logger.info("[" + guild.getName() + "] : Generate default pref");
            guildPref = GuildPreferenceEntity.getDefault(guild);
            guildPref = guildPreferenceRepository.save(guildPref);
        } else
            guildPref = guildPrefList.get(0);
        return guildPref;
    }



}
