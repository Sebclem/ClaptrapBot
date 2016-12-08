package net.borken.Outils;
import net.borken.commandes.Spam;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.util.GregorianCalendar;

/**
 * Created by seb65 on 09/11/2016.
 */
public class DayListener extends Thread {
    GregorianCalendar calendrier;
    int datePrecedente;

    public DayListener() {
        calendrier = new GregorianCalendar();
        datePrecedente=0;
    }

    @Override
    public void run() {
        while(true)
        {
            if(calendrier.get(GregorianCalendar.DAY_OF_MONTH)!=datePrecedente)
            {
                Spam spam=new Spam();
                String str[]={"all"};
                datePrecedente=calendrier.get(GregorianCalendar.DAY_OF_MONTH);
                try {
                    spam.reset(null,str);
                } catch (RateLimitedException e) {
                    e.printStackTrace();
                }
            }
            try {
                sleep(7200000);
            } catch (InterruptedException e) {
                System.err.println(new Entete().get("ERREUR","AutoReset")+e.getCause());
            }
        }
    }
}
