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
    private static final DayListener INSTANCE = new DayListener();
    private final Logger logger = LogManager.getLogger();
    private Calendar calendar;
    private int previousDay;

    private final ArrayList<NewDayListener> listeners = new ArrayList<>();

    private DayListener() {
        calendar = Calendar.getInstance();
        previousDay = calendar.get(GregorianCalendar.DAY_OF_MONTH);
    }

    public static DayListener getInstance() {
        return INSTANCE;
    }

    public void addListener(NewDayListener listener) {
        listeners.add(listener);
    }

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