package com.jaiselrahman.agendacalendar.util;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;

import com.jaiselrahman.agendacalendar.model.BaseEvent;

import java.time.LocalDate;
import java.util.List;

public class EventCache {
    private static final LongSparseArray<List<BaseEvent>> eventCache = new LongSparseArray<>();

    @NonNull
    public static List<BaseEvent> getEvents(LocalDate localDate, Loader eventLoader) {
        long key = getCacheKey(localDate);
        List<BaseEvent> events = eventCache.get(key);
        if (events == null) {
            events = eventLoader.getEvents(localDate);

        }
        eventCache.put(key, events);
        return events;
    }

    public static void clear(LocalDate localDate) {
        eventCache.remove(getCacheKey(localDate));
    }

    public static void clearAll() {
        eventCache.clear();
    }

    private static long getCacheKey(LocalDate localDate) {
        return localDate.toEpochDay();
    }

    public interface Loader {
        @NonNull
        List<BaseEvent> getEvents(LocalDate localDate);
    }
}
