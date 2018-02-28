package net.Broken.RestApi.Data;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.List;
/**
 * Data for JSON Parsing
 */
public class PlaylistData {

    private List<UserAudioTrackData> list;

    public PlaylistData(List<UserAudioTrackData> list) {
        this.list = list;
    }

    public List<UserAudioTrackData> getList() {
        return list;
    }
}
