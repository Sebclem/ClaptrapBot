package net.Broken.Tools;

import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.DB.Repository.PendingPwdResetRepository;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.Settings.GetSettingsData;
import net.Broken.RestApi.Data.Settings.PostSetSettings;
import net.Broken.RestApi.Data.Settings.Value;
import net.Broken.SpringContext;
import net.Broken.Tools.UserManager.Exceptions.UnknownTokenException;
import net.Broken.Tools.UserManager.UserUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

public class SettingsUtils {

    private static SettingsUtils INSTANCE;
    private Logger logger = LogManager.getLogger();

    public static SettingsUtils getInstance(){
        return (INSTANCE == null) ? new SettingsUtils() : INSTANCE;
    }

    GuildPreferenceRepository guildPreferenceRepository;
    UserRepository userRepository;


    private SettingsUtils() {
        ApplicationContext context = SpringContext.getAppContext();
        guildPreferenceRepository = (GuildPreferenceRepository) context.getBean("guildPreferenceRepository");
        userRepository = (UserRepository) context.getBean("userRepository");


    }

    public ArrayList<GetSettingsData> extractSettings(Guild guild){
        ArrayList<GetSettingsData> list = new ArrayList<>();

        List<GuildPreferenceEntity> guildPrefList = guildPreferenceRepository.findByGuildId(guild.getId());
        GuildPreferenceEntity guildPref;
        if(guildPrefList.isEmpty()){
            guildPref = GuildPreferenceEntity.getDefault(guild);
            guildPreferenceRepository.save(guildPref);
        }
        else
            guildPref = guildPrefList.get(0);



        list.add(new GetSettingsData(
                "Enable Welcome Message",
                "welcome",
                GetSettingsData.TYPE.BOOL,
                null,
                Boolean.toString(guildPref.isWelcome())
        ));
        list.add(new GetSettingsData(
                "Welcome Message chanel",
                "welcome_chanel_id",
                GetSettingsData.TYPE.LIST,
                getTextChannels(guild),
                guildPref.getWelcomeChanelID()
        ));
        list.add(new GetSettingsData(
                "Welcome Message",
                "welcome_message",
                GetSettingsData.TYPE.STRING,
                null,
                guildPref.getWelcomeMessage()
        ));


        list.add(new GetSettingsData(
                "Enable Default Role",
                "default_role",
                GetSettingsData.TYPE.BOOL,
                null,
                Boolean.toString(guildPref.isDefaultRole())
        ));
        list.add(new GetSettingsData(
                "Default Role",
                "default_role_id",
                GetSettingsData.TYPE.LIST,
                getRoles(guild),
                guildPref.getDefaultRoleId()
        ));


        list.add(new GetSettingsData(
                "Enable Anti Spam",
                "anti_spam",
                GetSettingsData.TYPE.BOOL,
                null,
                Boolean.toString(guildPref.isAntiSpam())
        ));

        list.add(new GetSettingsData(
                "Enable Daily Madame Message",
                "daily_madame",
                GetSettingsData.TYPE.BOOL,
                null,
                Boolean.toString(guildPref.isDailyMadame())
        ));

        return list;

    }


    public boolean checkPermission(String token, String guild){
        if(token == null || guild == null){
            return false;
        }
        else{
            try {
                UserEntity user = UserUtils.getInstance().getUserWithApiToken(userRepository, token);
                User jdaUser = MainBot.jda.getUserById(user.getJdaId());
                Guild jdaGuild = MainBot.jda.getGuildById(guild);
                if(jdaGuild == null){
                    return false;
                }
                if(!jdaGuild.getMember(jdaUser).hasPermission(Permission.ADMINISTRATOR)){
                    return false;
                }






                return true;

            } catch (Exception e) {
                logger.warn("Unknown Token! " + token);
                return false;
            }
        }
    }


    public boolean setSettings(Guild guild, List<PostSetSettings> settings){
        GuildPreferenceEntity pref = getPreference(guild);
        for (PostSetSettings setting : settings){
            String value = setting.val;
            logger.debug(setting.id + " : " + value);
            switch (setting.id) {
                case "anti_spam":
                    if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
                        boolean result = Boolean.parseBoolean(value);
                        pref.setAntiSpam(result);
                        pref = guildPreferenceRepository.save(pref);
                    } else {
                        logger.error("anti_spam error. Key: " + setting.id + " Val: " + setting.val);
                        return false;
                    }

                    break;


                case "default_role":
                    if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
                        boolean result = Boolean.parseBoolean(value);
                        pref.setDefaultRole(result);
                        pref = guildPreferenceRepository.save(pref);
                    } else {
                        logger.error("default_role error. Key: " + setting.id + " Val: " + setting.val);

                        return false;
                    }

                    break;
                case "default_role_id":
                    try {
                        Role role = guild.getRoleById(value);
                        if (role != null) {
                            pref.setDefaultRoleId(role.getId());

                            pref = guildPreferenceRepository.save(pref);

                        } else {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        logger.error("default_role_id error. Key: " + setting.id + " Val: " + setting.val);

                        return false;
                    }

                    break;
                case "welcome":
                    if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
                        boolean result = Boolean.parseBoolean(value);
                        pref.setWelcome(result);
                        pref = guildPreferenceRepository.save(pref);
                    } else {
                        logger.error("welcome error. Key: " + setting.id + " Val: " + setting.val);
                        return false;
                    }
                    break;
                case "welcome_chanel_id":
                    try {
                        TextChannel chanel = guild.getTextChannelById(value);
                        if (chanel != null) {
                            pref.setWelcomeChanelID(chanel.getId());

                            pref = guildPreferenceRepository.save(pref);

                        } else {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        logger.error("welcome_chanel_id error. Key: " + setting.id + " Val: " + setting.val);

                        return false;
                    }
                    break;
                case "welcome_message":
                    pref.setWelcomeMessage(value);
                    pref = guildPreferenceRepository.save(pref);

                    break;

                case "daily_madame":
                    if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
                        boolean result = Boolean.parseBoolean(value);
                        pref.setDailyMadame(result);
                        pref = guildPreferenceRepository.save(pref);

                    } else {
                        logger.error("daily_madame error. Key: " + setting.id + " Val: " + setting.val);

                        return false;
                    }
                    break;
            }
        }
        return true;

    }


    private List<Value> getTextChannels(Guild guild){
        List<Value> channels = new ArrayList<>();
        for(TextChannel channel : guild.getTextChannels()){
            channels.add(new Value(channel.getName(), channel.getId()));
        }
        return channels;
    }

    private List<Value> getRoles(Guild guild){
        List<Value> roles = new ArrayList<>();
        for(Role role : guild.getRoles()){
            roles.add(new Value(role.getName(), role.getId()));
        }
        return roles;
    }

    public GuildPreferenceEntity getPreference(Guild guild){
        List<GuildPreferenceEntity> guildPrefList = guildPreferenceRepository.findByGuildId(guild.getId());
        GuildPreferenceEntity guildPref;
        if(guildPrefList.isEmpty()){
            logger.info("Generate default pref for " + guild.getName());
            guildPref = GuildPreferenceEntity.getDefault(guild);
            guildPreferenceRepository.save(guildPref);
        }
        else
            guildPref = guildPrefList.get(0);
        return guildPref;
    }
}
