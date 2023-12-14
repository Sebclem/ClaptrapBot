package net.Broken.Tools.Settings;

import java.util.ArrayList;
import java.util.List;

import net.Broken.Api.Data.Settings.Value;
import net.Broken.DB.Entity.GuildPreferenceEntity;

public class SettingValueBuilder {
        private final GuildPreferenceEntity guildPreference;

        public SettingValueBuilder(GuildPreferenceEntity guildPreference) {
                this.guildPreference = guildPreference;
        }

        public List<Value> build() {
                List<Value> values = new ArrayList<>(getWelcomeValues(guildPreference));
                values.addAll(getDefaultRoleValues(guildPreference));
                values.addAll(getDailyValues(guildPreference));
                values.addAll(getAutoVoiceChannelValues(guildPreference));
                return values;
        }

        private List<Value> getWelcomeValues(GuildPreferenceEntity pref) {
                List<Value> toReturn = new ArrayList<>();
                toReturn.add(new Value(
                                "welcome_enable",
                                pref.isWelcome()));
                toReturn.add(new Value(
                                "welcome_chanel_id",
                                pref.getWelcomeChanelID()));
                toReturn.add(new Value(
                                "welcome_message",
                                pref.getWelcomeMessage()));
                return toReturn;
        }

        private List<Value> getDefaultRoleValues(GuildPreferenceEntity pref) {
                List<Value> toReturn = new ArrayList<>();
                toReturn.add(new Value(
                                "default_role",
                                pref.isDefaultRole()));
                toReturn.add(new Value(
                                "default_role_id",
                                pref.getDefaultRoleId()));
                return toReturn;
        }

        private List<Value> getDailyValues(GuildPreferenceEntity pref) {
                List<Value> toReturn = new ArrayList<>();
                toReturn.add(new Value(
                                "daily_madame",
                                pref.isDailyMadame()));
                return toReturn;
        }

        private List<Value> getAutoVoiceChannelValues(GuildPreferenceEntity pref) {
                List<Value> toReturn = new ArrayList<>();
                toReturn.add(new Value(
                                "auto_voice",
                                pref.isAutoVoice()));
                toReturn.add(new Value(
                                "auto_voice_base_channel",
                                pref.getAutoVoiceChannelID()));
                toReturn.add(new Value(
                                "auto_voice_channel_title",
                                pref.getAutoVoiceChannelTitle()));
                return toReturn;
        }

}
