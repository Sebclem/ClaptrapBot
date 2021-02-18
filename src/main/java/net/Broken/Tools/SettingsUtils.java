package net.Broken.Tools;

import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.Settings.GetSettingsData;
import net.Broken.RestApi.Data.Settings.PostSetSettings;
import net.Broken.RestApi.Data.Settings.Value;
import net.Broken.SpringContext;
import net.Broken.Tools.UserManager.UserUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SettingsUtils {

    private static SettingsUtils INSTANCE;
    private final Logger logger = LogManager.getLogger();

    public static SettingsUtils getInstance() {
        return (INSTANCE == null) ? new SettingsUtils() : INSTANCE;
    }

    private final GuildPreferenceRepository guildPreferenceRepository;
    private final UserRepository userRepository;


    private SettingsUtils() {
        ApplicationContext context = SpringContext.getAppContext();
        guildPreferenceRepository = (GuildPreferenceRepository) context.getBean("guildPreferenceRepository");
        userRepository = (UserRepository) context.getBean("userRepository");


    }

    /**
     * Extract all settings for displaying on webpage
     *
     * @param guild The guild
     * @return All formatted settings
     */
    public ArrayList<GetSettingsData> extractSettings(Guild guild) {

        ArrayList<GetSettingsData> list = new ArrayList<>();
        List<GuildPreferenceEntity> guildPrefList = guildPreferenceRepository.findByGuildId(guild.getId());
        GuildPreferenceEntity guildPref;
        if (guildPrefList.isEmpty()) {
            guildPref = GuildPreferenceEntity.getDefault(guild);
            guildPreferenceRepository.save(guildPref);
        } else
            guildPref = guildPrefList.get(0);

        List<String> visibleVoice = new ArrayList<>(guildPref.getVisibleVoiceChanel());
        if (visibleVoice.size() == 0) {
            guildPref = setDefaultVoiceChannels(guild, guildPref);
        }
        list.add(new GetSettingsData(
                "Visible Voices Channels",
                null,
                "voices_channels",
                GetSettingsData.TYPE.SELECT_LIST,
                getVoiceChannels(guild, visibleVoice),
                null
        ));

        list.add(new GetSettingsData(
                "Enable Welcome Message",
                null,
                "welcome",
                GetSettingsData.TYPE.BOOL,
                null,
                Boolean.toString(guildPref.isWelcome())
        ));
        list.add(new GetSettingsData(
                "Welcome Message chanel",
                null,
                "welcome_chanel_id",
                GetSettingsData.TYPE.LIST,
                getTextChannels(guild),
                guildPref.getWelcomeChanelID()
        ));
        list.add(new GetSettingsData(
                "Welcome Message",
                null,
                "welcome_message",
                GetSettingsData.TYPE.STRING,
                null,
                guildPref.getWelcomeMessage()
        ));


        list.add(new GetSettingsData(
                "Enable Default Role",
                null,
                "default_role",
                GetSettingsData.TYPE.BOOL,
                null,
                Boolean.toString(guildPref.isDefaultRole())
        ));
        list.add(new GetSettingsData(
                "Default Role",
                null,
                "default_role_id",
                GetSettingsData.TYPE.LIST,
                getRoles(guild),
                guildPref.getDefaultRoleId()
        ));

        list.add(new GetSettingsData(
                "Enable Anti Spam",
                null,
                "anti_spam",
                GetSettingsData.TYPE.BOOL,
                null,
                Boolean.toString(guildPref.isAntiSpam())
        ));

        list.add(new GetSettingsData(
                "Enable Daily Madame Message",
                null,
                "daily_madame",
                GetSettingsData.TYPE.BOOL,
                null,
                Boolean.toString(guildPref.isDailyMadame())
        ));

        list.add(new GetSettingsData(
                "Enable Auto Create Voice Chanel",
                null,
                "auto_voice",
                GetSettingsData.TYPE.BOOL,
                null,
                Boolean.toString(guildPref.isAutoVoice())
        ));
        list.add(new GetSettingsData(
                "Base Voice Channel For Auto Create",
                "If someone joint this channel, a new voice channel will be created with the same settings.",
                "auto_voice_base_channel",
                GetSettingsData.TYPE.LIST,
                getVoiceChannels(guild, null),
                guildPref.getAutoVoiceChannelID()
        ));
        list.add(new GetSettingsData(
                "Auto Created Voice Channel title",
                "Auto created voice channel will use this title, @count will be replaced by the channel count.",
                "auto_voice_channel_title",
                GetSettingsData.TYPE.STRING,
                null,
                guildPref.getAutoVoiceChannelTitle()
        ));

        return list;

    }

    /**
     * Check if the user have the permission to set settings
     *
     * @param token User token
     * @param guild Guild
     * @return True if user have ADMINISTRATOR permission
     */
    public boolean checkPermission(String token, String guild) {
        if (token == null || guild == null) {
            return false;
        } else {
            try {
                UserEntity user = UserUtils.getInstance().getUserWithApiToken(userRepository, token);
                User jdaUser = MainBot.jda.getUserById(user.getJdaId());
                Guild jdaGuild = MainBot.jda.getGuildById(guild);
                if (jdaGuild == null || jdaUser == null)
                    return false;

                Member guildUser = jdaGuild.getMember(jdaUser);
                if (guildUser == null)
                    return false;
                return guildUser.hasPermission(Permission.ADMINISTRATOR);
            } catch (Exception e) {
                logger.debug("Unknown Token or user :" + token);
                return false;
            }
        }
    }


    public boolean setSettings(Guild guild, List<PostSetSettings> settings) {
        GuildPreferenceEntity pref = getPreference(guild);
        for (PostSetSettings setting : settings) {
            String value = setting.val;
            logger.debug(setting.id + " : " + value);
            switch (setting.id) {
                case "voices_channels":
                    List<String> list = checkVoiceChanel(guild, setting.vals);
                    if (list == null) {
                        logger.error("voices_channels error, bad ID.");
                        return false;
                    } else
                        pref.setVisibleVoiceChanel(list);
                    break;

                case "anti_spam":
                    boolean result_as = Boolean.parseBoolean(value);
                    pref.setAntiSpam(result_as);
                    break;

                case "default_role":
                    boolean result_df = Boolean.parseBoolean(value);
                    pref.setDefaultRole(result_df);
                    pref = guildPreferenceRepository.save(pref);
                    break;

                case "default_role_id":
                    try {
                        Role role = guild.getRoleById(value);
                        if (role != null) {
                            pref.setDefaultRoleId(role.getId());
                            pref = guildPreferenceRepository.save(pref);
                        } else
                            throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        logger.error("default_role_id error. Key: " + setting.id + " Val: " + setting.val);
                        return false;
                    }
                    break;

                case "welcome":
                    boolean result_w = Boolean.parseBoolean(value);
                    pref.setWelcome(result_w);
                    break;
                case "welcome_chanel_id":
                    try {
                        TextChannel channel = guild.getTextChannelById(value);
                        if (channel != null) {
                            pref.setWelcomeChanelID(channel.getId());
                        } else
                            throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        logger.error("welcome_chanel_id error. Key: " + setting.id + " Val: " + setting.val);
                        return false;
                    }
                    break;

                case "welcome_message":
                    pref.setWelcomeMessage(value);
                    break;

                case "daily_madame":
                    boolean result_dm = Boolean.parseBoolean(value);
                    pref.setDailyMadame(result_dm);
                    break;

                case "auto_voice":
                    boolean result_av = Boolean.parseBoolean(value);
                    pref.setAutoVoice(result_av);
                    break;

                case "auto_voice_base_channel":
                    VoiceChannel channel = guild.getVoiceChannelById(value);
                    if (channel == null) {
                        logger.error("voices_channels error, bad ID.");
                        return false;
                    } else
                        pref.setAutoVoiceChannelID(channel.getId());
                    break;

                case "auto_voice_channel_title":
                    pref.setAutoVoiceChannelTitle(value);
                    break;
            }
        }
        guildPreferenceRepository.save(pref);
        return true;
    }


    private List<Value> getTextChannels(Guild guild) {
        List<Value> channels = new ArrayList<>();
        for (TextChannel channel : guild.getTextChannels()) {
            channels.add(new Value(channel.getName(), channel.getId()));
        }
        return channels;
    }

    private List<Value> getRoles(Guild guild) {
        List<Value> roles = new ArrayList<>();
        for (Role role : guild.getRoles()) {
            roles.add(new Value(role.getName(), role.getId()));
        }
        return roles;
    }


    private List<Value> getVoiceChannels(Guild guild, List<String> selected) {
        List<Value> channels = new ArrayList<>();
        for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
            if (selected == null)
                channels.add(new Value(voiceChannel.getName(), voiceChannel.getId()));
            else
                channels.add(new Value(voiceChannel.getName(), voiceChannel.getId(), selected.contains(voiceChannel.getId())));
        }
        return channels;
    }

    private List<String> checkVoiceChanel(Guild guild, List<String> values) {
        List<String> list = new ArrayList<>();
        for (String value : values) {
            if (guild.getVoiceChannelById(value) != null) {
                list.add(value);
            } else {
                logger.error("Unknown voice channel id: " + value);
                list = null;
                break;
            }
        }
        return list;
    }

    public GuildPreferenceEntity cleanVisibleVoicePref(Guild guild, GuildPreferenceEntity guildPref) {
        List<String> voice = guildPref.getVisibleVoiceChanel();
        for (String prefVoice : guildPref.getVisibleVoiceChanel()) {
            if (guild.getVoiceChannelById(prefVoice) == null)
                voice.remove(prefVoice);
        }
        guildPref.setVisibleVoiceChanel(voice);
        return guildPreferenceRepository.save(guildPref);
    }


    public GuildPreferenceEntity setDefaultVoiceChannels(Guild guild, GuildPreferenceEntity guildPref) {
        List<String> prefVoice = guildPref.getVisibleVoiceChanel();
        if (prefVoice == null)
            prefVoice = new ArrayList<>();
        for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
            prefVoice.add(voiceChannel.getId());
        }
        guildPref.setVisibleVoiceChanel(prefVoice);
        return guildPreferenceRepository.save(guildPref);

    }

    public GuildPreferenceEntity getPreference(Guild guild) {
        List<GuildPreferenceEntity> guildPrefList = guildPreferenceRepository.findByGuildId(guild.getId());
        GuildPreferenceEntity guildPref;
        if (guildPrefList.isEmpty()) {
            logger.info("Generate default pref for " + guild.getName());
            guildPref = GuildPreferenceEntity.getDefault(guild);
            guildPreferenceRepository.save(guildPref);
        } else
            guildPref = guildPrefList.get(0);
        return guildPref;
    }
}
