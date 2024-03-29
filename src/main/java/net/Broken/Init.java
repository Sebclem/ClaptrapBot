package net.Broken;

import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.Tools.Command.SlashCommandLoader;
import net.Broken.Tools.DayListener.DayListener;
import net.Broken.Tools.DayListener.Listeners.DailyMadame;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

public class Init {
    static private final Logger logger = LogManager.getLogger();

    private Init() {
    }

    static JDA initJda(BotConfigLoader config) {
        logger.info("-----------------------INIT-----------------------");
        if (config == null) {
            logger.fatal("Please enter bot token as an argument.");
            return null;
        } else {
            try {
                logger.info("Connecting to Discord api...");

                JDA jda = JDABuilder
                        .createDefault(config.token())
                        .enableIntents(GatewayIntent.GUILD_MEMBERS)
                        .setMemberCachePolicy(MemberCachePolicy.ALL)
                        .setBulkDeleteSplittingEnabled(false)
                        .build();

                jda.awaitReady()
                        .setAutoReconnect(true);
                jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.playing("Loading..."));

                logger.info("Connected on {} Guilds:", jda.getGuilds().size());
                for (Guild server : jda.getGuilds()) {
                    server.loadMembers();
                }
                return jda;
            } catch (InterruptedException e) {
                logger.catching(e);
                return null;
            }
        }
    }

    static void polish(JDA jda, BotConfigLoader config) {
        logger.info("Check database...");
        checkDatabase();
        logger.info("Loading commands");
        SlashCommandLoader.load(config);
        SlashCommandLoader.registerSlashCommands(jda.updateCommands());
        DayListener dayListener = DayListener.getInstance();
        dayListener.addListener(new DailyMadame());
        dayListener.start();
        jda.addEventListener(new BotListener());
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(config.url()));

        logger.info("-----------------------END INIT-----------------------");
    }

    private static void checkDatabase() {
        ApplicationContext context = SpringContext.getAppContext();
        logger.debug("Stats...");

        logger.debug("Guild Prefs...");
        GuildPreferenceRepository guildPreference = context.getBean(GuildPreferenceRepository.class);
        for (GuildPreferenceEntity pref : guildPreference.findAll()) {
            boolean save = false;
            if (pref.getWelcomeMessage() != null && pref.getWelcomeMessage().equals(" ")) {
                pref.setWelcomeMessage(null);
                save = true;
            }
            if (pref.getWelcomeChanelID() != null && pref.getWelcomeChanelID().equals(" ")) {
                pref.setWelcomeChanelID(null);
                save = true;
            }
            if (pref.getWelcomeChanelID() != null && pref.getWelcomeChanelID().equals(" ")) {
                pref.setWelcomeChanelID(null);
                save = true;
            }
            if (pref.getDefaultRoleId() != null && pref.getDefaultRoleId().equals(" ")) {
                pref.setDefaultRoleId(null);
                save = true;
            }
            if (pref.getAutoVoiceChannelID() != null && pref.getAutoVoiceChannelID().equals(" ")) {
                pref.setAutoVoiceChannelID(null);
                save = true;
            }
            if (pref.getAutoVoiceChannelTitle() != null && pref.getAutoVoiceChannelTitle().equals(" ")) {
                pref.setAutoVoiceChannelTitle(null);
                save = true;
            }

            if (save) {
                guildPreference.save(pref);
            }
        }

    }
}
