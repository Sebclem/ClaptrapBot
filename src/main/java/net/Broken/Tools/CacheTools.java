package net.Broken.Tools;

import net.Broken.DB.Entity.UserEntity;
import net.Broken.MainBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class CacheTools {
    private static Logger logger = LogManager.getLogger();

    public static void loadAllGuildMembers() {
        List<Guild> guilds = MainBot.jda.getGuilds();
        for (Guild guild : guilds) {
            guild.loadMembers().get();
        }
    }

    public static User getJdaUser(UserEntity userEntity) {
        User user = MainBot.jda.getUserById(userEntity.getJdaId());
        if (user == null) {
            logger.debug("User cache not found for " + userEntity.getName() + ", fetching user.");
            user = MainBot.jda.retrieveUserById(userEntity.getJdaId()).complete();
        }
        return user;
    }
}
