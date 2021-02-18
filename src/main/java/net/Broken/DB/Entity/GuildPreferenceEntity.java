package net.Broken.DB.Entity;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GuildPreferenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String guildId;

    private boolean antiSpam;

    private boolean welcome;

    private String welcomeMessage;

    private String welcomeChanelID;

    private boolean defaultRole;

    private String defaultRoleId;

    private boolean dailyMadame;

    @ElementCollection
    private List<String> visibleVoiceChanel;

    private boolean autoVoice;
    private String autoVoiceChannelID;
    private String autoVoiceChannelTitle;


    public GuildPreferenceEntity(String guildId,
                                 boolean antiSpam,
                                 boolean welcome,
                                 String welcomeMessage,
                                 String welcomeChanelID,
                                 boolean defaultRole,
                                 String defaultRoleId,
                                 boolean dailyMadame,
                                 ArrayList<String> visibleVoiceChanel,
                                 boolean autoVoice,
                                 String autoVoiceChannelID,
                                 String autoVoiceChannelTitle) {
        this.guildId = guildId;
        this.antiSpam = antiSpam;
        this.welcome = welcome;
        this.welcomeMessage = welcomeMessage;
        this.welcomeChanelID = welcomeChanelID;
        this.defaultRole = defaultRole;
        this.defaultRoleId = defaultRoleId;
        this.dailyMadame = dailyMadame;
        this.visibleVoiceChanel = visibleVoiceChanel;
        this.autoVoice = autoVoice;
        this.autoVoiceChannelID = autoVoiceChannelID;
        this.autoVoiceChannelTitle = autoVoiceChannelTitle;
    }

    public GuildPreferenceEntity() {
    }


    public static GuildPreferenceEntity getDefault(Guild guild) {
        ArrayList<String> voice = new ArrayList<>();
        for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
            voice.add(voiceChannel.getId());
        }

        return new GuildPreferenceEntity(guild.getId(), false, false, "Welcome to this awesome server @name! ", " ", false, " ", true, voice, false, " ", " ");
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

    public boolean isDailyMadame() {
        return dailyMadame;
    }

    public void setDailyMadame(boolean dailyMadame) {
        this.dailyMadame = dailyMadame;
    }

    public List<String> getVisibleVoiceChanel() {
        return visibleVoiceChanel;
    }

    public void setVisibleVoiceChanel(List<String> visibleVoiceChanel) {
        this.visibleVoiceChanel = visibleVoiceChanel;
    }

    public boolean isAutoVoice() {
        return autoVoice;
    }

    public void setAutoVoice(boolean autoVoice) {
        this.autoVoice = autoVoice;
    }

    public String getAutoVoiceChannelID() {
        return autoVoiceChannelID;
    }

    public void setAutoVoiceChannelID(String autoVoiceChannelID) {
        this.autoVoiceChannelID = autoVoiceChannelID;
    }

    public String getAutoVoiceChannelTitle() {
        return autoVoiceChannelTitle;
    }

    public void setAutoVoiceChannelTitle(String autoVoiceChannelTitle) {
        this.autoVoiceChannelTitle = autoVoiceChannelTitle;
    }
}
