package com.jaiselrahman.agendacalendar.model;

import androidx.annotation.NonNull;

import java.util.Calendar;

public interface BaseEvent extends Comparable {

    @NonNull
    String getTitle();

    Calendar getTime();

    default String getDescription() {
        return null;
    }

    default String getLocation() {
        return null;
    }

    @Override
    default int compareTo(Object o) {
        Calendar curr = getTime(), other;

        if (o instanceof Calendar) {
            other = (Calendar) o;
        } else if (o instanceof BaseEvent) {
            other = ((BaseEvent) o).getTime();
        } else {
            throw new ClassCastException("Argument should be instance of Calendar or BaseEvent");
        }

        if (curr.get(Calendar.YEAR) == other.get(Calendar.YEAR)
                && curr.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)) {
            return 0;
        } else {
            return curr.compareTo(other);
        }
    }
}
