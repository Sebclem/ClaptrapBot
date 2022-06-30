package net.Broken.Api.Services;

import net.Broken.Api.Data.Settings.SettingGroup;
import net.Broken.Api.Data.Settings.Value;
import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.Tools.Settings.SettingDescriptionBuilder;
import net.Broken.Tools.Settings.SettingSaver;
import net.Broken.Tools.Settings.SettingValueBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService {

    public final GuildPreferenceRepository preferenceRepository;
    private final Logger logger = LogManager.getLogger();


    public SettingService(GuildPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    public List<SettingGroup> getSettingDescription() {
        return new SettingDescriptionBuilder().build();
    }


    public List<Value> getValues(String guildId) {
        GuildPreferenceEntity pref = preferenceRepository.findByGuildId(guildId).orElseGet(() -> {
            logger.info("[API] : Generate default guild pref");
            return preferenceRepository.save(GuildPreferenceEntity.getDefault(guildId));
        });
        return new SettingValueBuilder(pref).build();
    }

    public GuildPreferenceEntity saveValue(String guildId, List<Value> values){
        GuildPreferenceEntity pref = preferenceRepository.findByGuildId(guildId).orElseGet(() -> {
            logger.info("[API] : Generate default guild pref");
            return preferenceRepository.save(GuildPreferenceEntity.getDefault(guildId));
        });
        return new SettingSaver(preferenceRepository, pref).save(values);
    }

}
