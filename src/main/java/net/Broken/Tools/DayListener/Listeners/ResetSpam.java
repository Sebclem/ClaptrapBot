package net.Broken.Tools.DayListener.Listeners;

import net.Broken.Commands.Spam;
import net.Broken.Tools.DayListener.NewDayListener;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.util.GregorianCalendar;

public class ResetSpam implements NewDayListener {
    @Override
    public void onNewDay() {
        Spam spam = new Spam();
        String str[] = {"all"};

        try {
            spam.reset(null,str);
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }
    }
}
