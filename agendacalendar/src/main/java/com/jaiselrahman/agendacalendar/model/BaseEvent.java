package com.jaiselrahman.agendacalendar.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

public interface BaseEvent extends Comparable {

    @NonNull
    Calendar getTime();

    default int getColor() {
        return 0;
    }

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

        public String getTitle() {
            return "No Events";
        }

        @Override
        @NonNull
        public Calendar getTime() {
            return time;
        }

        @Override
        public final boolean equals(@Nullable Object other) {
            if (other instanceof Empty) {
                return compareTo(other) == 0;
            }
            return super.equals(other);
        }

        @Override
        final public int hashCode() {
            return 0;
        }
    }
}
