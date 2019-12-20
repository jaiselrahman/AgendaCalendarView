package com.jaiselrahman.agendacalendarsample.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jaiselrahman.agendacalendar.model.BaseEvent;

import org.threeten.bp.LocalDateTime;

public class Event implements BaseEvent {
    private String title, description, location;
    private LocalDateTime time;
    private int color;

    public Event(@NonNull String title, String description, String location, LocalDateTime time, int color) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.time = time;
        this.color = color;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    @Override
    @NonNull
    public LocalDateTime getTime() {
        return time;
    }

    public int getColor() {
        return color;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Event) {
            Event event = (Event) obj;
            return time == event.getTime()
                    && title.equals(event.getTitle());
        }
        return super.equals(obj);
    }
}
