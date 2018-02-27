package net.Broken.Commands.Over18;

import net.Broken.Tools.Command.NumberedCommande;
import org.apache.logging.log4j.LogManager;

/**
 * Created by seb65 on 07/11/2016.
 */
public class Boobs extends NumberedCommande {

    public Boobs() {
        super(LogManager.getLogger(), "http://lesaintdesseins.fr/","featured-img","img");
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

    @Override
    public boolean isNSFW() {
        return true;
    }
}
