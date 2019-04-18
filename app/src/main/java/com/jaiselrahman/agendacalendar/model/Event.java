package com.jaiselrahman.agendacalendar.model;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class Event implements BaseEvent {
    private String title, description, location;
    private Calendar time;

    public Event(@NonNull String title, String description, String location, Calendar time) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.time = time;
    }

    @Override
    @NonNull
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public Calendar getTime() {
        return time;
    }
}
