package net.Broken.Api.Security.Expression;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import net.Broken.MainBot;
import net.Broken.Api.Data.Music.Connect;
import net.Broken.Api.Security.Data.JwtPrincipal;
import net.Broken.Tools.CacheTools;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;

@Service
public class WebSecurity {
    public boolean isInGuild(String guildId) {

        JwtPrincipal jwtPrincipal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Guild guild = MainBot.jda.getGuildById(guildId);
        return CacheTools.getJdaUser(jwtPrincipal.user()).getMutualGuilds().contains(guild);
    }

    public boolean canManageGuild(String guildId) {
        JwtPrincipal jwtPrincipal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Member member = MainBot.jda.getGuildById(guildId).getMemberById(jwtPrincipal.user().getDiscordId());
        return member.hasPermission(
                Permission.MANAGE_SERVER,
                Permission.MANAGE_PERMISSIONS,
                Permission.MANAGE_CHANNEL);
    }

    public boolean canInteractWithVoiceChannel(String guildId, Connect connectPayload) {
        JwtPrincipal jwtPrincipal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Guild guild = MainBot.jda.getGuildById(guildId);
        Member member = guild.getMemberById(jwtPrincipal.user().getDiscordId());
        VoiceChannel channel = guild.getVoiceChannelById(connectPayload.channelId());
        if (channel == null) {
            return false;
        }

        return (member.hasPermission(channel, Permission.VOICE_CONNECT)
                || member.getVoiceState() != null
                        && member.getVoiceState().getChannel() == channel)
                && member.hasPermission(channel, Permission.VOICE_SPEAK);
    }

    public boolean canInteractWithVoiceChannel(String guildId) {
        JwtPrincipal jwtPrincipal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Guild guild = MainBot.jda.getGuildById(guildId);
        AudioChannelUnion channel = guild.getAudioManager().getConnectedChannel();

        if (channel == null) {
            return false;
        }

        Member member = guild.getMemberById(jwtPrincipal.user().getDiscordId());
        return (member.hasPermission(channel, Permission.VOICE_CONNECT)
                || member.getVoiceState() != null
                        && member.getVoiceState().getChannel() == channel)
                && member.hasPermission(channel, Permission.VOICE_SPEAK);
    }

}
