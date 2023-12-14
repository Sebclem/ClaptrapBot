package net.Broken.SlashCommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.Broken.SlashCommand;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;


public class Clear implements SlashCommand {
    @Override
    public void action(SlashCommandInteractionEvent event) {
        if (event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            event.reply(":white_check_mark: Done").setEphemeral(true).queue();
            long n = event.getOption("n").getAsLong();
            MessageChannel chanel = event.getChannel();
            chanel.getIterableHistory().takeAsync((int) n).thenAccept(chanel::purgeMessages);
        } else {
            MessageCreateData message = new MessageCreateBuilder().setEmbeds(EmbedMessageUtils.getFlushError("You are not a supreme being, you cannot do that !")).build();
            event.reply(message).setEphemeral(true).queue();
        }
    }

    @Override
    public String getDescription() {
        return "Clear the last [n] message(s)";
    }

    @Override
    public List<OptionData> getOptions() {
        ArrayList<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER, "n", "The number of message(s) to clear", true));
        return options;
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
        return DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE);
    }

    
}
