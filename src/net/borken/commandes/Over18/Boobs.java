package net.borken.commandes.Over18;

import net.borken.commandes.NumberedCommande;
import org.apache.logging.log4j.LogManager;

/**
 * Created by seb65 on 07/11/2016.
 */
public class Boobs extends NumberedCommande {

    public Boobs() {
        super(LogManager.getLogger(), "http://lesaintdesseins.fr/");
    }
}
