package net.Broken.Commands.Over18;

import net.Broken.Tools.Command.NoDev;
import net.Broken.Tools.Command.NumberedCommande;
import org.apache.logging.log4j.LogManager;

/**
 * Boobs command, return random picture from lesaintdesseins.fr
 */
@NoDev()
public class Boobs extends NumberedCommande {

    public Boobs() {
        super(LogManager.getLogger(), "http://lesaintdesseins.fr/", "-2/", "featured-img", "img");
    }

    @Override
    public String toString() {
        return "Boobs";
    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public boolean isAdminCmd() {
        return false;
    }

    /**
     * Determines if the command is usable only by bot level admin user
     *
     * @return boolean
     */
    @Override
    public boolean isBotAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return true;
    }
}
