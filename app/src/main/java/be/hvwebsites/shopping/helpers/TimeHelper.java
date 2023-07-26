package be.hvwebsites.shopping.helpers;

import java.util.Calendar;

public class TimeHelper {
    private final int hours;
    private final int minutes;
    private final int dayInWeek;

    public TimeHelper() {
        Calendar calendarDate = Calendar.getInstance();
        // Bepaal hoe laat het is
        hours = calendarDate.get(Calendar.HOUR_OF_DAY);
        int amPm = calendarDate.get(Calendar.AM_PM);
        minutes = calendarDate.get(Calendar.MINUTE);
        dayInWeek = calendarDate.get(Calendar.DAY_OF_WEEK);
        boolean debug = true;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getDayInWeek() {
        return dayInWeek;
    }
}
