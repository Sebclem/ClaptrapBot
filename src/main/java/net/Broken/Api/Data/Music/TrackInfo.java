package net.Broken.Api.Data.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.Broken.Api.Data.UserInfo;
import net.Broken.audio.UserAudioTrack;

public record TrackInfo(UserInfo submitter, AudioTrackInfo detail) {
    public TrackInfo(UserInfo submitter, AudioTrackInfo detail) {
        this.submitter = submitter;
        this.detail = detail;
    }

    public TrackInfo(UserAudioTrack userAudioTrack) {
        this(new UserInfo(userAudioTrack.getSubmittedUser().getId(), userAudioTrack.getSubmittedUser().getName(), userAudioTrack.getSubmittedUser().getAvatarUrl()),
                userAudioTrack.getAudioTrack().getInfo());
    }
}