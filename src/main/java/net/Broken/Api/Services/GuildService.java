package net.Broken.Api.Services;

import net.Broken.Api.Data.Guild;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.MainBot;
import net.Broken.Tools.CacheTools;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GuildService {
    public List<Guild> getMutualGuilds(UserEntity user){
        User discordUser = CacheTools.getJdaUser(user);
        List<net.dv8tion.jda.api.entities.Guild> mutualGuilds = discordUser.getMutualGuilds();
        List<Guild> guildList = new ArrayList<>();

        for (net.dv8tion.jda.api.entities.Guild guild : mutualGuilds){
            guildList.add(new Guild(guild.getId(), guild.getName(), guild.getIconUrl()));
        }
        return guildList;
    }

}
