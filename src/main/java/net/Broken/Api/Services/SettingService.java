package net.Broken.Api.Services;

import liquibase.pro.packaged.V;
import net.Broken.Api.Data.Settings.SettingDescriber;
import net.Broken.Api.Data.Settings.SettingGroup;
import net.Broken.Api.Data.Settings.Value;
import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingService {

    public final GuildPreferenceRepository preferenceRepository;
    private final Logger logger = LogManager.getLogger();


    public SettingService(GuildPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    public List<SettingGroup> getSettingDescription() {
        List<SettingGroup> toReturn = new ArrayList<>();
        toReturn.add(getWelcomeGroup());
        toReturn.add(getDefaultRoleGroup());
        toReturn.add(getAutoVoiceChannelGroup());
        toReturn.add(getDailyGroup());
        return toReturn;
    }

    private SettingGroup getWelcomeGroup() {
        SettingDescriber mainField = new SettingDescriber(
                "welcome_enable",
                "Enable Welcome Message",
                null,
                SettingDescriber.TYPE.BOOL
        );

        List<SettingDescriber> fields = new ArrayList<>();
        fields.add(new SettingDescriber(
                "welcome_chanel_id",
                "Welcome Message chanel",
                null,
                SettingDescriber.TYPE.TEXT_CHANNEL
        ));
        fields.add(new SettingDescriber(
                "welcome_message",
                "Welcome Message",
                null,
                SettingDescriber.TYPE.STRING
        ));


        return new SettingGroup(
                "Welcome Message",
                mainField,
                fields);
    }

    private SettingGroup getDefaultRoleGroup() {
        SettingDescriber mainField = new SettingDescriber(
                "default_role",
                "Enable Default Role",
                null,
                SettingDescriber.TYPE.BOOL
        );

        List<SettingDescriber> fields = new ArrayList<>();
        fields.add(new SettingDescriber(
                "default_role_id",
                "Default Role",
                null,
                SettingDescriber.TYPE.ROLE
        ));

        return new SettingGroup(
                "Default Role",
                mainField,
                fields);
    }

    private SettingGroup getDailyGroup() {
        List<SettingDescriber> fields = new ArrayList<>();
        fields.add(new SettingDescriber(
                "daily_madame",
                "[NSFW] Enable Daily Madame Message",
                null,
                SettingDescriber.TYPE.BOOL
        ));

        return new SettingGroup(
                "Daily",
                null,
                fields);
    }

    private SettingGroup getAutoVoiceChannelGroup() {
        SettingDescriber mainField = new SettingDescriber(
                "auto_voice",
                "Enable Auto Create Voice Chanel",
                 null,
                SettingDescriber.TYPE.BOOL
        );

        List<SettingDescriber> fields = new ArrayList<>();
        fields.add(new SettingDescriber(
                "auto_voice_base_channel",
                "Base Voice Channel For Auto Create",
                "If someone joint this channel, a new voice channel will be created with the same settings.",
                SettingDescriber.TYPE.VOICE_CHANNEL
        ));
        fields.add(new SettingDescriber(
                "auto_voice_channel_title",
                "Auto Created Voice Channel title",
                "Auto created voice channel will use this title, @count will be replaced by the channel count.",
                SettingDescriber.TYPE.STRING
        ));

        return new SettingGroup(
                "Auto Voice Channel",
                mainField,
                fields);
    }


    public List<Value> getValues(String guildId) {
        GuildPreferenceEntity pref = preferenceRepository.findByGuildId(guildId).orElseGet(() -> {
            logger.info("[API] : Generate default guild pref");
            return preferenceRepository.save(GuildPreferenceEntity.getDefault(guildId));
        });
        List<Value> values = new ArrayList<>(getWelcomeValues(pref));
        values.addAll(getDefaultRoleValues(pref));
        values.addAll(getDailyValues(pref));
        values.addAll(getAutoVoiceChannelValues(pref));

        return values;
    }

    private List<Value> getWelcomeValues(GuildPreferenceEntity pref) {
        List<Value> toReturn = new ArrayList<>();
        toReturn.add(new Value(
                        "welcome_enable",
                        pref.isWelcome()
                )
        );
        toReturn.add(new Value(
                        "welcome_chanel_id",
                        pref.getWelcomeChanelID()
                )
        );
        toReturn.add(new Value(
                        "welcome_message",
                        pref.getWelcomeMessage()
                )
        );
        return toReturn;
    }
    private List<Value> getDefaultRoleValues(GuildPreferenceEntity pref) {
        List<Value> toReturn = new ArrayList<>();
        toReturn.add(new Value(
                        "default_role",
                        pref.isDefaultRole()
                )
        );
        toReturn.add(new Value(
                        "default_role_id",
                        pref.getDefaultRoleId()
                )
        );
        return toReturn;
    }
    private List<Value> getDailyValues(GuildPreferenceEntity pref) {
        List<Value> toReturn = new ArrayList<>();
        toReturn.add(new Value(
                        "daily_madame",
                        pref.isDailyMadame()
                )
        );
        return toReturn;
    }

    private List<Value> getAutoVoiceChannelValues(GuildPreferenceEntity pref) {
        List<Value> toReturn = new ArrayList<>();
        toReturn.add(new Value(
                        "auto_voice",
                        pref.isAutoVoice()
                )
        );
        toReturn.add(new Value(
                        "auto_voice_base_channel",
                        pref.getAutoVoiceChannelID()
                )
        );
        toReturn.add(new Value(
                        "auto_voice_channel_title",
                        pref.getAutoVoiceChannelTitle()
                )
        );
        return toReturn;
    }
}
