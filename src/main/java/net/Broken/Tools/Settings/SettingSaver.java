package net.Broken.Tools.Settings;

import net.Broken.Api.Data.Settings.Value;
import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class SettingSaver {
    private final GuildPreferenceRepository guildPreferenceRepository;
    private final GuildPreferenceEntity guildPreference;

    private final Logger logger = LogManager.getLogger();

    public SettingSaver(GuildPreferenceRepository guildPreferenceRepository, GuildPreferenceEntity guildPreference) {
        this.guildPreferenceRepository = guildPreferenceRepository;
        this.guildPreference = guildPreference;
    }

    public GuildPreferenceEntity save(List<Value> values) {
        for (Value value : values) {
            setValue(value);
        }
        return guildPreferenceRepository.save(guildPreference);
    }

    private void setValue(Value value) {
        switch (value.id()) {
//          WELCOME
            case "welcome_enable" -> guildPreference.setWelcome((Boolean) value.value());
            case "welcome_chanel_id" -> guildPreference.setWelcomeChanelID((String) value.value());
            case "welcome_message" -> guildPreference.setWelcomeMessage((String) value.value());
//          DEFAULT ROLE
            case "default_role" -> guildPreference.setDefaultRole((Boolean) value.value());
            case "default_role_id" -> guildPreference.setDefaultRoleId((String) value.value());
//          DAILY
            case "daily_madame" -> guildPreference.setDailyMadame((Boolean) value.value());
//          AUTO VOICE CHAN
            case "auto_voice" -> guildPreference.setAutoVoice((Boolean) value.value());
            case "auto_voice_base_channel" -> guildPreference.setAutoVoiceChannelID((String) value.value());
            case "auto_voice_channel_title" -> guildPreference.setAutoVoiceChannelTitle((String) value.value());
            default -> logger.warn("Invalid setting received: " + value.id());
        }
    }
}
