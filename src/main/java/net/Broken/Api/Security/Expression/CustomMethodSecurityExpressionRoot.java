package net.Broken.Api.Security.Expression;

import net.Broken.Api.Security.Data.JwtPrincipal;
import net.Broken.MainBot;
import net.Broken.Tools.CacheTools;
import net.dv8tion.jda.api.entities.Guild;
import okhttp3.Cache;
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
