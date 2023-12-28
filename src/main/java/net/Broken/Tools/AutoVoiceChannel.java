package net.Broken.Tools;

import static net.Broken.MainBot.jda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.requests.RestAction;

public class AutoVoiceChannel {
    private static final HashMap<String, AutoVoiceChannel> INSTANCE_MAP = new HashMap<>();
    private final Logger logger = LogManager.getLogger();

    private final String guildID;
    private final HashMap<Integer, String> createdChannels = new HashMap<>();

    public AutoVoiceChannel(Guild guild) {
        this.guildID = guild.getId();
    }

    public static AutoVoiceChannel getInstance(Guild guild) {
        if (INSTANCE_MAP.get(guild.getId()) == null) {
            INSTANCE_MAP.put(guild.getId(), new AutoVoiceChannel(guild));
        }
        return INSTANCE_MAP.get(guild.getId());
    }

    public void join(AudioChannel voiceChannel) {
        Guild guild = jda.getGuildById(guildID);
        if (guild == null)
            return;
        GuildPreferenceEntity pref = SettingsUtils.getInstance().getPreference(guild);
        if (pref.isAutoVoice() && voiceChannel.getId().equals(pref.getAutoVoiceChannelID())) {
            logger.info("Creating new voice channel for Guild : {}", guild.getName());
            int next = getNextNumber();
            String title = pref.getAutoVoiceChannelTitle();
            if (title.isEmpty()) {
                title = "Voice @count";
            }
            title = title.replace("@count", Integer.toString(next));
            voiceChannel.createCopy().setName(title).setPosition(voiceChannel.getPosition()).queue(newChannel -> {
                moveMembers(voiceChannel.getMembers(), (AudioChannel) newChannel);
                createdChannels.put(next, newChannel.getId());
            });
        }

    }

    public void leave(AudioChannel voiceChannel) {
        if (voiceChannel.getMembers().isEmpty()) {
            String id = voiceChannel.getId();
            for (Map.Entry<Integer, String> entry : createdChannels.entrySet()) {
                if (entry.getValue().equals(id)) {
                    logger.info("Auto created channel is empty, deleting it ...");
                    voiceChannel.delete().reason("Auto-remove empty voice channel").queue();
                    createdChannels.remove(entry.getKey());
                }
            }
        }

    }

    private int getNextNumber() {
        Set<Integer> keys = createdChannels.keySet();
        for (int next = 1; next < 999; next++) {
            if (!keys.contains(next))
                return next;
        }
        return 999;
    }

    private void moveMembers(List<Member> members, AudioChannel destination) {
        logger.debug("Moving Members to new voice channel...");
        RestAction<Void> restAction = null;
        for (Member member : members) {
            if (restAction == null)
                restAction = destination.getGuild().moveVoiceMember(member, destination);
            restAction = restAction.and(destination.getGuild().moveVoiceMember(member, destination));
        }
        if (restAction != null) {
            restAction.queue();
        }
    }

}
