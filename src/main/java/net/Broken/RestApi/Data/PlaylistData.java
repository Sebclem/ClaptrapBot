package net.Broken.RestApi.Data;

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
