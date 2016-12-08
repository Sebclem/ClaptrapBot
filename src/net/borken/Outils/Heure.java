package net.borken.Outils;

import java.util.GregorianCalendar;

/**
 * Created by seb65 on 21/10/2016.
 */
public class Heure {


    public int getHeure()
    {
        GregorianCalendar calendrier = new GregorianCalendar();
        return calendrier.get(GregorianCalendar.HOUR_OF_DAY);
    }
    public int getMin(){
        GregorianCalendar calendrier = new GregorianCalendar();
        return calendrier.get(GregorianCalendar.MINUTE);
    }
    public int getSeg(){
        GregorianCalendar calendrier = new GregorianCalendar();
        return calendrier.get(GregorianCalendar.SECOND);

    }
    public String getString(){
        String str=String.format("%02d",this.getHeure())+":"+String.format("%02d",this.getMin())+":"+String.format("%02d",this.getSeg());
        return str;
    }

}
