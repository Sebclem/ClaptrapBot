package net.Broken.Commands.Over18;

import net.Broken.Tools.Command.NoDev;
import net.Broken.Tools.Command.NumberedCommande;
import org.apache.logging.log4j.LogManager;

/**
 * Ass command, return random picture from feelation.com
 */
@NoDev()
public class Pipe extends NumberedCommande {
    public Pipe() {
        super(LogManager.getLogger(), "http://feelation.com/","-2/","featured-img","img");
    }

    @Override
    public String toString() {
        return "Pipe";
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
