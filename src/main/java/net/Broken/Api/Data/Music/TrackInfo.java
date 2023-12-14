package net.Broken.Api.Data.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.Broken.Api.Data.UserInfo;
import net.Broken.Audio.UserAudioTrack;

public record TrackInfo(UserInfo submitter, AudioTrackInfo detail) {

    public TrackInfo(UserAudioTrack userAudioTrack) {
        this(new UserInfo(userAudioTrack.getSubmittedUser().getId(), userAudioTrack.getSubmittedUser().getName(),
                userAudioTrack.getSubmittedUser().getAvatarUrl()),
                userAudioTrack.getAudioTrack().getInfo());
    }
}
