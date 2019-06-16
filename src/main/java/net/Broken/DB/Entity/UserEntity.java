package net.Broken.DB.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.Broken.Tools.UserManager.UserUtils;
import net.dv8tion.jda.core.entities.User;
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
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    private String name;

    private String jdaId;

    private String apiToken;

    private boolean isBotAdmin = false;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<UserStats> userStats;

    @JsonIgnore
    private String password;


    @OneToMany(mappedBy = "user")
    private List<PlaylistEntity> playlists;


    public UserEntity() {
    }

    public UserEntity(PendingUserEntity pendingUserEntity, String apiToken) {
        this.name = pendingUserEntity.getName();
        this.jdaId = pendingUserEntity.getJdaId();
        this.password = pendingUserEntity.getPassword();
        this.apiToken = apiToken;
    }

    public UserEntity(User user, PasswordEncoder passwordEncoder){
        this.name = user.getName();
        this.jdaId = user.getId();
        this.apiToken = UserUtils.getInstance().generateApiToken();
        this.password = passwordEncoder.encode(UserUtils.getInstance().generateCheckToken());
    }

    public UserEntity(String name, String id, PasswordEncoder passwordEncoder){
        this.name = name;
        this.jdaId = id;
        this.apiToken = UserUtils.getInstance().generateApiToken();
        this.password = passwordEncoder.encode(UserUtils.getInstance().generateCheckToken());
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public List<PlaylistEntity> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<PlaylistEntity> playlists) {
        this.playlists = playlists;
    }

    public void addPlaylist(PlaylistEntity... playlists){
        if(this.playlists == null)
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
