package net.Broken.audio;

import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.Tools.SettingsUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Used to find general voice channels
 */
public class GetVoiceChanels {
    private static Logger logger = LogManager.getLogger();

    /**
     * Search for 🤖 char on category name, if this category can't be find, auto create it
     * @param guild Current guild
     * @return General Category
     */
    public static List<VoiceChannel> find(Guild guild){
        SettingsUtils settingsUtils = SettingsUtils.getInstance();
        GuildPreferenceEntity pref = settingsUtils.getPreference(guild);

        ArrayList<VoiceChannel> list = new ArrayList<>();
        List<String> chanels = pref.getVisibleVoiceChanel();

        if(chanels == null || chanels.size() == 0){
            pref = settingsUtils.setDefaultVoiceChannels(guild, pref);
            chanels = pref.getVisibleVoiceChanel();
        }

        boolean needClean = false;
        for(String prefChan : chanels){
            VoiceChannel voice = guild.getVoiceChannelById(prefChan);
            if(voice != null)
                list.add(voice);
            else
                needClean = true;
        }

        if(needClean){
            logger.debug("Need Clean.");
            settingsUtils.cleanVisibleVoicePref(guild, pref);

        }

        list.sort(Comparator.comparingInt(GuildChannel::getPositionRaw));

        return list;

    }

}
