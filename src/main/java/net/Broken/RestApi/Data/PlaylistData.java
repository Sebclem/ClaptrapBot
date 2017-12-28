package net.Broken.RestApi.Data;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.List;

public class PlaylistData {

    private List<AudioTrackInfo> list;

    public PlaylistData(List<AudioTrackInfo> list) {
        this.list = list;
    }

    public List<AudioTrackInfo> getList() {
        return list;
    }
}
