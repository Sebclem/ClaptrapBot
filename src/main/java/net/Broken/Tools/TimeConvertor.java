package net.Broken.Tools;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimeConvertor {
    static Logger logger = LogManager.getLogger();

    public static ArrayList<String> sToTime(long sec) {
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = (int) (sec % SECONDS_IN_A_MINUTE);
        int totalMinutes = (int) (sec / SECONDS_IN_A_MINUTE);
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;
        ArrayList<String> list = new ArrayList<>();
        list.add(String.valueOf(hours));
        list.add(String.valueOf(minutes));
        list.add(String.valueOf(seconds));

        return list;
    }
}
