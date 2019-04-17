package com.jaiselrahman.agendacalendar.model;

import androidx.annotation.NonNull;

public class Event implements BaseEvent {
    private String title, description, location;
    private long time;

    public Event(@NonNull String title, String description, String location, long time) {
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
    public long getTime() {
        return time;
    }
}
