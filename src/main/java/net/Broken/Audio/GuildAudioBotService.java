package net.Broken.Audio;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class GuildAudioBotService {

    private static final HashMap<Guild, GuildAudioBotService> INSTANCES = new HashMap<>();

    private final GuildAudioManager guildAudioManager;

    private final AudioPlayerManager audioPlayerManager;

    /**
     * Extrem limit for playlist
     */
    private static final int LIST_EXTREM_LIMIT = 300;

    private final Guild guild;
    private final Logger logger = LogManager.getLogger();

    private final Map<String, Boolean> addStatus = new HashMap<>();

    private Message lastMessageWithButton;

    private GuildAudioBotService(Guild guild) {
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
        this.guildAudioManager = new GuildAudioManager(audioPlayerManager, guild);
        guild.getAudioManager().setSendingHandler(guildAudioManager.getSendHandler());
        this.guild = guild;
    }

    public static GuildAudioBotService getInstance(Guild guild) {
        INSTANCES.computeIfAbsent(guild, k -> new GuildAudioBotService(guild));
        return INSTANCES.get(guild);
    }

    /**
     * Load audio track from url, connect to chanel if not connected
     *
     * @param voiceChannel  Voice channel to connect if no connected
     * @param trackUrl      Audio track url
     * @param playlistLimit Limit of playlist
     * @param onHead        True for adding audio track on top of playlist
     */
    public void loadAndPlay(SlashCommandInteractionEvent event, AudioChannel voiceChannel, final String trackUrl, int playlistLimit, boolean onHead) {
        audioPlayerManager.loadItemOrdered(guildAudioManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                logger.info("[{}] Single Track detected!", guild.getName());
                UserAudioTrack uat = new UserAudioTrack(event.getUser(), track);
                MessageCreateData message = new MessageCreateBuilder()
                        .setEmbeds(EmbedMessageUtils.getMusicAdded(track.getInfo(), event.getMember(), -1))
                        .build();
                clearLastButton();
                lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();
                play(guild, voiceChannel, guildAudioManager, uat, onHead);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                logger.info("[{}] Playlist detected! Limit: {}", guild, playlistLimit);
                AudioTrack firstTrack = playlist.getSelectedTrack();
                int size = Math.min(playlist.getTracks().size(), playlistLimit);
                MessageCreateData message = new MessageCreateBuilder()
                        .setEmbeds(EmbedMessageUtils.getMusicAdded(firstTrack.getInfo(), event.getMember(), size))
                        .build();
                clearLastButton();
                lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();
                playListLoader(playlist, playlistLimit, event.getUser(), onHead);
            }

            @Override
            public void noMatches() {
                logger.warn("[{}] Cant find media!", guild);
                MessageCreateData message = new MessageCreateBuilder().setEmbeds(EmbedMessageUtils.getMusicError("Video not found !")).build();
                event.getHook().setEphemeral(true).sendMessage(message).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logger.error("[{}] Can't load media!", guild);
                logger.error(exception.getMessage());
                MessageCreateData message = new MessageCreateBuilder().setEmbeds(EmbedMessageUtils.getMusicError("Playback error !")).build();
                event.getHook().setEphemeral(true).sendMessage(message).queue();
            }
        });
    }

    public boolean loadAndPlaySync(String trackUrl, String userId) throws ExecutionException, InterruptedException {
        Member member = guild.getMemberById(userId);
        AudioChannelUnion playedChanel = guild.getAudioManager().getConnectedChannel();
        final String uuid = UUID.randomUUID().toString();
        Future<Void> future = audioPlayerManager.loadItemOrdered(guildAudioManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                logger.info("[{}] Auto add {} to playlist.", guild, track.getInfo().title);
                UserAudioTrack userAudioTrack = new UserAudioTrack(member.getUser(), track);
                play(guild, playedChanel, guildAudioManager, userAudioTrack, true);
                addStatus.put(uuid, true);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack track = playlist.getTracks().get(0);
                logger.info("[{}] Auto add {} to playlist.", guild, track.getInfo().title);
                UserAudioTrack userAudioTrack = new UserAudioTrack(member.getUser(), track);
                play(guild, playedChanel, guildAudioManager, userAudioTrack, true);
                addStatus.put(uuid, true);
            }

            @Override
            public void noMatches() {
                logger.warn("[{}] Track not found: {}", guild, trackUrl);
                addStatus.put(uuid, false);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logger.error("[{}] Cant load media!", guild);
                logger.error(exception.getMessage());
                addStatus.put(uuid, false);
            }
        });
        future.get();
        return addStatus.remove(uuid);
    }


    /**
     * Load playlist to playlist
     *
     * @param playlist      Loaded playlist
     * @param playlistLimit Playlist limit
     * @param user          User who have submitted the playlist
     * @param onHead        True for adding audio track on top of playlist
     */
    public void playListLoader(AudioPlaylist playlist, int playlistLimit, User user, boolean onHead) {

        AudioChannelUnion playedChanel = guild.getAudioManager().getConnectedChannel();
        List<AudioTrack> tracks = playlist.getTracks();
        if (onHead)
            Collections.reverse(tracks);

        int i = 0;
        for (AudioTrack track : playlist.getTracks()) {
            UserAudioTrack uat = new UserAudioTrack(user, track);
            play(guild, playedChanel, guildAudioManager, uat, onHead);
            if ((playlistLimit != -1 && i >= playlistLimit) || i > LIST_EXTREM_LIMIT)
                break;
            i++;
        }
    }


    /**
     * Add single track to playlist, auto-connect if not connected to vocal chanel
     *
     * @param guild        guild
     * @param channel      Chanel for auto-connect
     * @param musicManager Guild music manager
     * @param track        Track to add to playlist
     * @param onHead       True for adding audio track on top of playlist
     */
    public void play(Guild guild, AudioChannel channel, GuildAudioManager musicManager, UserAudioTrack track, boolean onHead) {
        if (!guild.getAudioManager().isConnected())
            guild.getAudioManager().openAudioConnection(channel);
        if (!onHead)
            musicManager.scheduler.queue(track);
        else
            musicManager.scheduler.addNext(track);
    }

    public void add(SlashCommandInteractionEvent event, String url, int playListLimit, boolean onHead) {
        if (guild.getAudioManager().isConnected()) {
            loadAndPlay(event, guild.getAudioManager().getConnectedChannel(), url, playListLimit, onHead);
        } else {
            MessageCreateData message = new MessageCreateBuilder().setEmbeds(EmbedMessageUtils.getMusicError("Not connected to vocal chanel !")).build();
            event.getHook().setEphemeral(true).sendMessage(message).queue();
        }
    }

    public void connect(AudioChannel voiceChannel) {
        guild.getAudioManager().openAudioConnection(voiceChannel);
    }

    public void pause(InteractionHook hook) {
        pause();
        MessageCreateData message = new MessageCreateBuilder().setEmbeds(
                EmbedMessageUtils.buildStandar(
                        new EmbedBuilder()
                                .setTitle(":pause_button:  Playback paused")
                                .setColor(Color.green)
                )).build();
        clearLastButton();
        lastMessageWithButton = hook.sendMessage(message).addActionRow(getActionButton()).complete();
    }

    public void pause() {
        guildAudioManager.scheduler.pause();
    }

    public void resume(InteractionHook hook) {
        MessageCreateData message;
        if (guildAudioManager.player.getPlayingTrack() == null) {
            message = new MessageCreateBuilder().setEmbeds(
                    EmbedMessageUtils.buildStandar(
                            new EmbedBuilder()
                                    .setTitle(":warning:  Nothing to play, playlist is empty !")
                                    .setColor(Color.green)
                    )).build();
        } else {
            resume();
            message = new MessageCreateBuilder().setEmbeds(
                    EmbedMessageUtils.buildStandar(
                            new EmbedBuilder()
                                    .setTitle(":arrow_forward:  Playback resumed")
                                    .setColor(Color.green)
                    )).build();
        }
        clearLastButton();
        lastMessageWithButton = hook.sendMessage(message).addActionRow(getActionButton()).complete();
    }

    public void resume() {
        guildAudioManager.scheduler.resume();
    }

    public void skipTrack(InteractionHook hook) {
        skipTrack();
        MessageCreateData message = new MessageCreateBuilder().setEmbeds(
                EmbedMessageUtils.buildStandar(
                        new EmbedBuilder()
                                .setTitle(":track_next:  Next Track")
                                .setColor(Color.green)
                )).build();
        clearLastButton();
        lastMessageWithButton = hook.sendMessage(message).addActionRow(getActionButton()).complete();
    }

    public void skipTrack() {
        guildAudioManager.scheduler.nextTrack();
    }

    public void stop(InteractionHook hook) {
        stop();
        MessageCreateData message = new MessageCreateBuilder().setEmbeds(
                EmbedMessageUtils.buildStandar(
                        new EmbedBuilder()
                                .setTitle(":stop_button:  Playback stopped")
                                .setColor(Color.green)
                )).build();
        clearLastButton();
        lastMessageWithButton = hook.sendMessage(message).addActionRow(getActionButton()).complete();

    }

    public void stop() {
        guildAudioManager.scheduler.stop();
        guildAudioManager.scheduler.flush();
        clearLastButton();
    }

    public void disconnect(InteractionHook hook) {
        disconnect();
        MessageCreateData message = new MessageCreateBuilder().setEmbeds(
                EmbedMessageUtils.buildStandar(
                        new EmbedBuilder()
                                .setTitle(":eject:  Disconnected")
                                .setColor(Color.green)
                )).build();
        clearLastButton();
        hook.sendMessage(message).queue();
    }

    public void disconnect() {
        guildAudioManager.scheduler.stop();
        guildAudioManager.scheduler.flush();
        guild.getAudioManager().closeAudioConnection();
        clearLastButton();
    }
    public void info(InteractionHook hook) {
        AudioTrackInfo info = guildAudioManager.scheduler.getInfo();
        UserAudioTrack userAudioTrack = guildAudioManager.scheduler.getCurrentPlayingTrack();
        MessageCreateData message = new MessageCreateBuilder().setEmbeds(EmbedMessageUtils.getMusicInfo(info, userAudioTrack)).build();
        clearLastButton();
        lastMessageWithButton = hook.sendMessage(message).addActionRow(getActionButton()).complete();
    }

    public void flush(InteractionHook hook) {
        guildAudioManager.scheduler.flush();
        MessageCreateData message = new MessageCreateBuilder().setEmbeds(
                EmbedMessageUtils.buildStandar(
                        new EmbedBuilder()
                                .setTitle(":wastebasket:  Playlist flushed")
                                .setColor(Color.green)
                )).build();
        clearLastButton();
        lastMessageWithButton = hook.sendMessage(message).addActionRow(getActionButton()).complete();
    }

    /**
     * Print current playlist content
     *
     * @param event
     */
    public void list(InteractionHook hook) {
        List<UserAudioTrack> list = guildAudioManager.scheduler.getList();

        if (list.isEmpty()) {
            MessageCreateData message = new MessageCreateBuilder().setEmbeds(
                    EmbedMessageUtils.buildStandar(
                            new EmbedBuilder()
                                    .setTitle(":scroll:  Playlist")
                                    .setColor(Color.green)
                                    .setDescription("Oh no ! The playlist is empty !")
                    )).build();
           hook.sendMessage(message).queue();
        } else {
            StringBuilder resp = new StringBuilder();
            int i = 0;
            for (UserAudioTrack trackInfo : list) {
                resp.append(":arrow_right:  ");
                resp.append(trackInfo.getAudioTrack().getInfo().title);
                resp.append(" - ");
                resp.append(trackInfo.getAudioTrack().getInfo().author);
                resp.append("\n\n");
                if (i >= 5) {
                    resp.append(":arrow_forward: And ");
                    resp.append(list.size() - 5);
                    resp.append(" other tracks ...");
                    break;
                }
                i++;
            }
            MessageCreateData message = new MessageCreateBuilder().setEmbeds(
                    EmbedMessageUtils.buildStandar(
                            new EmbedBuilder()
                                    .setTitle(":scroll:  Playlist")
                                    .setColor(Color.green)
                                    .setDescription(resp.toString())
                    )).build();
            hook.sendMessage(message).queue();
        }

    }

    public Guild getGuild() {
        return guild;
    }

    public GuildAudioManager getGuidAudioManager() {
        return guildAudioManager;
    }


    public void clearLastButton() {
        if (lastMessageWithButton != null) {
            this.lastMessageWithButton.editMessageComponents(new ArrayList<>()).queue();
            this.lastMessageWithButton = null;
        }

    }

    public void updateLastButton() {
        if (lastMessageWithButton != null)
            lastMessageWithButton = lastMessageWithButton.editMessageComponents(ActionRow.of(getActionButton())).complete();
    }


    private List<Button> getActionButton() {
        ArrayList<Button> buttonArrayList = new ArrayList<>();
        if (guildAudioManager.player.getPlayingTrack() == null) {
            buttonArrayList.add(Button.success("play", Emoji.fromUnicode("▶️")).withDisabled(true));
            buttonArrayList.add(Button.primary("next", Emoji.fromUnicode("⏭️")).withDisabled(true));
            buttonArrayList.add(Button.primary("stop", Emoji.fromUnicode("⏹️")).withDisabled(true));
            buttonArrayList.add(Button.danger("disconnect", Emoji.fromUnicode("⏏️")));
            return buttonArrayList;
        }
        if (guildAudioManager.player.isPaused()) {
            buttonArrayList.add(Button.success("play", Emoji.fromUnicode("▶️")));
        } else {
            buttonArrayList.add(Button.success("pause", Emoji.fromUnicode("⏸️")));
        }
        buttonArrayList.add(Button.primary("next", Emoji.fromUnicode("⏭️")));
        buttonArrayList.add(Button.primary("stop", Emoji.fromUnicode("⏹️")));
        buttonArrayList.add(Button.danger("disconnect", Emoji.fromUnicode("⏏️")));
        return buttonArrayList;
    }
}
