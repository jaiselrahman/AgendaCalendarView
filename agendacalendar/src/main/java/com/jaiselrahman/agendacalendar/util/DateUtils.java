package com.jaiselrahman.agendacalendar.util;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.WeekFields;

import java.util.Locale;

public class DateUtils {
    private static LocalDate today = LocalDate.now();

    public static boolean isToday(LocalDateTime localDateTime) {
        return today.getYear() == localDateTime.getYear()
                && today.getDayOfYear() == localDateTime.getDayOfYear();
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
