package net.Broken.RestApi.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
/**
 * Data for JSON Parsing
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrentMusicData {
    private final UserAudioTrackData info;
    private final long currentPos;
    private final String state;
    private final boolean pause;
    private final boolean autoflow;


    public CurrentMusicData(UserAudioTrackData info, long currentPos, String state, boolean pause, boolean autoflow) {
        this.info = info;
        this.currentPos = currentPos;
        this.state = state;
        this.pause = pause;
        this.autoflow = autoflow;
    }

    public UserAudioTrackData getInfo() {
        return info;
    }

    public long getCurrentPos() {
        return currentPos;
    }

    public String getState() {
        if(pause)
            return "PAUSE";
        else
            return state;
    }

    public boolean isAutoflow() {
        return autoflow;
    }
}
