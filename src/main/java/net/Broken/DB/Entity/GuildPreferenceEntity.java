package net.Broken.DB.Entity;

import net.dv8tion.jda.core.entities.Guild;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class GuildPreferenceEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    private String guildId;

    private boolean antiSpam;

    private boolean welcome;

    private String welcomeMessage;

    private String welcomeChanelID;

    private boolean defaultRole;

    private String defaultRoleId;


    public GuildPreferenceEntity(String guildId, boolean antiSpam, boolean welcome, String welcomeMessage, String welcomeChanelID, boolean defaultRole, String defaultRoleId) {
        this.guildId = guildId;
        this.antiSpam = antiSpam;
        this.welcome = welcome;
        this.welcomeMessage = welcomeMessage;
        this.welcomeChanelID = welcomeChanelID;
        this.defaultRole = defaultRole;
        this.defaultRoleId = defaultRoleId;
    }

    public GuildPreferenceEntity(){}


    public static GuildPreferenceEntity getDefault(Guild guild){
        return new GuildPreferenceEntity(guild.getId(), false, false, "Welcome to this awesome server @name! ", " ", false, " ");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public boolean isAntiSpam() {
        return antiSpam;
    }

    public void setAntiSpam(boolean antiSpam) {
        this.antiSpam = antiSpam;
    }

    public boolean isWelcome() {
        return welcome;
    }

    public void setWelcome(boolean welcome) {
        this.welcome = welcome;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public String getWelcomeChanelID() {
        return welcomeChanelID;
    }

    public void setWelcomeChanelID(String welcomeChanelID) {
        this.welcomeChanelID = welcomeChanelID;
    }

    public String getDefaultRoleId() {
        return defaultRoleId;
    }

    public void setDefaultRoleId(String defaultRoleId) {
        this.defaultRoleId = defaultRoleId;
    }

    public boolean isDefaultRole() {
        return defaultRole;
    }

    public void setDefaultRole(boolean defaultRole) {
        this.defaultRole = defaultRole;
    }
}
