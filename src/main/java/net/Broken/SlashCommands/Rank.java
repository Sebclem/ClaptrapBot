package net.Broken.SlashCommands;

import net.Broken.BotConfigLoader;
import net.Broken.SlashCommand;
import net.Broken.SpringContext;
import net.Broken.Tools.UserManager.Stats.UserStatsUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Collections;
import java.util.List;

public class Rank implements SlashCommand {
    @Override
    public void action(SlashCommandInteractionEvent event) {
        String url = SpringContext.getAppContext().getBean(BotConfigLoader.class).url();
        event.deferReply().queue();
        UserStatsUtils userStats = UserStatsUtils.getINSTANCE();
        MessageEmbed messageEmbed = userStats.getRankMessage(event.getMember());
        event.getHook().sendMessageEmbeds(messageEmbed).addActionRow(
                Button.link("https://" + url + "/rank", "More stats")
        ).queue();
    }

    @Override
    public String getDescription() {
        return "Get the top 5 ranking of this server";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return Collections.emptyList();
    }

    @Override
    public boolean isBotAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public DefaultMemberPermissions getDefaultPermissions() {
        return DefaultMemberPermissions.ENABLED;
    }
}
