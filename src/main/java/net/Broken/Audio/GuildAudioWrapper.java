package net.Broken.Audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.Broken.MainBot;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GuildAudioWrapper {

    private static final HashMap<Guild, GuildAudioWrapper> INSTANCES = new HashMap<>();

    private final GuildAudioManager guildAudioManager;

    private final AudioPlayerManager audioPlayerManager;

    /**
     * Extrem limit for playlist
     */
    private final int listExtremLimit = 300;

    private final Guild guild;
    private final Logger logger = LogManager.getLogger();

    private Message lastMessageWithButton;

    private GuildAudioWrapper(Guild guild) {
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
        this.guildAudioManager = new GuildAudioManager(audioPlayerManager, guild);
        guild.getAudioManager().setSendingHandler(guildAudioManager.getSendHandler());
        this.guild = guild;
    }

    public static GuildAudioWrapper getInstance(Guild guild) {
        if (!INSTANCES.containsKey(guild)) {
            INSTANCES.put(guild, new GuildAudioWrapper(guild));
        }
        return INSTANCES.get(guild);
    }

    /**
     * Load audio track from url, connect to chanel if not connected
     *
     * @param event
     * @param voiceChannel  Voice channel to connect if no connected
     * @param trackUrl      Audio track url
     * @param playlistLimit Limit of playlist
     * @param onHead        True for adding audio track on top of playlist
     */
    public void loadAndPlay(SlashCommandEvent event, VoiceChannel voiceChannel, final String trackUrl, int playlistLimit, boolean onHead) {
        GuildAudioManager guidAudioManager = getGuidAudioManager();

        audioPlayerManager.loadItemOrdered(guidAudioManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                logger.info("[" + guild + "] Single Track detected!");
                UserAudioTrack uat = new UserAudioTrack(event.getUser(), track);
                Message message = new MessageBuilder()
                        .setEmbeds(EmbedMessageUtils.getMusicAdded(track.getInfo(), event.getMember(), -1))
                        .build();
                clearLastButton();
                lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();
                play(guild, voiceChannel, guidAudioManager, uat, onHead);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                logger.info("[" + guild + "] Playlist detected! Limit: " + playlistLimit);
                AudioTrack firstTrack = playlist.getSelectedTrack();
                int size = Math.min(playlist.getTracks().size(), playlistLimit);
                Message message = new MessageBuilder()
                        .setEmbeds(EmbedMessageUtils.getMusicAdded(firstTrack.getInfo(), event.getMember(), size))
                        .build();
                clearLastButton();
                lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();
                playListLoader(playlist, playlistLimit, event.getUser(), onHead);
            }

            @Override
            public void noMatches() {
                logger.warn("[" + guild + "] Cant find media!");
                Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getMusicError("Video not found !")).build();
                event.getHook().setEphemeral(true).sendMessage(message).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logger.error("[" + guild + "] Can't load media!");
                logger.error(exception.getMessage());
                Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getMusicError("Playback error !")).build();
                event.getHook().setEphemeral(true).sendMessage(message).queue();
            }
        });
    }

    public void loadAndPlayAuto(String trackUrl) {
        VoiceChannel playedChanel = guild.getAudioManager().getConnectedChannel();
        audioPlayerManager.loadItemOrdered(guildAudioManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                logger.info("[" + guild + "] Auto add " + track.getInfo().title + " to playlist.");
                UserAudioTrack userAudioTrack = new UserAudioTrack(MainBot.jda.getSelfUser(), track);
                play(guild, playedChanel, guildAudioManager, userAudioTrack, true);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack track = playlist.getTracks().get(0);
                logger.info("[" + guild + "] Auto add " + track.getInfo().title + " to playlist.");
                UserAudioTrack userAudioTrack = new UserAudioTrack(MainBot.jda.getSelfUser(), track);
                play(guild, playedChanel, guildAudioManager, userAudioTrack, true);
            }

            @Override
            public void noMatches() {
                logger.warn("[" + guild + "] Track not found: " + trackUrl);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logger.error("[" + guild + "] Cant load media!");
                logger.error(exception.getMessage());
            }
        });
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

        VoiceChannel playedChanel = guild.getAudioManager().getConnectedChannel();
        List<AudioTrack> tracks = playlist.getTracks();
        if (onHead)
            Collections.reverse(tracks);

        int i = 0;
        for (AudioTrack track : playlist.getTracks()) {
            UserAudioTrack uat = new UserAudioTrack(user, track);
            play(guild, playedChanel, guildAudioManager, uat, onHead);
            if ((playlistLimit != -1 && i >= playlistLimit) || i > listExtremLimit)
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
    public void play(Guild guild, VoiceChannel channel, GuildAudioManager musicManager, UserAudioTrack track, boolean onHead) {
        if (!guild.getAudioManager().isConnected())
            guild.getAudioManager().openAudioConnection(channel);
        if (!onHead)
            musicManager.scheduler.queue(track);
        else
            musicManager.scheduler.addNext(track);
    }

    /**
     * Skip current track
     *
     * @param event
     */
    public void skipTrack(GenericInteractionCreateEvent event) {
        GuildAudioManager musicManager = getGuidAudioManager();
        musicManager.scheduler.nextTrack();
        Message message = new MessageBuilder().setEmbeds(
                EmbedMessageUtils.buildStandar(
                        new EmbedBuilder()
                                .setTitle(":track_next:  Next Track")
                                .setColor(Color.green)
                )).build();
        clearLastButton();
        lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();
    }

    /**
     * Pause current track
     *
     * @param event
     */
    public void pause(GenericInteractionCreateEvent event) {
        GuildAudioManager musicManager = getGuidAudioManager();
        musicManager.scheduler.pause();
        Message message = new MessageBuilder().setEmbeds(
                EmbedMessageUtils.buildStandar(
                        new EmbedBuilder()
                                .setTitle(":pause_button:  Playback paused")
                                .setColor(Color.green)
                )).build();
        clearLastButton();
        lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();


    }

    /**
     * Resume paused track
     *
     * @param event
     */
    public void resume(GenericInteractionCreateEvent event) {
        GuildAudioManager musicManager = getGuidAudioManager();
        Message message;
        if (musicManager.player.getPlayingTrack() == null) {
            message = new MessageBuilder().setEmbeds(
                    EmbedMessageUtils.buildStandar(
                            new EmbedBuilder()
                                    .setTitle(":warning:  Nothing to play, playlist is empty !")
                                    .setColor(Color.green)
                    )).build();
        } else {
            musicManager.scheduler.resume();
            message = new MessageBuilder().setEmbeds(
                    EmbedMessageUtils.buildStandar(
                            new EmbedBuilder()
                                    .setTitle(":arrow_forward:  Playback resumed")
                                    .setColor(Color.green)
                    )).build();
        }
        clearLastButton();
        lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();
    }

    /**
     * Print current played track info
     *
     * @param event
     */
    public void info(GenericInteractionCreateEvent event) {
        GuildAudioManager musicManager = getGuidAudioManager();
        AudioTrackInfo info = musicManager.scheduler.getInfo();
        UserAudioTrack userAudioTrack = musicManager.scheduler.getCurrentPlayingTrack();
        Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getMusicInfo(info, userAudioTrack)).build();
        clearLastButton();
        lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();
    }

    public void flush(GenericInteractionCreateEvent event) {
        GuildAudioManager musicManager = getGuidAudioManager();
        musicManager.scheduler.flush();
        Message message = new MessageBuilder().setEmbeds(
                EmbedMessageUtils.buildStandar(
                        new EmbedBuilder()
                                .setTitle(":wastebasket:  Playlist flushed")
                                .setColor(Color.green)
                )).build();
        clearLastButton();
        lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();
    }

    /**
     * Print current playlist content
     *
     * @param event
     */
    public void list(GenericInteractionCreateEvent event) {
        GuildAudioManager musicManager = getGuidAudioManager();
        List<UserAudioTrack> list = musicManager.scheduler.getList();

        if (list.size() == 0) {
            Message message = new MessageBuilder().setEmbeds(
                    EmbedMessageUtils.buildStandar(
                            new EmbedBuilder()
                                    .setTitle(":scroll:  Playlist")
                                    .setColor(Color.green)
                                    .setDescription("Oh no ! The playlist is empty !")
                    )).build();
            event.getHook().sendMessage(message).queue();
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
            Message message = new MessageBuilder().setEmbeds(
                    EmbedMessageUtils.buildStandar(
                            new EmbedBuilder()
                                    .setTitle(":scroll:  Playlist")
                                    .setColor(Color.green)
                                    .setDescription(resp.toString())
                    )).build();
            event.getHook().sendMessage(message).queue();
        }

    }

    /**
     * Called by //add, only if already connected
     *
     * @param event
     * @param url           Audio track url
     * @param playListLimit Limit of playlist
     * @param onHead        True for adding audio track on top of playlist
     */
    public void add(SlashCommandEvent event, String url, int playListLimit, boolean onHead) {
        if (guild.getAudioManager().isConnected()) {
            loadAndPlay(event, guild.getAudioManager().getConnectedChannel(), url, playListLimit, onHead);
        } else {
            Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getMusicError("Not connected to vocal chanel !")).build();
            event.getHook().setEphemeral(true).sendMessage(message).queue();
        }
    }

    /**
     * Stop current playing track and flush playlist
     *
     * @param event
     */
    public void stop(GenericInteractionCreateEvent event) {
        guildAudioManager.scheduler.stop();
        guildAudioManager.scheduler.flush();

        if (event != null) {
            Message message = new MessageBuilder().setEmbeds(
                    EmbedMessageUtils.buildStandar(
                            new EmbedBuilder()
                                    .setTitle(":stop_button:  Playback stopped")
                                    .setColor(Color.green)
                    )).build();
            clearLastButton();
            lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();
        }
    }

    public void stop() {

        GuildAudioManager musicManager = getGuidAudioManager();
        musicManager.scheduler.stop();
        musicManager.scheduler.flush();
        clearLastButton();
    }

    public void disconnect(GenericInteractionCreateEvent event) {
        disconnect();
        Message message = new MessageBuilder().setEmbeds(
                EmbedMessageUtils.buildStandar(
                        new EmbedBuilder()
                                .setTitle(":eject:  Disconnected")
                                .setColor(Color.green)
                )).build();
        clearLastButton();
        event.getHook().sendMessage(message).queue();
    }

    public void disconnect() {
        GuildAudioManager musicManager = getGuidAudioManager();
        musicManager.scheduler.stop();
        musicManager.scheduler.flush();
        guild.getAudioManager().closeAudioConnection();
        clearLastButton();
    }

    public Guild getGuild() {
        return guild;
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
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
