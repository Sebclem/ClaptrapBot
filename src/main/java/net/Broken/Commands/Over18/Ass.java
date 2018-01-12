package net.Broken.Commands.Over18;

import net.Broken.Outils.Command.NumberedCommande;
import org.apache.logging.log4j.LogManager;

/**
 * Created by seb65 on 07/11/2016.
 */
public class Ass  extends NumberedCommande {

    public Ass() {
        super(LogManager.getLogger(), "http://les400culs.com/","featured-img","img");
    }

    @Override
    public String toString() {
        return "Ass";
    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }
}
