package com.jaiselrahman.agendacalendar.util;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;

import com.jaiselrahman.agendacalendar.model.BaseEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventCache {
    private static final LongSparseArray<List<BaseEvent>> eventCache = new LongSparseArray<>();

    @NonNull
    public static List<BaseEvent> getEvents(long time, Loader eventLoader) {
        long key = getCacheKey(time);
        List<BaseEvent> events = eventCache.get(key);
        if (events == null) {
            events = eventLoader.getEvents(time);

        }
        eventCache.put(key, events);
        return events;
    }

    public static void clear() {
        eventCache.clear();
    }

    private static long getCacheKey(long time) {
        return TimeUnit.MILLISECONDS.toDays(time);
    }

    public interface Loader {
        @NonNull
        List<BaseEvent> getEvents(long time);
    }
}
