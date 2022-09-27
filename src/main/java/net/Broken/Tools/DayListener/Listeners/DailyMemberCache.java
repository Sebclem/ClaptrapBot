package net.Broken.Tools.DayListener.Listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.Broken.MainBot;
import net.Broken.Tools.DayListener.NewDayListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class DailyMemberCache implements NewDayListener {

  Logger logger = LogManager.getLogger();

  @Override
  public void onNewDay() {
    JDA jda = MainBot.jda;
    logger.info("[DAILY] Connected on " + jda.getGuilds().size() + " Guilds:");
    for (Guild server : jda.getGuilds()) {
      server.loadMembers().get();
      logger.info("... " + server.getName() + " " + server.getMembers().size() + " Members");
    }

  }

}
