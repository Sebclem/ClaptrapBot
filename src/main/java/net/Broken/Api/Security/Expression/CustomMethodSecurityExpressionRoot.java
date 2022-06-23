package net.Broken.Api.Security.Expression;

import net.Broken.Api.Data.Music.Connect;
import net.Broken.Api.Security.Data.JwtPrincipal;
import net.Broken.MainBot;
import net.Broken.Tools.CacheTools;
import net.Broken.Audio.GuildAudioWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionRoot
        extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {
    private Object filterObject;
    private Object returnObject;
    /**
     * Creates a new instance
     *
     * @param authentication the {@link Authentication} to use. Cannot be null.
     */
    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    public boolean isInGuild(String guildId){
        JwtPrincipal jwtPrincipal = (JwtPrincipal) authentication.getPrincipal();
        Guild guild = MainBot.jda.getGuildById(guildId);
        return CacheTools.getJdaUser(jwtPrincipal.user()).getMutualGuilds().contains(guild);
    }

    public boolean canManageGuild(String guildId){
        JwtPrincipal jwtPrincipal = (JwtPrincipal) authentication.getPrincipal();
        Member member = MainBot.jda.getGuildById(guildId).getMemberById(jwtPrincipal.user().getDiscordId());
        return member.hasPermission(
                Permission.MANAGE_SERVER,
                Permission.MANAGE_PERMISSIONS,
                Permission.MANAGE_CHANNEL
        );
    }

    public boolean canInteractWithVoiceChannel(String guildId, Connect connectPayload){
        JwtPrincipal jwtPrincipal = (JwtPrincipal) authentication.getPrincipal();
        Guild guild = MainBot.jda.getGuildById(guildId);
        Member member = guild.getMemberById(jwtPrincipal.user().getDiscordId());
        VoiceChannel channel = guild.getVoiceChannelById(connectPayload.channelId());
        if( channel == null){
            return false;
        }

       return  (member.hasPermission(channel, Permission.VOICE_CONNECT)
                || member.getVoiceState() != null
                && member.getVoiceState().getChannel() == channel)
                && member.hasPermission(channel, Permission.VOICE_SPEAK);
    }

    public boolean canInteractWithVoiceChannel(String guildId) {
        JwtPrincipal jwtPrincipal = (JwtPrincipal) authentication.getPrincipal();
        Guild guild = MainBot.jda.getGuildById(guildId);
        GuildAudioWrapper guildAudioWrapper = GuildAudioWrapper.getInstance(guild);
        VoiceChannel channel = guild.getAudioManager().getConnectedChannel();

        if (channel == null) {
            return false;
        }

        Member member = guild.getMemberById(jwtPrincipal.user().getDiscordId());
        return (member.hasPermission(channel, Permission.VOICE_CONNECT)
                || member.getVoiceState() != null
                && member.getVoiceState().getChannel() == channel)
                && member.hasPermission(channel, Permission.VOICE_SPEAK);
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }
}
