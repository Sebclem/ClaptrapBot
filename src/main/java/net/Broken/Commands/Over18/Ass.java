package net.Broken.Commands.Over18;

import net.Broken.Tools.Command.NoDev;
import net.Broken.Tools.Command.NumberedCommande;
import org.apache.logging.log4j.LogManager;

/**
 * Ass command, return random picture from les400culs.com
 */

@NoDev()
public class Ass  extends NumberedCommande {

    public Ass() {
        super(LogManager.getLogger(), "http://les400culs.com/","-2/","featured-img","img");
    }

    @Override
    public String toString() {
        return "Ass";
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
        return true;
    }
}
