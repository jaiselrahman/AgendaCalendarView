package com.jaiselrahman.agendacalendar.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.jaiselrahman.agendacalendar.R;
import com.jaiselrahman.agendacalendar.model.BaseEvent;
import com.jaiselrahman.agendacalendar.util.DateUtils;
import com.jaiselrahman.agendacalendar.util.EventCache;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.format.TextStyle;
import org.threeten.bp.temporal.ChronoField;

import java.util.List;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class EventAdapter<E extends BaseEvent, T extends List<E>>
        extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private static final DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
            .appendText(ChronoField.DAY_OF_WEEK, TextStyle.SHORT)
            .toFormatter();

    private static final DateTimeFormatter monthFormatter = new DateTimeFormatterBuilder()
            .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT)
            .toFormatter();

    private static final int EVENT = 0;
    private static final int EMPTY_EVENT = 1;
    private static final BaseEvent.Empty key = new BaseEvent.Empty(null);

    private final StickyHeaderAdapter<HeaderViewHolder> dayHeader =
            new StickyHeaderAdapter<HeaderViewHolder>() {

                @Override
                final public long getHeaderId(int position) {
                    LocalDateTime date = eventList.get(position).getTime();
                    return date.getYear() * 1000 + date.getDayOfYear();
                }

                @Override
                final public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item_header, parent, false);
                    return new HeaderViewHolder(v);
                }

                @Override
                public void onBindHeaderViewHolder(HeaderViewHolder headerViewHolder, int position) {
                    BaseEvent event = eventList.get(position);
                    headerViewHolder.bind(event, showMonth(event, position));
                }

                private boolean showMonth(BaseEvent event, int position) {
                    return getHeaderId(position) == getHeaderId(0)
                            || getEvent(position - 1).getTime().getMonthValue() != event.getTime().getMonthValue();
                }
            };

    private OnEventClickListener<E> onEventClickListener;

    private EventList<E, T> eventList;

    private int dayTextColor;
    private int currentDayTextColor;
    private int currentDayBackground;

    public EventAdapter(EventList<E, T> eventList) {
        this.eventList = eventList;
        this.eventList.setAdapter(this);
    }

    private EventCache.Loader eventLoader = time -> {
        //noinspection unchecked
        return (List<BaseEvent>) eventList.getEvents(time);
    };

    final public void setEvents(T events) {
        eventList.setEvents(events);
    }

    public E getEvent(int position) {
        return eventList.get(position);
    }

    @NonNull
    @Override
    final public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == EVENT) {
            EventViewHolder holder = createEventViewHolder(parent);
            //noinspection unchecked
            holder.setOnEventClickListener(onEventClickListener);
            return holder;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_empty_item, parent, false);
            return new EmptyEventHolder(v);
        }
    }

    public abstract EventViewHolder<E> createEventViewHolder(ViewGroup viewGroup);

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        BaseEvent event = eventList.get(position);
        //noinspection unchecked
        holder.bind(event);
        holder.event = event;
    }

    @Override
    public int getItemViewType(int position) {
        if (eventList.get(position) instanceof BaseEvent.Empty) {
            return EMPTY_EVENT;
        }
        return EVENT;
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public int getAdapterPosition(LocalDate localDate) {
        key.setTime(localDate.atStartOfDay());
        return eventList.possibleIndexOf(key);
    }

    public List<E> getEventsOn(LocalDate localDate) {
        //noinspection unchecked
        return (List<E>) EventCache.getEvents(localDate, eventLoader);
    }

    public void setOnEventClickListener(OnEventClickListener<E> onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
    }

    void setOnEventSetListener(EventList.OnEventSetListener onEventSetListener) {
        eventList.setOnEventSetListener(onEventSetListener);
    }

    void setCurrentDayBackground(int currentDayBackground) {
        this.currentDayBackground = currentDayBackground;
    }

    void setCurrentDayTextColor(int currentDayTextColor) {
        this.currentDayTextColor = currentDayTextColor;
    }

    void setDayTextColor(int dayTextColor) {
        this.dayTextColor = dayTextColor;
    }

    StickyHeaderAdapter<HeaderViewHolder> getDayHeader() {
        return dayHeader;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView day, date, month;

        HeaderViewHolder(@NonNull View v) {
            super(v);
            day = v.findViewById(R.id.day);
            day.setTextColor(ColorUtils.setAlphaComponent(dayTextColor, 0xAA));

            date = v.findViewById(R.id.date);
            date.setTextColor(dayTextColor);

            month = v.findViewById(R.id.month);
            month.setTextColor(dayTextColor);
        }

        void bind(BaseEvent event, boolean showMonth) {
            date.setText(String.valueOf(event.getTime().getDayOfMonth()));
            day.setText(dateFormatter.format(event.getTime()));

            if (DateUtils.isToday(event.getTime())) {
                date.setBackgroundResource(currentDayBackground);
                date.setTextColor(currentDayTextColor);
            } else {
                date.setBackground(null);
                date.setTextColor(dayTextColor);
            }

            if (showMonth) {
                month.setVisibility(View.VISIBLE);
                month.setText(monthFormatter.format(event.getTime()));
            } else {
                month.setVisibility(View.GONE);
            }
        }
    }

    private static class EmptyEventHolder extends EventViewHolder<BaseEvent.Empty> {
        private TextView title;

        EmptyEventHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
        }

        public void bind(BaseEvent.Empty emptyEvent) {
            title.setText(emptyEvent.getTitle());
        }
    }

    public abstract static class EventViewHolder<E extends BaseEvent> extends RecyclerView.ViewHolder {
        private E event;

        public EventViewHolder(View v) {
            super(v);
        }

        private void setOnEventClickListener(OnEventClickListener<E> onEventClickListener) {
            itemView.setOnClickListener(v1 -> onEventClickListener.onEventClick(event));
        }

        public abstract void bind(E event);
    }

    public interface OnEventClickListener<T extends BaseEvent> {
        void onEventClick(T event);
    }
}
