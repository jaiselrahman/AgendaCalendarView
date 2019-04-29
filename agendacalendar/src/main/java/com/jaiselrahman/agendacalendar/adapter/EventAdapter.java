package com.jaiselrahman.agendacalendar.adapter;

import android.text.TextUtils;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>
        implements StickyHeaderAdapter<EventAdapter.HeaderViewHolder> {
    private static final int EVENT = 0;
    private static final int EMPTY_EVENT = 1;

    private static Calendar cal = Calendar.getInstance();
    private static DateFormat timeFormat = DateFormat.getTimeInstance();

    private OnEventClickListener onEventClickListener;

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

    public void setEvents(List<BaseEvent> events) {
        eventItems.beginBatchedUpdates();

        eventItems.replaceAll(events);
        fillNoEvents();

        eventItems.endBatchedUpdates();
    }

    public <T extends BaseEvent> void addEvent(T event) {
        int pos = eventItems.indexOf(new BaseEvent.Empty(event.getTime()));
        if (pos != SortedList.INVALID_POSITION) {
            eventItems.removeItemAt(pos);
        }
        eventItems.add(event);
    }

    public void addEvents(List<BaseEvent> events) {
        if (events.isEmpty()) return;

        if (events.size() == 1) {
            addEvent(events.get(0));
        }

        eventItems.beginBatchedUpdates();

        for (BaseEvent event : events) {
            int pos = eventItems.indexOf(new BaseEvent.Empty(event.getTime()));
            if (pos != SortedList.INVALID_POSITION) {
                eventItems.removeItemAt(pos);
            }
        }

        eventItems.addAll(events);
        fillNoEvents();

        eventItems.endBatchedUpdates();
    }

    public void removeEvent(BaseEvent event) {
        int pos = eventItems.indexOf(event);

        eventItems.remove(event);

        long dayDiff = pos < 0 || pos >= eventItems.size() ? 0 :
                DateUtils.dayDiff(eventItems.get(pos).getTimeInMillis(),
                        eventItems.get(pos - 1).getTimeInMillis());

        if (dayDiff > 1) {
            eventItems.add(new BaseEvent.Empty(event.getTime()));
        }
    }

    public void removeEvents(List<BaseEvent> events) {
        eventItems.beginBatchedUpdates();

        for (BaseEvent event : events) {
            removeEvent(event);
        }

        eventItems.endBatchedUpdates();
    }

    private void fillNoEvents() {
        List<BaseEvent> emptyEvents = new ArrayList<>();

        int toBeAdded = 0, eventSize = eventItems.size();
        BaseEvent lastChecked = eventItems.get(toBeAdded);

        while (toBeAdded < eventSize) {
            boolean hasEvent = false;

            for (int j = toBeAdded; j < eventSize; ) {
                BaseEvent event = eventItems.get(j);
                long dayDiff = DateUtils.dayDiff(event.getTimeInMillis(), lastChecked.getTimeInMillis());
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

    @Override
    public long getHeaderId(int position) {
        Calendar cal = eventItems.get(position).getTime();
        return cal.get(Calendar.YEAR) * 1000 + cal.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder headerViewHolder, int position) {
        headerViewHolder.bind(eventItems.get(position));
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == EVENT ? R.layout.event_list_item : R.layout.event_list_empty_item;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new EventViewHolder(v, onEventClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        if (getItemViewType(position) == EVENT)
            holder.bind(eventItems.get(position));
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
        for (int i = 0; i < getItemCount(); i++) {
            if (eventItems.get(i).compareTo(cal) == 0) return i;
        }
        return -1;
    }

    public void setOnEventClickListener(OnEventClickListener onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
    }

    public interface OnEventClickListener {
        void onEventClick(BaseEvent event);
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        private TextView location;
        private TextView time;
        private BaseEvent event;

        EventViewHolder(@NonNull View v, OnEventClickListener onEventClickListener) {
            super(v);
            title = v.findViewById(R.id.title);
            description = v.findViewById(R.id.description);
            location = v.findViewById(R.id.location);
            time = v.findViewById(R.id.time);

            v.setOnClickListener(v1 -> onEventClickListener.onEventClick(event));
        }

        void bind(BaseEvent event) {
            this.event = event;

            this.title.setText(event.getTitle());

            description.setVisibility(TextUtils.isEmpty(event.getDescription()) ? View.GONE : View.VISIBLE);
            description.setText(event.getDescription());

            location.setVisibility(TextUtils.isEmpty(event.getLocation()) ? View.GONE : View.VISIBLE);
            location.setText(event.getLocation());

            time.setVisibility(event.getTime() != null ? View.GONE : View.VISIBLE);
            time.setText(timeFormat.format(event.getTime().getTimeInMillis()));
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
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
}
