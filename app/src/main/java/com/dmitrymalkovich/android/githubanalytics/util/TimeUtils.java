package com.dmitrymalkovich.android.githubanalytics.util;

import com.dmitrymalkovich.android.githubapi.core.TimeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TimeUtils {
    public static final String LOG_TAG = TimeUtils.class.getSimpleName();

    public static long today() {
        Calendar c = new GregorianCalendar(TimeConverter.getTimeZone());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis() / 1000 * 1000;
    }

    public static long yesterday() {
        Calendar c = new GregorianCalendar(TimeConverter.getTimeZone());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day - 1, 0, 0, 0);
        return calendar.getTimeInMillis() / 1000 * 1000;
    }

    public static long twoWeeksAgo() {
        Calendar c = new GregorianCalendar(TimeConverter.getTimeZone());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day - 14, 0, 0, 0);
        return calendar.getTimeInMillis() / 1000 * 1000;
    }

    public static long weekAgo() {
        Calendar c = new GregorianCalendar(TimeConverter.getTimeZone());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day - 7, 0, 0, 0);
        return calendar.getTimeInMillis() / 1000 * 1000;
    }

    public static String humanReadable(long timestamp) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.US);
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }
}
