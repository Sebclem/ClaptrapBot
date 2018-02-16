package net.Broken.Tools.DayListener;
import net.Broken.Commands.Spam;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by seb65 on 09/11/2016.
 */
public class DayListener extends Thread {
    private GregorianCalendar calendar;
    private int previousDay;
    private ArrayList<NewDayListener> listeners = new ArrayList<>();

    private DayListener() {
        calendar = new GregorianCalendar();
        previousDay = calendar.get(GregorianCalendar.DAY_OF_MONTH);
    }

    private static DayListener INSTANCE = new DayListener();

    public static DayListener getInstance()
    {
        return INSTANCE;
    }

    public void addListener(NewDayListener listener){
        listeners.add(listener);
    }

    public void trigger(){
        for(NewDayListener listener : listeners){
            listener.onNewDay();
        }
    }


    @Override
    public void run() {
        while(true)
        {
            if(calendar.get(GregorianCalendar.DAY_OF_MONTH) != previousDay)
            {
                LogManager.getLogger().info("New day triggered!");
                trigger();
                previousDay = calendar.get(GregorianCalendar.DAY_OF_MONTH);
            }
            try {
                sleep(1800000);
            } catch (InterruptedException e) {
                LogManager.getLogger().catching(e);
            }
        }
    }
}
