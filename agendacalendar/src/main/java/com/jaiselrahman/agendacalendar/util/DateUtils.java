package com.jaiselrahman.agendacalendar.util;

import java.util.concurrent.TimeUnit;

public class DateUtils {
    public static boolean isToday(long when) {
        long today = System.currentTimeMillis();
        return TimeUnit.MILLISECONDS.toDays(today) == TimeUnit.MILLISECONDS.toDays(when);
    }

    public static long dayDiff(long day1, long day2) {
        return TimeUnit.MILLISECONDS.toDays(day1) - TimeUnit.MILLISECONDS.toDays(day2);
    }
}
