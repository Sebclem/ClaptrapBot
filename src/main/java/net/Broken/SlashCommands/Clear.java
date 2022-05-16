package net.Broken.SlashCommands;

import net.Broken.SlashCommand;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;

public class Clear implements SlashCommand {
    @Override
    public void action(SlashCommandEvent event) {
        if (event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            event.reply(":white_check_mark: Done").setEphemeral(true).queue();
            long n = event.getOption("n").getAsLong();
            MessageChannel chanel = event.getChannel();
            chanel.getIterableHistory().takeAsync((int) n).thenAccept(chanel::purgeMessages);
        } else {
            Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getFlushError("You are not a supreme being, you cannot do that !")).build();
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

    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public boolean isDisableByDefault() {
        return true;
    }
}
