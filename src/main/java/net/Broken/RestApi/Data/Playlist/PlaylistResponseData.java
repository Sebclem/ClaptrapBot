package net.Broken.RestApi.Data.Playlist;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.Broken.DB.Entity.PlaylistEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaylistResponseData {
    public String message;
    public String error;
    public PlaylistEntity playlist;

    public PlaylistResponseData(String message, PlaylistEntity playlist) {
        this.message = message;
        this.playlist = playlist;
    }

    public PlaylistResponseData(String message, String error) {
        this.message = message;
        this.error = error;
    }
}
