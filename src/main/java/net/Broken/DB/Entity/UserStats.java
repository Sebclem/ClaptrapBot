package net.Broken.DB.Entity;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
public class UserStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String guildId;

    @ManyToOne
    @JoinColumn(name = "userEntity_id", nullable = false)
    private UserEntity user;

    @ColumnDefault("0")
    private Long vocalTime = 0L;

    @ColumnDefault("0")
    private Long messageCount = 0L;

    @ColumnDefault("0")
    private Long apiCommandCount = 0L;

    public UserStats() {
    }

    public UserStats(String guildId, UserEntity user) {
        this.guildId = guildId;
        this.user = user;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Long getVocalTime() {
        return vocalTime;
    }

    public void setVocalTime(Long vocalTime) {
        this.vocalTime = vocalTime;
    }

    public Long getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Long messageCount) {
        this.messageCount = messageCount;
    }

    public Long getApiCommandCount() {
        return apiCommandCount;
    }

    public void setApiCommandCount(Long apiCommandCount) {
        this.apiCommandCount = apiCommandCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }
}
