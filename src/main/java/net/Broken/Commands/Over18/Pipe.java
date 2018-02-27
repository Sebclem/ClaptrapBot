package net.Broken.Commands.Over18;

import net.Broken.Tools.Command.NumberedCommande;
import org.apache.logging.log4j.LogManager;

/**
 * Created by seb65 on 07/11/2016.
 */
public class Pipe extends NumberedCommande {
    public Pipe() {
        super(LogManager.getLogger(), "http://feelation.com/","featured-img","img");
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
