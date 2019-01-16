package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.Tools.UserManager.Stats.UserStatsUtils;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Rank implements Commande {
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        UserStatsUtils userStats = UserStatsUtils.getINSTANCE();
        MessageEmbed msg = userStats.getRankMessage(event.getMember());
        event.getTextChannel().sendMessage(msg).queue();
    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public boolean isAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }
}
