package com.jaiselrahman.agendacalendar.util;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.temporal.WeekFields;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    public static boolean isToday(long when) {
        long today = System.currentTimeMillis();
        return TimeUnit.MILLISECONDS.toDays(today) == TimeUnit.MILLISECONDS.toDays(when);
    }

    public static long dayDiff(long day1, long day2) {
        return TimeUnit.MILLISECONDS.toDays(day1) - TimeUnit.MILLISECONDS.toDays(day2);
    }

    public static String[] getDaysOfWeek() {
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();

        DayOfWeek[] daysOfWeek = DayOfWeek.values();
        String[] daysOfWeek2 = new String[daysOfWeek.length];

        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            int j = 0;
            for (int i = firstDayOfWeek.ordinal(); i < daysOfWeek.length; i++, j++)
                daysOfWeek2[j] = String.valueOf(daysOfWeek[i].toString().charAt(0));

            for (int i = 0; i < firstDayOfWeek.ordinal(); i++, j++)
                daysOfWeek2[j] = String.valueOf(daysOfWeek[i].toString().charAt(0));
        }

        return daysOfWeek2;
    }
}
