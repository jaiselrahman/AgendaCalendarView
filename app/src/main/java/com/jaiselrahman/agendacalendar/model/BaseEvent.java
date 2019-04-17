package com.jaiselrahman.agendacalendar.model;

import androidx.annotation.NonNull;

public interface BaseEvent extends Comparable {

    @NonNull
    String getTitle();

    long getTime();

    default String getDescription() {
        return null;
    }

    default String getLocation() {
        return null;
    }

    @Override
    default int compareTo(Object o) {
        return (int) (getTime() - ((BaseEvent) o).getTime());
    }
}
