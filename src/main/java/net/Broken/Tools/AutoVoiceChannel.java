package net.Broken.Tools;


import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static net.Broken.MainBot.jda;

public class AutoVoiceChannel {
    private static final HashMap<String, AutoVoiceChannel> INSTANCE_MAP = new HashMap<>();
    private final Logger logger = LogManager.getLogger();

    private final String guildID;
    private final HashMap<Integer, String> createdChannels = new HashMap<>();

    public static AutoVoiceChannel getInstance(Guild guild) {
        if (INSTANCE_MAP.get(guild.getId()) == null) {
            INSTANCE_MAP.put(guild.getId(), new AutoVoiceChannel(guild));
        }
        return INSTANCE_MAP.get(guild.getId());
    }

    public AutoVoiceChannel(Guild guild) {
        this.guildID = guild.getId();
    }

    public void join(VoiceChannel voiceChannel) {
        Guild guild = jda.getGuildById(guildID);
        if (guild == null)
            return;
        GuildPreferenceEntity pref = SettingsUtils.getInstance().getPreference(guild);
        if (pref.isAutoVoice() && voiceChannel.getId().equals(pref.getAutoVoiceChannelID())) {
            logger.info("Creating new voice channel for Guild : " + guild.getName());
            VoiceChannel newChannel = voiceChannel.createCopy().complete();
            int next = getNextNumber();
            String title = pref.getAutoVoiceChannelTitle();
            if (title.isEmpty()){
                title = "Voice @count";
            }
            title = title.replace("@count", Integer.toString(next));
            newChannel.getManager().setName(title).setPosition(voiceChannel.getPosition()).queue();

            createdChannels.put(next, newChannel.getId());
            moveMembers(voiceChannel.getMembers(), newChannel);
        }

    }

    public void leave(VoiceChannel voiceChannel) {
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

    private void moveMembers(List<Member> members, VoiceChannel destination) {
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
