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
import net.Broken.Outils.EmbedMessageUtils;
import net.Broken.Outils.MessageTimeOut;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AudioM {
    private GuildMusicManager musicManager;
    private AudioPlayerManager playerManager;
    private VoiceChannel playedChanel;
    private int listTimeOut = 30;
    private int listExtremLimit = 300;
    private Logger logger = LogManager.getLogger();
    private Guild guild;



    public AudioM(Guild guild) {
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        this.guild = guild;
    }

    public void loadAndPlay(MessageReceivedEvent event, VoiceChannel voiceChannel, final String trackUrl, int playlistLimit, boolean onHead) {
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        playedChanel = voiceChannel;

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                logger.info("Single Track detected!");

                Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Ajout de "+track.getInfo().title+" à la file d'attente!")).complete();
                List<Message> messages = new ArrayList<Message>(){{
                    add(message);
                    add(event.getMessage());
                }};
                new MessageTimeOut(messages, MainBot.messageTimeOut).start();

                play(guild, voiceChannel, musicManager, track, onHead);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                logger.info("Playlist detected! Limit: "+playlistLimit);
                AudioTrack firstTrack = playlist.getSelectedTrack();

                Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Ajout de "+firstTrack.getInfo().title+" et les 30 premiers titres à la file d'attente!")).complete();
                List<Message> messages = new ArrayList<Message>(){{
                    add(message);
                    add(event.getMessage());
                }};
                new MessageTimeOut(messages, MainBot.messageTimeOut).start();

                playListLoader(playlist, playlistLimit, onHead);



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

    public void playListLoader(AudioPlaylist playlist,int playlistLimit, boolean onHead){
        int i = 0;
        for(AudioTrack track : playlist.getTracks()){
            play(guild, playedChanel, musicManager, track, onHead);
            i++;
            if((i>=playlistLimit && i!=-1) || i>listExtremLimit)
                break;
        }
    }


    private GuildMusicManager getGuildAudioPlayer(Guild guild) {
        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void play(Guild guild, VoiceChannel channel, GuildMusicManager musicManager, AudioTrack track,boolean onHead) {
        if(!guild.getAudioManager().isConnected())
            guild.getAudioManager().openAudioConnection(channel);
        if(!onHead)
            musicManager.scheduler.queue(track);
        else
            musicManager.scheduler.addNext(track);
    }

    public void skipTrack(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.nextTrack();

        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Musique suivante!")).complete();
        List<Message> messages = new ArrayList<Message>(){{
            add(message);
            add(event.getMessage());
        }};
        new MessageTimeOut(messages, MainBot.messageTimeOut).start();
    }

    public void pause(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.pause();

        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Musique en pause !")).complete();
        List<Message> messages = new ArrayList<Message>(){{
            add(message);
            add(event.getMessage());
        }};
        new MessageTimeOut(messages, MainBot.messageTimeOut).start();
    }

    public void resume (MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.resume();

        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Reprise de la piste en cour !")).complete();
        List<Message> messages = new ArrayList<Message>(){{
            add(message);
            add(event.getMessage());
        }};
        new MessageTimeOut(messages, MainBot.messageTimeOut).start();
    }

    public void info(MessageReceivedEvent event){
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        AudioTrackInfo info = musicManager.scheduler.getInfo();

        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk(info.title+"\n"+info.uri)).complete();
        List<Message> messages = new ArrayList<Message>(){{
            add(message);
            add(event.getMessage());
        }};
        new MessageTimeOut(messages, MainBot.messageTimeOut).start();
    }

    public void flush(MessageReceivedEvent event){
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.flush();
        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("RAZ de la playlist!")).complete();
        List<Message> messages = new ArrayList<Message>(){{
            add(message);
            add(event.getMessage());
        }};
        new MessageTimeOut(messages, MainBot.messageTimeOut).start();
    }

    public void list(MessageReceivedEvent event){
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        List<AudioTrackInfo> list = musicManager.scheduler.getList();
        StringBuilder resp = new StringBuilder();
        if(list.size() == 0){
            resp.append("Oh mon dieux!\nElle est vide! \n:astonished: ");
        }
        else
        {
            for(AudioTrackInfo trackInfo : list){
                resp.append("- ");
                resp.append(trackInfo.title);
                resp.append("\n");
            }
        }
        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Playlist:\n\n"+resp.toString())).complete();
        List<Message> messages = new ArrayList<Message>(){{
            add(message);
            add(event.getMessage());
        }};
        new MessageTimeOut(messages, listTimeOut).start();
    }


    public void add(MessageReceivedEvent event,String url, int playListLimit, boolean onHead) {
        if(playedChanel != null){
            loadAndPlay(event,playedChanel, url, playListLimit,onHead);
        }
        else
        {
            Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Aucune lecture en cour!")).complete();
            List<Message> messages = new ArrayList<Message>(){{
                add(message);
                add(event.getMessage());
            }};
            new MessageTimeOut(messages, MainBot.messageTimeOut).start();
        }
    }


    public void stop (MessageReceivedEvent event) {
        musicManager.scheduler.stop();
        musicManager.scheduler.flush();

        if (event != null) {
            Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Arret de la musique!")).complete();
            List<Message> messages = new ArrayList<Message>(){{
                add(message);
                add(event.getMessage());
            }};
            new MessageTimeOut(messages, MainBot.messageTimeOut).start();
        }
    }

    public void stop (GuildVoiceLeaveEvent event) {

        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.stop();
        playedChanel = null;
        event.getGuild().getAudioManager().closeAudioConnection();
    }

    public GuildMusicManager getMusicManager() throws NullMusicManager, NotConectedException {
        if( musicManager == null)
            throw new NullMusicManager();
        else if( playedChanel == null)
            throw new NotConectedException();
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
}
