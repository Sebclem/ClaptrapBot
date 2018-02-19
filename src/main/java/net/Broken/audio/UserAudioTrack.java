package net.Broken.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.User;

public class UserAudioTrack{
    private User user;
    private AudioTrack audioTrack;

    public UserAudioTrack(User user, AudioTrack audioTrack) {
        this.user = user;
        this.audioTrack = audioTrack;
    }

    public User getSubmitedUser() {
        return user;
    }

    public AudioTrack getAudioTrack() {
        return audioTrack;
    }
}
