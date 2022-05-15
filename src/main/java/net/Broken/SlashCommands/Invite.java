package net.Broken.SlashCommands;

import net.Broken.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

public class Invite implements SlashCommand {
    @Override
    public void action(SlashCommandEvent event) {
        event.reply(event.getJDA().setRequiredScopes("bot", "applications.commands").getInviteUrl(Permission.ADMINISTRATOR)).setEphemeral(true).queue();
    }

    @Override
    public String getDescription() {
        return "Get the link to invite this bot to your server";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return null;
    }

    @Override
    public boolean isBotAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }
}
