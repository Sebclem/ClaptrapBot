package net.Broken.DB.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class PlaylistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userEntity_id", nullable = false)
    private UserEntity user;


    @OneToMany(mappedBy = "playlist")
    private List<TrackEntity> tracks;


    public PlaylistEntity() {
    }

    public PlaylistEntity(String name, UserEntity user) {
        this.name = name;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<TrackEntity> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackEntity> tracks) {
        this.tracks = tracks;
    }

    public void addTracks(TrackEntity... tracks) {
        if (this.tracks == null)
            this.tracks = new ArrayList<>();

        this.tracks.addAll(Arrays.asList(tracks));
    }


}
