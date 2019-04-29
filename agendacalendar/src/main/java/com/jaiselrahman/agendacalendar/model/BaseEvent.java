package com.jaiselrahman.agendacalendar.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

public interface BaseEvent extends Comparable, com.github.sundeepk.compactcalendarview.domain.BaseEvent {

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
    default long getTimeInMillis() {
        if (getTime() == null) return 0;
        return getTime().getTimeInMillis();
    }

    default int getColor() {
        return 0;
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

    final class Empty implements BaseEvent {
        private Calendar time;

        public Empty(Calendar time) {
            this.time = time;
        }

        @NonNull
        @Override
        public String getTitle() {
            return "No Events";
        }

        @Override
        public Calendar getTime() {
            return time;
        }

        @Override
        public final boolean equals(@Nullable Object obj) {
            if (obj instanceof Empty) {
                Calendar other = ((Empty) obj).getTime();
                if (time.get(Calendar.YEAR) == other.get(Calendar.YEAR)
                        && time.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)) {
                    return true;
                }
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }
}
