package com.jaiselrahman.agendacalendar.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.jaiselrahman.agendacalendar.R;
import com.jaiselrahman.agendacalendar.model.BaseEvent;
import com.jaiselrahman.agendacalendar.util.DateUtils;
import com.jaiselrahman.agendacalendar.util.EventCache;
import com.jaiselrahman.agendacalendar.util.EventUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

@SuppressWarnings("WeakerAccess")
public abstract class EventAdapter<T extends BaseEvent>
        extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private static final int EVENT = 0;
    private static final int EMPTY_EVENT = 1;

    private final StickyHeaderAdapter<EventAdapter.HeaderViewHolder> eventStickyHeader =
            new StickyHeaderAdapter<EventAdapter.HeaderViewHolder>() {

                @Override
                final public long getHeaderId(int position) {
                    Calendar cal = eventItems.get(position).getTime();
                    return cal.get(Calendar.YEAR) * 1000 + cal.get(Calendar.DAY_OF_YEAR);
                }

                @Override
                final public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item_header, parent, false);
                    return new HeaderViewHolder(v);
                }

                @Override
                public void onBindHeaderViewHolder(HeaderViewHolder headerViewHolder, int position) {
                    headerViewHolder.bind(eventItems.get(position));
                }
            };

    private static Calendar cal = Calendar.getInstance();

    private OnEventClickListener<T> onEventClickListener;

    private SortedList<BaseEvent> eventItems = new SortedList<>(BaseEvent.class, new SortedList.Callback<BaseEvent>() {
        @Override
        public int compare(BaseEvent o1, BaseEvent o2) {
            return o1.compareTo(o2);
        }

        @Override
        public boolean areContentsTheSame(BaseEvent oldItem, BaseEvent newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(BaseEvent item1, BaseEvent item2) {
            return item1.equals(item2);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemChanged(position, count);
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }
    });

    private EventCache.Loader eventLoader = time -> {
        int pos = getPosition(time);
        if (pos == -1) {
            return Collections.emptyList();
        }

        List<BaseEvent> events = new ArrayList<>();

        BaseEvent event = eventItems.get(pos);
        events.add(event);

        for (int i = pos + 1; i < eventItems.size(); i++) {
            BaseEvent nextEvent = eventItems.get(i);
            if (event.compareTo(nextEvent) != 0) {
                break;
            } else {
                events.add(nextEvent);
                event = nextEvent;
            }
        }

        return events;
    };

    final public void setEvents(List<T> events) {
        eventItems.beginBatchedUpdates();

        //noinspection unchecked
        eventItems.replaceAll((List<BaseEvent>) events);
        fillNoEvents();
        EventCache.clear();

        eventItems.endBatchedUpdates();
    }

    public T getEvent(int position) {
        //noinspection unchecked
        return (T) eventItems.get(position);
    }

    @NonNull
    @Override
    final public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == EVENT) {
            EventViewHolder holder = createEventViewHolder(parent);
            holder.setOnEventClickListener(onEventClickListener);
            return holder;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_empty_item, parent, false);
            return new EmptyEventHolder(v);
        }
    }

    public abstract EventViewHolder<T> createEventViewHolder(ViewGroup viewGroup);

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        BaseEvent event = eventItems.get(position);
        holder.bind(event);
        holder.event = event;
    }

    @Override
    public int getItemViewType(int position) {
        if (eventItems.get(position) instanceof BaseEvent.Empty) {
            return EMPTY_EVENT;
        }
        return EVENT;
    }

    @Override
    public int getItemCount() {
        return eventItems == null ? 0 : eventItems.size();
    }

    public int getPosition(long time) {
        cal.setTimeInMillis(time);
        return EventUtils.searchEvent(eventItems, cal);
    }

    public List<T> getEventsOn(long time) {
        //noinspection unchecked
        return (List<T>) EventCache.getEvents(time, eventLoader);
    }

    public void setOnEventClickListener(OnEventClickListener<T> onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
    }

    private void fillNoEvents() {
        List<BaseEvent> emptyEvents = new ArrayList<>();

        int toBeAdded = 0, eventSize = eventItems.size();
        BaseEvent lastChecked = eventItems.get(toBeAdded);

        while (toBeAdded < eventSize) {
            boolean hasEvent = false;

            for (int j = toBeAdded; j < eventSize; ) {
                BaseEvent event = eventItems.get(j);
                long dayDiff = DateUtils.dayDiff(event.getTime().getTimeInMillis(), lastChecked.getTime().getTimeInMillis());
                if (dayDiff <= 1) {
                    lastChecked = event;
                    hasEvent = true;
                    toBeAdded = ++j;
                } else {
                    break;
                }
            }

            if (!hasEvent) {
                Calendar cal = (Calendar) lastChecked.getTime().clone();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                lastChecked = new BaseEvent.Empty(cal);
                emptyEvents.add(lastChecked);
            }
        }

        eventItems.addAll(emptyEvents);
    }

    StickyHeaderAdapter<EventAdapter.HeaderViewHolder> getEventStickyHeader() {
        return eventStickyHeader;
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView day, date;

        HeaderViewHolder(@NonNull View v) {
            super(v);
            day = v.findViewById(R.id.day);
            date = v.findViewById(R.id.date);
        }

        void bind(BaseEvent event) {
            cal.setTimeInMillis(event.getTime().getTimeInMillis());
            date.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
            day.setText(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
            if (DateUtils.isToday(event.getTime().getTimeInMillis())) {
                itemView.setBackgroundResource(R.drawable.event_current_item_header);
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
