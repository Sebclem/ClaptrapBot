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
import net.Broken.Tools.MessageTimeOut;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AudioM {

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

    private static AudioM INSTANCE;

    public static AudioM getInstance(Guild guild){
        if(INSTANCE == null){
            INSTANCE = new AudioM(guild);
        }

        return INSTANCE;
    }



    private AudioM(Guild guild) {
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        this.guild = guild;
    }

    /**
     * Load audio track from url, connect to chanel if not connected
     * @param event
     * @param voiceChannel Voice channel to connect if no connected
     * @param trackUrl Audio track url
     * @param playlistLimit Limit of playlist
     * @param onHead True for adding audio track on top of playlist
     */
    public void loadAndPlay(MessageReceivedEvent event, VoiceChannel voiceChannel, final String trackUrl, int playlistLimit, boolean onHead) {
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        playedChanel = voiceChannel;

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                logger.info("Single Track detected!");
                UserAudioTrack uat = new UserAudioTrack(event.getAuthor(), track);
                Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Ajout de "+track.getInfo().title+" à la file d'attente!")).complete();
                new MessageTimeOut(MainBot.messageTimeOut, message, event.getMessage()).start();

                play(guild, voiceChannel, musicManager, uat, onHead);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                logger.info("Playlist detected! Limit: "+playlistLimit);
                AudioTrack firstTrack = playlist.getSelectedTrack();

                Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Ajout de "+firstTrack.getInfo().title+" et les 30 premiers titres à la file d'attente!")).complete();
                new MessageTimeOut(MainBot.messageTimeOut, message, event.getMessage()).start();

                playListLoader(playlist, playlistLimit ,event.getAuthor() , onHead);



            }

            @Override
            public void noMatches() {
                logger.warn("Cant find media!");
                Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Musique introuvable!")).complete();
                List<Message> messages = new ArrayList<Message>(){{
                    add(message);
                    add(event.getMessage());
                }};
                new MessageTimeOut(messages, MainBot.messageTimeOut).start();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logger.error("Cant load media!");
                logger.error(exception.getMessage());
                Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Erreur de lecture!")).complete();
                List<Message> messages = new ArrayList<Message>(){{
                    add(message);
                    add(event.getMessage());
                }};
                new MessageTimeOut(messages, MainBot.messageTimeOut).start();
            }
        });
    }

    public void loadAndPlayAuto(String trackUrl){
        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler(){
            @Override
            public void trackLoaded(AudioTrack track) {
                logger.info("Auto add " + track.getInfo().title +" to playlist.");
                UserAudioTrack userAudioTrack = new UserAudioTrack(MainBot.jda.getSelfUser(),track);
                play(guild, playedChanel, musicManager, userAudioTrack, true);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack track = playlist.getTracks().get(0);
                logger.info("Auto add " + track.getInfo().title +" to playlist.");
                UserAudioTrack userAudioTrack = new UserAudioTrack(MainBot.jda.getSelfUser(),track);
                play(guild, playedChanel, musicManager, userAudioTrack, true);
            }

            @Override
            public void noMatches() {
                logger.warn("Track not found: "+trackUrl);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logger.error("Cant load media!");
                logger.error(exception.getMessage());
            }
        });
    }


    /**
     * Load playlist to playlist
     * @param playlist Loaded playlist
     * @param playlistLimit Playlist limit
     * @param user User who have submitted the playlist
     * @param onHead True for adding audio track on top of playlist
     */
    public void playListLoader(AudioPlaylist playlist, int playlistLimit, User user, boolean onHead){
        int i = 0;
        List<AudioTrack> tracks = playlist.getTracks();
        if(onHead)
            Collections.reverse(tracks);
            
        for(AudioTrack track : playlist.getTracks()){
            UserAudioTrack uat = new UserAudioTrack(user, track);
            play(guild, playedChanel, musicManager, uat, onHead);
            i++;
            if((i>=playlistLimit && i!=-1) || i>listExtremLimit)
                break;
        }
    }


    public GuildMusicManager getGuildAudioPlayer(Guild guild) {
        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    /**
     * Add single track to playlist, auto-connect if not connected to vocal chanel
     * @param guild guild
     * @param channel Chanel for auto-connect
     * @param musicManager Guild music manager
     * @param track Track to add to playlist
     * @param onHead True for adding audio track on top of playlist
     */
    public void play(Guild guild, VoiceChannel channel, GuildMusicManager musicManager, UserAudioTrack track,boolean onHead) {
        if(!guild.getAudioManager().isConnected())
            guild.getAudioManager().openAudioConnection(channel);
        if(!onHead)
            musicManager.scheduler.queue(track);
        else
            musicManager.scheduler.addNext(track);
    }

    /**
     * Skip current track
     * @param event
     */
    public void skipTrack(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.nextTrack();
        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Musique suivante!")).complete();
        new MessageTimeOut(MainBot.messageTimeOut, message, event.getMessage()).start();
    }

    /**
     * Pause current track
     * @param event
     */
    public void pause(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.pause();

        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Musique en pause !")).complete();
        new MessageTimeOut(MainBot.messageTimeOut, event.getMessage(), message).start();

    }

    /**
     * Resume paused track
     * @param event
     */
    public void resume (MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.resume();

        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Reprise de la piste en cour !")).complete();
        new MessageTimeOut(MainBot.messageTimeOut, event.getMessage(), message).start();
    }

    /**
     * Print current played track info
     * @param event
     */
    public void info(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        AudioTrackInfo info = musicManager.scheduler.getInfo();
        UserAudioTrack userAudioTrack = musicManager.scheduler.getCurrentPlayingTrack();

        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk(info.title + "\n" + info.uri + "\nSubmitted by: " + userAudioTrack.getSubmittedUser().getName())).complete();
        new MessageTimeOut(MainBot.messageTimeOut, event.getMessage(), message).start();
    }

        public void flush(MessageReceivedEvent event){
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.flush();
        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("RAZ de la playlist!")).complete();
        new MessageTimeOut(MainBot.messageTimeOut, event.getMessage(), message).start();
    }

    /**
     * Print current playlist content
     * @param event
     */
    public void list(MessageReceivedEvent event){
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        List<UserAudioTrackData> list = musicManager.scheduler.getList();
        StringBuilder resp = new StringBuilder();
        if(list.size() == 0){
            resp.append("Oh mon dieux!\nElle est vide! \n:astonished: ");
        }
        else
        {
            for(UserAudioTrackData trackInfo : list){
                resp.append("- ");
                resp.append(trackInfo.getAudioTrackInfo().title);
                resp.append("\n");
            }
        }
        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Playlist:\n\n"+resp.toString())).complete();
        new MessageTimeOut(MainBot.messageTimeOut, event.getMessage(), message).start();
    }

    /**
     * Called by //add, only if already connected
     * @param event
     * @param url Audio track url
     * @param playListLimit Limit of playlist
     * @param onHead True for adding audio track on top of playlist
     */
    public void add(MessageReceivedEvent event, String url, int playListLimit, boolean onHead) {
        if(playedChanel != null){
            loadAndPlay(event ,playedChanel, url, playListLimit,onHead);
        }
        else
        {
            Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Aucune lecture en cour!")).complete();
            new MessageTimeOut(MainBot.messageTimeOut, event.getMessage(), message).start();
        }
    }

    /**
     * Stop current playing track and flush playlist
     * @param event
     */
    public void stop (MessageReceivedEvent event) {
        musicManager.scheduler.stop();
        musicManager.scheduler.flush();

        if (event != null) {
            Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Arret de la musique!")).complete();
            new MessageTimeOut(MainBot.messageTimeOut, event.getMessage(), message).start();
        }
    }

    /**
     * Stop current playing track and flush playlist (no confirmation message)
     */
    public void stop () {

        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        musicManager.scheduler.stop();
        musicManager.scheduler.flush();
        playedChanel = null;
        guild.getAudioManager().closeAudioConnection();
    }

    public GuildMusicManager getGuildMusicManager() throws NullMusicManager, NotConnectedException {
        if( musicManager == null)
            throw new NullMusicManager();
        else if( playedChanel == null)
            throw new NotConnectedException();
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
