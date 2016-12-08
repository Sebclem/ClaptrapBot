package net.borken.Outils;

import enigma.console.TextAttributes;
import net.borken.MainBot;

import java.awt.*;

/**
 * Created by seb65 on 24/10/2016.
 */
public class Entete {
    public String get(String type,String fonction){
        if (type.equals("ERREUR"))
        {
            MainBot.txtColor = new TextAttributes(Color.red, Color.black);
            MainBot.s_console.setTextAttributes(MainBot.txtColor);
        }
        else
        {
            MainBot.txtColor = new TextAttributes(Color.blue, Color.black);
            MainBot.s_console.setTextAttributes(MainBot.txtColor);
        }
        return "["+new Heure().getString()+"] ["+type+"] ["+fonction+"]: ";

    }
}
