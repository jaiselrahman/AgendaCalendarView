package com.jaiselrahman.agendacalendar.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

public interface BaseEvent extends Comparable {

    @NonNull
    LocalDateTime getTime();

    default int getColor() {
        return 0;
    }

    default int compareTo(Object o) {
        if (o == null) return -1;
        LocalDateTime curr = getTime(), other;

        if (o instanceof LocalDate) {
            LocalDate date = (LocalDate) o;
            if (curr.getYear() == date.getYear()
                    && curr.getDayOfYear() == date.getDayOfYear()) {
                return 0;
            } else {
                return curr.compareTo(date.atStartOfDay());
            }
        }

        if (o instanceof LocalDateTime) {
            other = (LocalDateTime) o;
        } else if (o instanceof BaseEvent) {
            other = ((BaseEvent) o).getTime();
        } else {
            throw new ClassCastException("Argument should be instance of Calendar or BaseEvent");
        }

        if (curr.getYear() == other.getYear()
                && curr.getDayOfYear() == other.getDayOfYear()) {
            return 0;
        } else {
            return curr.compareTo(other);
        }
    }

    final class Empty implements BaseEvent {
        private LocalDateTime time;

        public Empty(LocalDateTime time) {
            this.time = time;
        }

        public String getTitle() {
            return "No Events";
        }

        @Override
        @NonNull
        public LocalDateTime getTime() {
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
