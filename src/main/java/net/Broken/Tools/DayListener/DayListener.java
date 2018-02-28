package net.Broken.Tools.DayListener;
import net.Broken.Commands.Spam;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Day change listener
 */
public class DayListener extends Thread {
    private Logger logger = LogManager.getLogger();
    private Calendar calendar;
    private int previousDay;

    /**
     * List of listeners to need to be triggered
     */
    private ArrayList<NewDayListener> listeners = new ArrayList<>();
    private static DayListener INSTANCE = new DayListener();


    /**
     * Default private constructor
     */
    private DayListener() {
        calendar = Calendar.getInstance();
        previousDay = calendar.get(GregorianCalendar.DAY_OF_MONTH);
    }


    /**
     * Singleton
     * @return Unique DayListener instance.
     */
    public static DayListener getInstance()
    {
        return INSTANCE;
    }

    /**
     * Add Listener who will be triggered
     * @param listener
     */
    public void addListener(NewDayListener listener){
        listeners.add(listener);
    }

    /**
     * Trigger all listeners
     */
    public void trigger(){
        for(NewDayListener listener : listeners){
            listener.onNewDay();
        }
    }


    /**
     * Thread loop
     */
    @Override
    public void run() {
        while(true)
        {   calendar = Calendar.getInstance();
            logger.debug(calendar.get(GregorianCalendar.DAY_OF_MONTH)+"/"+previousDay);
            if(calendar.get(GregorianCalendar.DAY_OF_MONTH) != previousDay)
            {
                LogManager.getLogger().info("New day triggered!");
                trigger();
                previousDay = calendar.get(GregorianCalendar.DAY_OF_MONTH);
            }
            try {
                sleep(600000);
            } catch (InterruptedException e) {
                LogManager.getLogger().catching(e);
            }
        }
    }
}