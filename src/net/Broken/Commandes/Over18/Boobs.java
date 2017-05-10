package net.Broken.Commandes.Over18;

import net.Broken.Commandes.NumberedCommande;
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
}
