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
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.ArrayList;
import java.util.List;

public class AudioM {
    GuildMusicManager musicManager;
    AudioPlayerManager playerManager;
    VoiceChannel playedChanel;
    int listTimeOut = 30;

    public AudioM() {
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public void loadAndPlay(MessageReceivedEvent event, VoiceChannel voiceChannel, final String trackUrl) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        playedChanel = voiceChannel;

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Ajout de "+track.getInfo().title+" à la file d'attente!")).complete();
                List<Message> messages = new ArrayList<Message>(){{
                    add(message);
                    add(event.getMessage());
                }};
                new MessageTimeOut(messages, MainBot.messageTimeOut).run();

                play(event.getGuild(), voiceChannel, musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Ajout de "+firstTrack.getInfo().title+" et les 30 premiers titres à la file d'attente!")).complete();
                List<Message> messages = new ArrayList<Message>(){{
                    add(message);
                    add(event.getMessage());
                }};
                new MessageTimeOut(messages, MainBot.messageTimeOut).run();
                int i = 0;
                for(AudioTrack track : playlist.getTracks()){
                    play(event.getGuild(), voiceChannel, musicManager, track);
                    i++;
                    if(i>30)
                        break;
                }




            }

            @Override
            public void noMatches() {
                Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Musique introuvable!")).complete();
                List<Message> messages = new ArrayList<Message>(){{
                    add(message);
                    add(event.getMessage());
                }};
                new MessageTimeOut(messages, MainBot.messageTimeOut).run();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Erreur de lecture!")).complete();
                List<Message> messages = new ArrayList<Message>(){{
                    add(message);
                    add(event.getMessage());
                }};
                new MessageTimeOut(messages, MainBot.messageTimeOut).run();
            }
        });
    }




    private GuildMusicManager getGuildAudioPlayer(Guild guild) {
        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    private void play(Guild guild, VoiceChannel channel, GuildMusicManager musicManager, AudioTrack track) {
        guild.getAudioManager().openAudioConnection(channel);

        musicManager.scheduler.queue(track);
    }

    public void skipTrack(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.nextTrack();

        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Musique suivante!")).complete();
        List<Message> messages = new ArrayList<Message>(){{
            add(message);
            add(event.getMessage());
        }};
        new MessageTimeOut(messages, MainBot.messageTimeOut).run();
    }

    public void pause(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.pause();

        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Musique en pause !")).complete();
        List<Message> messages = new ArrayList<Message>(){{
            add(message);
            add(event.getMessage());
        }};
        new MessageTimeOut(messages, MainBot.messageTimeOut).run();
    }

    public void resume (MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.resume();

        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Reprise de la piste en cour !")).complete();
        List<Message> messages = new ArrayList<Message>(){{
            add(message);
            add(event.getMessage());
        }};
        new MessageTimeOut(messages, MainBot.messageTimeOut).run();
    }

    public void info(MessageReceivedEvent event){
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        AudioTrackInfo info = musicManager.scheduler.getInfo();

        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk(info.title+"\n"+info.uri)).complete();
        List<Message> messages = new ArrayList<Message>(){{
            add(message);
            add(event.getMessage());
        }};
        new MessageTimeOut(messages, MainBot.messageTimeOut).run();
    }

    public void flush(MessageReceivedEvent event){
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.flush();
        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("RAZ de la playlist!")).complete();
        List<Message> messages = new ArrayList<Message>(){{
            add(message);
            add(event.getMessage());
        }};
        new MessageTimeOut(messages, MainBot.messageTimeOut).run();
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
        new MessageTimeOut(messages, listTimeOut).run();
    }

    public void add(MessageReceivedEvent event,String url) {
        if(playedChanel != null){
            loadAndPlay(event,playedChanel, url);
        }
        else
        {
            Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Aucune lecture en cour!")).complete();
            List<Message> messages = new ArrayList<Message>(){{
                add(message);
                add(event.getMessage());
            }};
            new MessageTimeOut(messages, MainBot.messageTimeOut).run();
        }
    }

    public void stop (MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.stop();
        playedChanel = null;

        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicOk("Arret de la musique!")).complete();
        List<Message> messages = new ArrayList<Message>(){{
            add(message);
            add(event.getMessage());
        }};
        new MessageTimeOut(messages, MainBot.messageTimeOut).run();
    }



}
