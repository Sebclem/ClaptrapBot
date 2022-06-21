package net.Broken.Api.Services;

import net.Broken.Api.Data.Guild.Channel;
import net.Broken.Api.Data.Music.PlayBackInfo;
import net.Broken.Api.Data.Music.Status;
import net.Broken.Api.Data.Music.TrackInfo;
import net.Broken.MainBot;
import net.Broken.audio.AudioM;
import net.Broken.audio.UserAudioTrack;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.springframework.stereotype.Service;

@Service
public class AudioService {
    public Status getGuildAudioStatus(String guildId, String userId) {
        Guild guild = MainBot.jda.getGuildById(guildId);
        Member member = guild.getMemberById(userId);
        GuildVoiceState voiceState = guild.getSelfMember().getVoiceState();

        if (voiceState != null) {
            VoiceChannel channel = voiceState.getChannel();
            if (channel != null) {
//              The user can view the audio status if:
//                  -> He can view the voice channel
//                  -> OR He can *not* view the voice channel, but he is connected to this voice channel
                boolean canView = member.hasPermission(channel, Permission.VIEW_CHANNEL)
                        || (member.getVoiceState() != null && member.getVoiceState().getChannel() == channel);
                if (canView) {
//                  The user can interact with the audio if:
//                      -> He can connect to this voice channel
//                      -> AND He can speak in this voice channel
//                      -> AND He is connected to this voice channel
                    boolean canInteract = member.hasPermission(channel, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)
                            && member.getVoiceState() != null
                            && member.getVoiceState().getChannel() == channel;
                    AudioM audioM = AudioM.getInstance(guild);
                    boolean stopped = audioM.getGuildAudioPlayer().player.getPlayingTrack() == null;
                    PlayBackInfo playBackInfo;
                    if (!stopped) {
                        boolean paused = audioM.getGuildAudioPlayer().player.isPaused();
                        long position = audioM.getGuildAudioPlayer().player.getPlayingTrack().getPosition();
                        UserAudioTrack userAudioTrack = audioM.getGuildAudioPlayer().scheduler.getCurrentPlayingTrack();

                        playBackInfo = new PlayBackInfo(paused, false, position, new TrackInfo(userAudioTrack));

                    } else {
                        playBackInfo = new PlayBackInfo(false, true, null, null);
                    }
                    Channel channelApi = new Channel(channel.getId(), channel.getName());
                    return new Status(true, channelApi, true, canInteract, playBackInfo);

                } else {
                    return new Status(true, null, false, false, null);
                }


            }
        }
        return new Status(false, null, null, null, null);
    }
}
