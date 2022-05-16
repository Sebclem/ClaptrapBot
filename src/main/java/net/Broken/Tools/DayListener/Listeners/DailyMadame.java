package net.Broken.Tools.DayListener.Listeners;

import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.MainBot;
import net.Broken.SlashCommands.Over18.Madame;
import net.Broken.SpringContext;
import net.Broken.Tools.DayListener.NewDayListener;
import net.Broken.Tools.FindContentOnWebPage;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

/**
 * Daily Listener for DailyMadame
 */
public class DailyMadame implements NewDayListener {

    private GuildPreferenceRepository guildPreferenceRepository;
    private Logger logger = LogManager.getLogger();

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
                List<GuildPreferenceEntity> guildPref = guildPreferenceRepository.findByGuildId(guild.getId());
                if (guildPref.size() > 0 && guildPref.get(0).isDailyMadame()) {
                    for (TextChannel iterator : guild.getTextChannels()) {
                        if (iterator.isNSFW() && iterator.canTalk()) {
                            chanel = iterator;
                            logger.debug("break: " + chanel.getName());
                            break;
                        }
                    }
                    if (chanel != null) {

                        chanel.sendMessage("Madame of the day :kissing_heart: \n" + imgUrl).queue();


                    } else {
                        logger.info("No NSFW chanel found for " + guild.getName() + ", ignoring it!");
                    }
                }


            }
        } catch (IOException e) {
            logger.catching(e);
        }
    }
}
