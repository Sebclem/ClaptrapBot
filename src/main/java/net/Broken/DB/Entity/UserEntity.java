package net.Broken.DB.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.Broken.Tools.UserManager.UserUtils;
import net.dv8tion.jda.api.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Entity for DB. Represent confirmed user account.
 */
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    @Column(unique=true)
    private String jdaId;

    private boolean isBotAdmin = false;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<UserStats> userStats;

    @OneToMany(mappedBy = "user")
    private List<PlaylistEntity> playlists;


    public UserEntity() {
    }

    public UserEntity(User user) {
        this.name = user.getName();
        this.jdaId = user.getId();
    }

    public UserEntity(String name, String id) {
        this.name = name;
        this.jdaId = id;
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

    public String getJdaId() {
        return jdaId;
    }

    public void setJdaId(String jdaId) {
        this.jdaId = jdaId;
    }

    public List<PlaylistEntity> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<PlaylistEntity> playlists) {
        this.playlists = playlists;
    }

    public void addPlaylist(PlaylistEntity... playlists) {
        if (this.playlists == null)
            this.playlists = new ArrayList<>();

        this.playlists.addAll(Arrays.asList(playlists));
    }

    public List<UserStats> getUserStats() {
        return userStats;
    }

    public void setUserStats(List<UserStats> userStats) {
        this.userStats = userStats;
    }

    public boolean isBotAdmin() {
        return isBotAdmin;
    }

    public void setBotAdmin(boolean botAdmin) {
        isBotAdmin = botAdmin;
    }
}
