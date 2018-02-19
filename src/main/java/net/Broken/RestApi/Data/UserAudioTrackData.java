package net.Broken.RestApi.Data;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.Broken.audio.UserAudioTrack;

public class UserAudioTrackData {
    private String user;
    private AudioTrackInfo audioTrackInfo;

    public UserAudioTrackData(String user, AudioTrackInfo audioTrackInfo) {
        this.user = user;
        this.audioTrackInfo = audioTrackInfo;
    }

    public UserAudioTrackData(UserAudioTrack userAudioTrack){
        this.audioTrackInfo = userAudioTrack.getAudioTrack().getInfo();
        this.user = userAudioTrack.getSubmitedUser().getName();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public AudioTrackInfo getAudioTrackInfo() {
        return audioTrackInfo;
    }

    public void setAudioTrackInfo(AudioTrackInfo audioTrackInfo) {
        this.audioTrackInfo = audioTrackInfo;
    }
}
