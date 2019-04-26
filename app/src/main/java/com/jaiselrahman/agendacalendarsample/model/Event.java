package com.jaiselrahman.agendacalendarsample.model;

import androidx.annotation.NonNull;

import com.jaiselrahman.agendacalendar.model.BaseEvent;

import java.util.Calendar;

public class Event implements BaseEvent {
    private String title, description, location;
    private Calendar time;
    private int color;

    public Event(@NonNull String title, String description, String location, Calendar time, int color) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.time = time;
        this.color = color;
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

    @Override
    public int getColor() {
        return color;
    }
}
