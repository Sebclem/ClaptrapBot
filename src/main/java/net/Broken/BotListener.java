package net.Broken;

import net.Broken.Audio.GuildAudioBotService;
import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.Tools.AutoVoiceChannel;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.UserManager.Stats.UserStatsUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

import java.awt.*;
import java.util.HashMap;
import java.util.Optional;

/**
 * Bot Listener
 */
public class BotListener extends ListenerAdapter {
    private final GuildPreferenceRepository guildPreferenceRepository;
    private final BotConfigLoader botConfig;

    private final Logger logger = LogManager.getLogger();

    public BotListener() {
        ApplicationContext context = SpringContext.getAppContext();
        guildPreferenceRepository = context.getBean(GuildPreferenceRepository.class);
        botConfig = context.getBean(BotConfigLoader.class);
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("Connection success");
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        GuildPreferenceEntity guildPref = getPreference(event.getGuild());
        if (guildPref.isDefaultRole()) {
            logger.info("[{}] : {} join the guild, adding default role !", event.getGuild().getName(),
                    event.getUser().getName());
            Role default_role = event.getGuild().getRoleById(guildPref.getDefaultRoleId());
            if (default_role != null) {
                event.getGuild().addRoleToMember(event.getMember(), default_role).queue();
            } else {
                logger.fatal("[{}] : Default role is null !", event.getGuild().getName());
            }
        }
        if (guildPref.isWelcome()) {
            TextChannel chanel = event.getGuild().getTextChannelById(guildPref.getWelcomeChanelID());
            if (chanel != null) {
                String message = guildPref.getWelcomeMessage().replace("@name", event.getMember().getAsMention());
                logger.debug(message);
                chanel.sendMessage(message).queue();
            } else {
                logger.fatal("[{}] : Welcome chanel is null !", event.getGuild().getName());
            }
        }

    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        GuildPreferenceEntity guildPref = getPreference(event.getGuild());
        if (guildPref.isDefaultRole() && event.getMember().getRoles().isEmpty()) {
            logger.info("[{}] : {} have no roles, reset to default !", event.getGuild().getName(),
                    event.getUser().getName());
            Role defaultRole = event.getGuild().getRoleById(guildPref.getDefaultRoleId());
            if (defaultRole == null) {
                logger.fatal("[{}] : Default role is null !", event.getGuild().getName());
                return;
            }
            event.getGuild().addRoleToMember(event.getMember(), defaultRole).queue();
        }

    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        super.onGuildVoiceUpdate(event);
        // Disconnect
        if (event.getChannelJoined() == null) {
            if (event.getGuild().getAudioManager().isConnected()) {
                logger.trace("User disconnected from voice channel.");

                if (event.getGuild().getAudioManager().getConnectedChannel().getMembers().size() == 1) {
                    logger.debug("I'm alone, close audio connection.");
                    GuildAudioBotService.getInstance(event.getGuild()).stop();
                }
            } else if (event.getMember().getUser() == MainBot.jda.getSelfUser()) {
                GuildAudioBotService.getInstance(event.getGuild()).clearLastButton();
            }
            AutoVoiceChannel autoVoiceChannel = AutoVoiceChannel.getInstance(event.getGuild());
            autoVoiceChannel.leave(event.getChannelLeft());
        }
        // Connect
        else if (event.getChannelLeft() == null && !event.getMember().getUser().isBot()) {
            UserStatsUtils userStatsUtils = UserStatsUtils.getINSTANCE();
            if (!userStatsUtils.runningCounters.containsKey(event.getMember().getId())) {
                UserStatsUtils.VoicePresenceCounter temp = new UserStatsUtils.VoicePresenceCounter(
                        event.getMember());
                temp.start();
                userStatsUtils.runningCounters.put(event.getMember().getId(), temp);
            }
            AutoVoiceChannel autoVoiceChannel = AutoVoiceChannel.getInstance(event.getGuild());
            autoVoiceChannel.join(event.getChannelJoined());
        }
        // Move
        else {
            AutoVoiceChannel autoVoiceChannel = AutoVoiceChannel.getInstance(event.getGuild());
            autoVoiceChannel.leave(event.getChannelLeft());
            autoVoiceChannel.join(event.getChannelJoined());
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            UserStatsUtils.getINSTANCE().addMessageCount(event.getMember());
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        super.onButtonInteraction(event);
        event.deferReply().queue();
        GuildAudioBotService guildAudioBotService = GuildAudioBotService.getInstance(event.getGuild());
        switch (event.getComponentId()) {
            case "pause" -> guildAudioBotService.pause(event);
            case "play" -> guildAudioBotService.resume(event);
            case "next" -> guildAudioBotService.skipTrack(event);
            case "stop" -> guildAudioBotService.stop(event);
            case "disconnect" -> guildAudioBotService.disconnect(event);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        HashMap<String, SlashCommand> commands = MainBot.slashCommands;
        if (commands.containsKey(event.getName())) {
            SlashCommand commandRunner = commands.get(event.getName());
            // It's form private message
            if (!event.isFromGuild()) {
                if (commandRunner.isPrivateUsable()) {
                    commandRunner.action(event);
                } else {
                    MessageEmbed message = EmbedMessageUtils.buildStandar(new EmbedBuilder()
                            .setTitle(":no_entry_sign:  This command is not usable in private message")
                            .setColor(Color.red));
                    event.replyEmbeds(message).setEphemeral(true).queue();
                }
            } else {
                if (!commandRunner.isNSFW() || event.getChannel().asTextChannel().isNSFW()) {
                    commandRunner.action(event);
                } else {
                    MessageEmbed message = EmbedMessageUtils.buildStandar(new EmbedBuilder()
                            .setTitle(":underage:  This command is only usable in NSFW channels")
                            .setColor(Color.red));
                    event.replyEmbeds(message).setEphemeral(true).queue();
                }
            }
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        logger.info("Join new guild! ({} {} Members)", event.getGuild().getName(),
                event.getGuild().getMembers().size());
        super.onGuildJoin(event);
        getPreference(event.getGuild());
        event.getGuild().loadMembers()
                .onSuccess((members -> logger.debug("[{}] Members loaded", event.getGuild().getName())));
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.GREEN)
                .setTitle("Hello there !")
                .setDescription(
                        "Allow me to introduce myself -- I am a CL4P-TP the discord bot, but my friends call me Claptrap ! Or they would, if any of them were real...\n"
                                +
                                "\nYou can access to my web UI with: " + botConfig.url())
                .setImage("https://i.imgur.com/Anf1Srg.gif");
        MessageCreateData message = new MessageCreateBuilder().setEmbeds(EmbedMessageUtils.buildStandar(eb)).build();

        TextChannel defaultChan = event.getGuild().getDefaultChannel().asTextChannel();
        if (defaultChan.canTalk()) {
            defaultChan.sendMessage(message).queue();
        } else {
            for (TextChannel chan : event.getGuild().getTextChannels()) {
                if (chan.canTalk()) {
                    chan.sendMessage(message).queue();
                    break;
                }
            }
        }
    }

    private GuildPreferenceEntity getPreference(Guild guild) {
        Optional<GuildPreferenceEntity> guildPref = guildPreferenceRepository.findByGuildId(guild.getId());
        if (guildPref.isEmpty()) {
            logger.info("[{}] : Generate default pref", guild.getName());
            return guildPreferenceRepository.save(GuildPreferenceEntity.getDefault(guild.getId()));
        } else {
            return guildPref.get();
        }
    }
}
