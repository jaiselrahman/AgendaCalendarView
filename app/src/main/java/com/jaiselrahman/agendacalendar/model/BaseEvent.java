package com.jaiselrahman.agendacalendar.model;

import androidx.annotation.NonNull;

public interface BaseEvent {

    @NonNull
    String getTitle();

    String getDescription();

    String getLocation();

    long getTime();
}
