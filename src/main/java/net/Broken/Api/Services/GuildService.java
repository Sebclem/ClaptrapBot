package net.Broken.Api.Services;

import net.Broken.Api.Data.Guild.Guild;
import net.Broken.Api.Data.Guild.Channel;
import net.Broken.Api.Data.Guild.Role;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.MainBot;
import net.Broken.Tools.CacheTools;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GuildService {
    public List<Guild> getMutualGuilds(UserEntity user) {
        User discordUser = MainBot.jda.retrieveUserById(user.getDiscordId()).complete();
        List<net.dv8tion.jda.api.entities.Guild> mutualGuilds = discordUser.getMutualGuilds();
        List<Guild> guildList = new ArrayList<>();

        for (net.dv8tion.jda.api.entities.Guild guild : mutualGuilds) {
            boolean canManage = guild.getMember(discordUser).hasPermission(
                    Permission.MANAGE_SERVER,
                    Permission.MANAGE_PERMISSIONS,
                    Permission.MANAGE_CHANNEL
            );
            guildList.add(new Guild(guild.getId(), guild.getName(), guild.getIconUrl(), canManage));
        }
        return guildList;
    }

    public List<Channel> getVoiceChannel(String guildId, String userId) {
        net.dv8tion.jda.api.entities.Guild guild = MainBot.jda.getGuildById(guildId);
        Member member = guild.getMemberById(userId);
        List<Channel> voiceChannels = new ArrayList<>();
        for (net.dv8tion.jda.api.entities.VoiceChannel voiceChannel : guild.getVoiceChannels()) {
            if (member.hasPermission(voiceChannel, Permission.VIEW_CHANNEL)) {
                voiceChannels.add(new Channel(voiceChannel.getId(), voiceChannel.getName()));
            }
        }
        return voiceChannels;
    }

    public List<Channel> getTextChannel(String guildId, String userId) {
        net.dv8tion.jda.api.entities.Guild guild = MainBot.jda.getGuildById(guildId);
        Member member = guild.getMemberById(userId);
        List<Channel> voiceChannels = new ArrayList<>();
        for (net.dv8tion.jda.api.entities.TextChannel textChannel : guild.getTextChannels()) {
            if (member.hasPermission(textChannel, Permission.VIEW_CHANNEL)) {
                voiceChannels.add(new Channel(textChannel.getId(), textChannel.getName()));
            }
        }
        return voiceChannels;
    }

    public List<Role> getRole(String guildId) {
        net.dv8tion.jda.api.entities.Guild guild = MainBot.jda.getGuildById(guildId);
        List<Role> roles = new ArrayList<>();
        for (net.dv8tion.jda.api.entities.Role role : guild.getRoles()) {
            if (!role.isManaged()) {
                roles.add(new Role(role.getId(), role.getName()));
            }
        }
        return roles;
    }

}
