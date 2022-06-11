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
import java.util.List;

public class SettingsUtils {

    private static SettingsUtils INSTANCE;
    private final Logger logger = LogManager.getLogger();
    private final GuildPreferenceRepository guildPreferenceRepository;
    private final UserRepository userRepository;
    private SettingsUtils() {
        ApplicationContext context = SpringContext.getAppContext();
        guildPreferenceRepository = (GuildPreferenceRepository) context.getBean("guildPreferenceRepository");
        userRepository = (UserRepository) context.getBean("userRepository");


    }

    public static SettingsUtils getInstance() {
        return (INSTANCE == null) ? new SettingsUtils() : INSTANCE;
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
                User jdaUser = MainBot.jda.getUserById(user.getDiscordId());
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

    public GuildPreferenceEntity getPreference(Guild guild) {
        return guildPreferenceRepository.findByGuildId(guild.getId()).orElseGet(()->{
            logger.info("Generate default pref for " + guild.getName());
            return guildPreferenceRepository.save(GuildPreferenceEntity.getDefault(guild.getId()));
        });
    }
}
