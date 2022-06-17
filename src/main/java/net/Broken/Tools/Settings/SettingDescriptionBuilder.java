package net.Broken.Tools.Settings;

import net.Broken.Api.Data.Settings.SettingDescriber;
import net.Broken.Api.Data.Settings.SettingGroup;

import java.util.ArrayList;
import java.util.List;

public class SettingDescriptionBuilder {
    public List<SettingGroup> build(){
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
}
