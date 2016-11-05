package com.dmitrymalkovich.android.githubanalytics.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeUtils {
    public static long today() {
        Calendar c = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis() / 1000 * 1000;
    }

    public static long yesterday() {
        Calendar c = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day - 1, 0, 0, 0);
        return calendar.getTimeInMillis() / 1000 * 1000;
    }
}
