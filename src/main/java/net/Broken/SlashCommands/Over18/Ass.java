package net.Broken.SlashCommands.Over18;

import org.apache.logging.log4j.LogManager;

import net.Broken.Tools.Command.Ignore;
import net.Broken.Tools.Command.NoDev;
import net.Broken.Tools.Command.NumberedSlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

@NoDev
@Ignore
public class Ass extends NumberedSlashCommand {

    public Ass() {
        super(LogManager.getLogger(), "http://les400culs.com/", "-2/", "featured-img", "img");
    }

    @Override
    public String getDescription() {
        return "Return random image from les400culs.com";
    }

    @Override
    public boolean isBotAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return true;
    }

    @Override
    public boolean isPrivateUsable() {
        return true;
    }

    @Override
    public DefaultMemberPermissions getDefaultPermissions() {
        return DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND);
    }
}
