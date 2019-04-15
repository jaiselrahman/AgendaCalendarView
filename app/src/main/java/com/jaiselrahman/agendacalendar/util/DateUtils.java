package com.jaiselrahman.agendacalendar.util;

import java.util.concurrent.TimeUnit;

public class DateUtils {
    public static boolean isToday(long when) {
        long today = System.currentTimeMillis();
        return TimeUnit.MILLISECONDS.toDays(today) == TimeUnit.MILLISECONDS.toDays(when);
    }
}
