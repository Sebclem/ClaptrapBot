package net.Broken.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.UserAudioTrackData;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AudioM {

    private static HashMap<Guild, AudioM> INSTANCES = new HashMap<>();
    /**
     * Music manager for this guild
     */
    private GuildMusicManager musicManager;
    /**
     * Audio player manager for this guild
     */
    private AudioPlayerManager playerManager;
    /**
     * Current voice chanel (null if not connected)
     */
    private VoiceChannel playedChanel;
    /**
     * Time out for list message
     */
    private int listTimeOut = 30;
    /**
     * Extrem limit for playlist
     */
    private int listExtremLimit = 300;
    /**
     * Current guild
     */
    private Guild guild;
    private Logger logger = LogManager.getLogger();

    private AudioM(Guild guild) {
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        this.guild = guild;
    }

    public static AudioM getInstance(Guild guild) {
        if (!INSTANCES.containsKey(guild)) {
            INSTANCES.put(guild, new AudioM(guild));
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
        GuildMusicManager musicManager = getGuildAudioPlayer();
        playedChanel = voiceChannel;

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                logger.info("[" + guild + "] Single Track detected!");
                UserAudioTrack uat = new UserAudioTrack(event.getUser(), track);
                Message message = new MessageBuilder()
                        .setEmbeds(EmbedMessageUtils.getMusicAdded(track.getInfo(), event.getMember(), -1))
                        .build();
                event.getHook().sendMessage(message).queue();
                play(guild, voiceChannel, musicManager, uat, onHead);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                logger.info("[" + guild + "] Playlist detected! Limit: " + playlistLimit);
                AudioTrack firstTrack = playlist.getSelectedTrack();
                int size = Math.min(playlist.getTracks().size(), playlistLimit);
                Message message = new MessageBuilder()
                        .setEmbeds(EmbedMessageUtils.getMusicAdded(firstTrack.getInfo(), event.getMember(), size))
                        .build();
                event.getHook().sendMessage(message).queue();
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
        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                logger.info("[" + guild + "] Auto add " + track.getInfo().title + " to playlist.");
                UserAudioTrack userAudioTrack = new UserAudioTrack(MainBot.jda.getSelfUser(), track);
                play(guild, playedChanel, musicManager, userAudioTrack, true);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack track = playlist.getTracks().get(0);
                logger.info("[" + guild + "] Auto add " + track.getInfo().title + " to playlist.");
                UserAudioTrack userAudioTrack = new UserAudioTrack(MainBot.jda.getSelfUser(), track);
                play(guild, playedChanel, musicManager, userAudioTrack, true);
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
        int i = 0;
        List<AudioTrack> tracks = playlist.getTracks();
        if (onHead)
            Collections.reverse(tracks);

        for (AudioTrack track : playlist.getTracks()) {
            UserAudioTrack uat = new UserAudioTrack(user, track);
            play(guild, playedChanel, musicManager, uat, onHead);
            i++;
            if ((i >= playlistLimit && i != -1) || i > listExtremLimit)
                break;
        }
    }


    public GuildMusicManager getGuildAudioPlayer() {
        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager, guild);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
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
    public void play(Guild guild, VoiceChannel channel, GuildMusicManager musicManager, UserAudioTrack track, boolean onHead) {
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
    public void skipTrack(SlashCommandEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer();
        musicManager.scheduler.nextTrack();
        Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getMusicOk("Next music !")).build();
        event.getHook().sendMessage(message).queue();
    }

    /**
     * Pause current track
     *
     * @param event
     */
    public void pause(SlashCommandEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer();
        musicManager.scheduler.pause();
        Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getMusicOk("Playback paused")).build();
        event.getHook().sendMessage(message).queue();


    }

    /**
     * Resume paused track
     *
     * @param event
     */
    public void resume(SlashCommandEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer();
        musicManager.scheduler.resume();
        Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getMusicOk("Playback resumed")).build();
        event.getHook().sendMessage(message).queue();
    }

    /**
     * Print current played track info
     *
     * @param event
     */
    public void info(SlashCommandEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer();
        AudioTrackInfo info = musicManager.scheduler.getInfo();
        UserAudioTrack userAudioTrack = musicManager.scheduler.getCurrentPlayingTrack();
        Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getMusicInfo(info, userAudioTrack)).build();
        event.getHook().sendMessage(message).queue();
    }

    public void flush(SlashCommandEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer();
        musicManager.scheduler.flush();
        Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getMusicOk("Flush playlist!")).build();
        event.getHook().sendMessage(message).queue();
    }

    /**
     * Print current playlist content
     *
     * @param event
     */
    public void list(SlashCommandEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer();
        List<UserAudioTrackData> list = musicManager.scheduler.getList();
        StringBuilder resp = new StringBuilder();
        if (list.size() == 0) {
            resp.append("Oh my god!\nThe playlist is empty ! \n:astonished: ");
        } else {
            for (UserAudioTrackData trackInfo : list) {
                resp.append("- ");
                resp.append(trackInfo.getAudioTrackInfo().title);
                resp.append("\n");
            }
        }
        Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getMusicOk("Playlist:\n\n" + resp.toString())).build();
        event.getHook().sendMessage(message).queue();
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
        if (playedChanel != null) {
            loadAndPlay(event, playedChanel, url, playListLimit, onHead);
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
    public void stop(SlashCommandEvent event) {
        musicManager.scheduler.stop();
        musicManager.scheduler.flush();

        if (event != null) {
            Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getMusicOk("Music stopped")).build();
            event.getHook().sendMessage(message).queue();
        }
    }

    /**
     * Stop current playing track and flush playlist (no confirmation message)
     */
    public void stop() {

        GuildMusicManager musicManager = getGuildAudioPlayer();
        musicManager.scheduler.stop();
        musicManager.scheduler.flush();
        playedChanel = null;
        guild.getAudioManager().closeAudioConnection();
    }

    public GuildMusicManager getGuildMusicManager() {
        if (musicManager == null)
            musicManager = getGuildAudioPlayer();
        return musicManager;

    }

    public Guild getGuild() {
        return guild;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public VoiceChannel getPlayedChanel() {
        return playedChanel;
    }

    public void setPlayedChanel(VoiceChannel playedChanel) {
        this.playedChanel = playedChanel;
    }
}
