package net.Broken.audio;

import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.Tools.SettingsUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
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
    private static final Logger logger = LogManager.getLogger();

    /**
     * Search for all voice channel where the user can connect
     *
     * @param guild Current guild
     * @return General Category
     */
    public static List<VoiceChannel> find(Guild guild, Member member) {

        ArrayList<VoiceChannel> list = new ArrayList<>();
        VoiceChannel afk = guild.getAfkChannel();
        GuildPreferenceEntity pref = SettingsUtils.getInstance().getPreference(guild);
        String autoVoice = pref.getAutoVoiceChannelID();
        for (VoiceChannel channel : guild.getVoiceChannels()) {
            if (channel != afk && !channel.getId().equals(autoVoice) && member.getPermissions(channel).contains(Permission.VOICE_CONNECT)) {
                list.add(channel);
            }
        }

        list.sort(Comparator.comparingInt(GuildChannel::getPositionRaw));

        return list;

    }

}
