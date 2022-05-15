package net.Broken.Tools.DayListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Day change listener
 */
public class DayListener extends Thread {
    private static DayListener INSTANCE = new DayListener();
    private Logger logger = LogManager.getLogger();
    private Calendar calendar;
    private int previousDay;
    /**
     * List of listeners to need to be triggered
     */
    private ArrayList<NewDayListener> listeners = new ArrayList<>();


    /**
     * Default private constructor
     */
    private DayListener() {
        calendar = Calendar.getInstance();
        previousDay = calendar.get(GregorianCalendar.DAY_OF_MONTH);
    }


    /**
     * Singleton
     *
     * @return Unique DayListener instance.
     */
    public static DayListener getInstance() {
        return INSTANCE;
    }

    /**
     * Add Listener who will be triggered
     *
     * @param listener
     */
    public void addListener(NewDayListener listener) {
        listeners.add(listener);
    }

    /**
     * Trigger all listeners
     */
    public void trigger() {
        for (NewDayListener listener : listeners) {
            try {
                listener.onNewDay();
            } catch (Exception ex) {
                logger.error("Fail to execute day change !");
                logger.catching(ex);
            }
        }
    }


    /**
     * Thread loop
     */
    @Override
    public void run() {
        while (true) {
            calendar = Calendar.getInstance();
            if (calendar.get(GregorianCalendar.DAY_OF_MONTH) != previousDay) {
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