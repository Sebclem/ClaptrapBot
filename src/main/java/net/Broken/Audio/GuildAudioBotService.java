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
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class GuildAudioBotService {

    private static final HashMap<Guild, GuildAudioBotService> INSTANCES = new HashMap<>();

    private final GuildAudioManager guildAudioManager;

    private final AudioPlayerManager audioPlayerManager;

    /**
     * Extrem limit for playlist
     */
    private final int listExtremLimit = 300;

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
        if (!INSTANCES.containsKey(guild)) {
            INSTANCES.put(guild, new GuildAudioBotService(guild));
        }
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
    public void loadAndPlay(SlashCommandEvent event, VoiceChannel voiceChannel, final String trackUrl, int playlistLimit, boolean onHead) {
        audioPlayerManager.loadItemOrdered(guildAudioManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                logger.info("[" + guild + "] Single Track detected!");
                UserAudioTrack uat = new UserAudioTrack(event.getUser(), track);
                Message message = new MessageBuilder()
                        .setEmbeds(EmbedMessageUtils.getMusicAdded(track.getInfo(), event.getMember(), -1))
                        .build();
                clearLastButton();
                lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();
                play(guild, voiceChannel, guildAudioManager, uat, onHead);
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

    public boolean loadAndPlaySync(String trackUrl, String userId) throws ExecutionException, InterruptedException {
        Member member = guild.getMemberById(userId);
        VoiceChannel playedChanel = guild.getAudioManager().getConnectedChannel();
        final String uuid = UUID.randomUUID().toString();
        Future<Void> future = audioPlayerManager.loadItemOrdered(guildAudioManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                logger.info("[" + guild + "] Auto add " + track.getInfo().title + " to playlist.");
                UserAudioTrack userAudioTrack = new UserAudioTrack(member.getUser(), track);
                play(guild, playedChanel, guildAudioManager, userAudioTrack, true);
                addStatus.put(uuid, true);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack track = playlist.getTracks().get(0);
                logger.info("[" + guild + "] Auto add " + track.getInfo().title + " to playlist.");
                UserAudioTrack userAudioTrack = new UserAudioTrack(member.getUser(), track);
                play(guild, playedChanel, guildAudioManager, userAudioTrack, true);
                addStatus.put(uuid, true);
            }

            @Override
            public void noMatches() {
                logger.warn("[" + guild + "] Track not found: " + trackUrl);
                addStatus.put(uuid, false);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logger.error("[" + guild + "] Cant load media!");
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

    public void add(SlashCommandEvent event, String url, int playListLimit, boolean onHead) {
        if (guild.getAudioManager().isConnected()) {
            loadAndPlay(event, guild.getAudioManager().getConnectedChannel(), url, playListLimit, onHead);
        } else {
            Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getMusicError("Not connected to vocal chanel !")).build();
            event.getHook().setEphemeral(true).sendMessage(message).queue();
        }
    }

    public void connect(VoiceChannel voiceChannel) {
        guild.getAudioManager().openAudioConnection(voiceChannel);
    }

    public void pause(GenericInteractionCreateEvent event) {
        pause();
        Message message = new MessageBuilder().setEmbeds(
                EmbedMessageUtils.buildStandar(
                        new EmbedBuilder()
                                .setTitle(":pause_button:  Playback paused")
                                .setColor(Color.green)
                )).build();
        clearLastButton();
        lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();
    }

    public void pause() {
        guildAudioManager.scheduler.pause();
    }

    public void resume(GenericInteractionCreateEvent event) {
        Message message;
        if (guildAudioManager.player.getPlayingTrack() == null) {
            message = new MessageBuilder().setEmbeds(
                    EmbedMessageUtils.buildStandar(
                            new EmbedBuilder()
                                    .setTitle(":warning:  Nothing to play, playlist is empty !")
                                    .setColor(Color.green)
                    )).build();
        } else {
            resume();
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

    public void resume() {
        guildAudioManager.scheduler.resume();
    }

    public void skipTrack(GenericInteractionCreateEvent event) {
        skipTrack();
        Message message = new MessageBuilder().setEmbeds(
                EmbedMessageUtils.buildStandar(
                        new EmbedBuilder()
                                .setTitle(":track_next:  Next Track")
                                .setColor(Color.green)
                )).build();
        clearLastButton();
        lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();
    }

    public void skipTrack() {
        guildAudioManager.scheduler.nextTrack();
    }

    public void stop(GenericInteractionCreateEvent event) {
        stop();
        Message message = new MessageBuilder().setEmbeds(
                EmbedMessageUtils.buildStandar(
                        new EmbedBuilder()
                                .setTitle(":stop_button:  Playback stopped")
                                .setColor(Color.green)
                )).build();
        clearLastButton();
        lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();

    }

    public void stop() {
        guildAudioManager.scheduler.stop();
        guildAudioManager.scheduler.flush();
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
        guildAudioManager.scheduler.stop();
        guildAudioManager.scheduler.flush();
        guild.getAudioManager().closeAudioConnection();
        clearLastButton();
    }
    public void info(GenericInteractionCreateEvent event) {
        AudioTrackInfo info = guildAudioManager.scheduler.getInfo();
        UserAudioTrack userAudioTrack = guildAudioManager.scheduler.getCurrentPlayingTrack();
        Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getMusicInfo(info, userAudioTrack)).build();
        clearLastButton();
        lastMessageWithButton = event.getHook().sendMessage(message).addActionRow(getActionButton()).complete();
    }

    public void flush(GenericInteractionCreateEvent event) {
        guildAudioManager.scheduler.flush();
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
        List<UserAudioTrack> list = guildAudioManager.scheduler.getList();

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
