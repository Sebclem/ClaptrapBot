package net.Broken.DB.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import net.Broken.Api.Security.Data.DiscordOauthUserInfo;
import net.dv8tion.jda.api.entities.User;

/**
 * Entity for DB. Represent confirmed user account.
 */
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String discriminator;

    @Column(unique = true)
    private String discordId;

    private String avatar;

    private boolean isBotAdmin = false;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<UserStats> userStats;

    public UserEntity() {
    }

    public UserEntity(User user) {
        this.username = user.getName();
        this.discordId = user.getId();
    }

    public UserEntity(String username, String id) {
        this.username = username;
        this.discordId = id;
    }

    public UserEntity(DiscordOauthUserInfo discordOauthUserInfo) {
        this.username = discordOauthUserInfo.username();
        this.discriminator = discordOauthUserInfo.discriminator();
        this.discordId = discordOauthUserInfo.id();
        this.avatar = discordOauthUserInfo.avatar();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }
}
