package net.Broken.DB.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import javax.persistence.*;

@Entity
public class TrackEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    private String title;

    private String url;

    private String identifier;

    private Integer pos;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="playlistEntity_id", nullable=false)
    private PlaylistEntity playlist;

    public TrackEntity() {
    }

    public TrackEntity(AudioTrackInfo trackInfo, int pos, PlaylistEntity playlist) {
        this.title = trackInfo.title;
        this.url = trackInfo.uri;
        this.identifier = trackInfo.identifier;
        this.playlist = playlist;
        this.pos = pos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PlaylistEntity getPlaylist() {
        return playlist;
    }

    public void setPlaylist(PlaylistEntity playlist) {
        this.playlist = playlist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }
}
