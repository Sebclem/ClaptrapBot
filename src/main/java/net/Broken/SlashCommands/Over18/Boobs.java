package net.Broken.SlashCommands.Over18;

import net.Broken.Tools.Command.Ignore;
import net.Broken.Tools.Command.NoDev;
import net.Broken.Tools.Command.NumberedSlashCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@NoDev
@Ignore
public class Boobs extends NumberedSlashCommand {
    Logger logger = LogManager.getLogger();

    public Boobs() {
        super(LogManager.getLogger(), "http://lesaintdesseins.fr/", "-2/", "featured-img", "img");
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
    public boolean isDisableByDefault() {
        return true;
    }
}
