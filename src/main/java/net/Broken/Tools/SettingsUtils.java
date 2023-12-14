package net.Broken.Tools;

import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.SpringContext;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

public class SettingsUtils {

    private static SettingsUtils INSTANCE;
    private final Logger logger = LogManager.getLogger();
    private final GuildPreferenceRepository guildPreferenceRepository;

    private SettingsUtils() {
        ApplicationContext context = SpringContext.getAppContext();
        guildPreferenceRepository = (GuildPreferenceRepository) context.getBean("guildPreferenceRepository");
    }

    public static SettingsUtils getInstance() {
        return (INSTANCE == null) ? new SettingsUtils() : INSTANCE;
    }

    public GuildPreferenceEntity getPreference(Guild guild) {
        return guildPreferenceRepository.findByGuildId(guild.getId()).orElseGet(() -> {
            logger.info("Generate default pref for {}", guild.getName());
            return guildPreferenceRepository.save(GuildPreferenceEntity.getDefault(guild.getId()));
        });
    }
}
