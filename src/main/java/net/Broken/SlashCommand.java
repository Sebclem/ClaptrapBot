package net.Broken;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

/**
 * Interface that define command structure.
 */
public interface SlashCommand {
    void action(SlashCommandInteractionEvent event);

    String getDescription();

    List<OptionData> getOptions();

    List<SubcommandData> getSubcommands();

    /**
     * Determines if the command is usable only by bot level admin user
     *
     * @return boolean
     */
    boolean isBotAdminCmd();

    /**
     * Determines if the command is only usable on NSFW channels
     *
     * @return boolean
     */
    boolean isNSFW();

    boolean isPrivateUsable();


    boolean isDisableByDefault();

}
