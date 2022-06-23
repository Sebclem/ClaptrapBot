package net.Broken.Api.Services;

import net.Broken.Api.Data.Guild.Channel;
import net.Broken.Api.Data.Music.Connect;
import net.Broken.Api.Data.Music.PlayBackInfo;
import net.Broken.Api.Data.Music.Status;
import net.Broken.Api.Data.Music.TrackInfo;
import net.Broken.Audio.GuildAudioBotService;
import net.Broken.Audio.UserAudioTrack;
import net.Broken.MainBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AudioService {

    final Logger logger = LogManager.getLogger();

    public Status getGuildAudioStatus(String guildId, String userId) {
        Guild guild = MainBot.jda.getGuildById(guildId);
        Member member = guild.getMemberById(userId);


        VoiceChannel channel = guild.getAudioManager().getConnectedChannel();
        ConnectionStatus status = guild.getAudioManager().getConnectionStatus();
        if (channel != null) {
//              The user can view the audio status if:
//                  -> He can view the voice channel
//                  -> OR He can *not* view the voice channel, but he is connected to this voice channel
            boolean canView = member.hasPermission(channel, Permission.VIEW_CHANNEL)
                    || (member.getVoiceState() != null
                    && member.getVoiceState().getChannel() == channel);
            GuildAudioBotService guildAudioBotService = GuildAudioBotService.getInstance(guild);

            if (canView) {
//                  The user can interact with the audio if:
//                      -> He can connect to this voice channel
//                          -> OR he is connected to this voice channel
//                      -> AND He can speak in this voice channel
                boolean canInteract = (member.hasPermission(channel, Permission.VOICE_CONNECT)
                        || member.getVoiceState() != null
                        && member.getVoiceState().getChannel() == channel)
                        && member.hasPermission(channel, Permission.VOICE_SPEAK);


                boolean stopped = guildAudioBotService.getGuidAudioManager().player.getPlayingTrack() == null;
                PlayBackInfo playBackInfo;
                if (!stopped) {
                    boolean paused = guildAudioBotService.getGuidAudioManager().player.isPaused();
                    long position = guildAudioBotService.getGuidAudioManager().player.getPlayingTrack().getPosition();
                    UserAudioTrack userAudioTrack = guildAudioBotService.getGuidAudioManager().scheduler.getCurrentPlayingTrack();

                    playBackInfo = new PlayBackInfo(paused, false, position, new TrackInfo(userAudioTrack));

                } else {
                    playBackInfo = new PlayBackInfo(false, true, null, null);
                }
                Channel channelApi = new Channel(channel.getId(), channel.getName());
                return new Status(true, status, channelApi, true, canInteract, playBackInfo);

            } else {
                return new Status(true, status, null, false, false, null);
            }
        }
        return new Status(false, status, null, null, null, null);
    }

    public ResponseEntity<Status> connect(String guildId, Connect body, String userId) {
        Guild guild = MainBot.jda.getGuildById(guildId);
        VoiceChannel voiceChannel = guild.getVoiceChannelById(body.channelId());
        GuildAudioBotService.getInstance(guild).connect(voiceChannel);
        Status status = getGuildAudioStatus(guildId, userId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    public ResponseEntity<Status> disconnect(String guildId, String userId) {
        Guild guild = MainBot.jda.getGuildById(guildId);
        GuildAudioBotService guildAudioBotService = GuildAudioBotService.getInstance(guild);
        guildAudioBotService.disconnect();
        Status status = getGuildAudioStatus(guildId, userId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }


    public ResponseEntity<Status> pause(String guildId, String userId){
        Guild guild = MainBot.jda.getGuildById(guildId);
        GuildAudioBotService.getInstance(guild).pause();
        Status status = getGuildAudioStatus(guildId, userId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    public ResponseEntity<Status> resume(String guildId, String userId){
        Guild guild = MainBot.jda.getGuildById(guildId);
        GuildAudioBotService.getInstance(guild).resume();
        Status status = getGuildAudioStatus(guildId, userId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    public ResponseEntity<Status> skip(String guildId, String userId){
        Guild guild = MainBot.jda.getGuildById(guildId);
        GuildAudioBotService.getInstance(guild).skipTrack();
        Status status = getGuildAudioStatus(guildId, userId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    public ResponseEntity<Status> stop(String guildId, String userId){
        Guild guild = MainBot.jda.getGuildById(guildId);
        GuildAudioBotService.getInstance(guild).stop();
        Status status = getGuildAudioStatus(guildId, userId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
