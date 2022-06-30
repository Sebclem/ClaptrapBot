package net.Broken.Tools;

import net.Broken.DB.Entity.UserEntity;
import net.Broken.MainBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class CacheTools {
    private static final Logger logger = LogManager.getLogger();

    public static void loadAllGuildMembers() {
        List<Guild> guilds = MainBot.jda.getGuilds();
        for (Guild guild : guilds) {
            guild.loadMembers().get();
        }
    }

    public static User getJdaUser(UserEntity userEntity) {
        User user = MainBot.jda.getUserById(userEntity.getDiscordId());
        if (user == null) {
            logger.debug("User cache not found for " + userEntity.getUsername() + ", fetching user.");
            user = MainBot.jda.retrieveUserById(userEntity.getDiscordId()).complete();
        }
        return user;
    }
}
