package net.Broken.Tools;

import net.Broken.MainBot;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

public class CacheTools {
    public static void loadAllGuildMembers(){
        List<Guild> guilds = MainBot.jda.getGuilds();
        for(Guild guild : guilds){
            guild.loadMembers().get();
        }
    }
}
