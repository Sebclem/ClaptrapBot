package net.Broken.Tools.DayListener.Listeners;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import net.Broken.MainBot;
import net.Broken.SpringContext;
import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.SlashCommands.Over18.Madame;
import net.Broken.Tools.FindContentOnWebPage;
import net.Broken.Tools.DayListener.NewDayListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/**
 * Daily Listener for DailyMadame
 */
public class DailyMadame implements NewDayListener {

    private final GuildPreferenceRepository guildPreferenceRepository;
    private final Logger logger = LogManager.getLogger();

    public DailyMadame() {
        ApplicationContext context = SpringContext.getAppContext();
        guildPreferenceRepository = (GuildPreferenceRepository) context.getBean("guildPreferenceRepository");
    }

    @Override
    public void onNewDay() {

        List<Guild> guilds = MainBot.jda.getGuilds();

        String imgUrl;
        try {
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            if (day != Calendar.MONDAY && day != Calendar.SUNDAY) {
                LocalDate now = LocalDate.now().minusDays(1);
                String date = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(now);
                String url = "https://www.bonjourmadame.fr/" + date + "/";
                imgUrl = FindContentOnWebPage.doYourJob(url, "post-content", "img");
            } else {
                Madame command = (Madame) MainBot.slashCommands.get("madame");
                imgUrl = command.poll();
            }

            for (Guild guild : guilds) {
                TextChannel chanel = null;
                logger.debug(guild.getName());
                Optional<GuildPreferenceEntity> guildPref = guildPreferenceRepository.findByGuildId(guild.getId());
                if (guildPref.isPresent() && guildPref.get().isDailyMadame()) {
                    for (TextChannel iterator : guild.getTextChannels()) {
                        if (iterator.isNSFW() && iterator.canTalk()) {
                            chanel = iterator;
                            logger.debug("break: {}", chanel.getName());
                            break;
                        }
                    }
                    if (chanel != null) {
                        chanel.sendMessage("Madame of the day :kissing_heart: \n" + imgUrl).queue();
                    } else {
                        logger.info("No NSFW chanel found for {}, ignoring it!", guild.getName());
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.catching(e);
        }
    }
}
