package com.jaiselrahman.agendacalendar.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaiselrahman.agendacalendar.R;
import com.jaiselrahman.agendacalendar.model.BaseEvent;
import com.jaiselrahman.agendacalendar.util.DateUtils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>
        implements StickyHeaderAdapter<EventAdapter.HeaderViewHolder> {
    private static Calendar cal = Calendar.getInstance();
    private static DateFormat timeFormat = DateFormat.getTimeInstance();
    private SortedList<BaseEvent> events = new SortedList<>(BaseEvent.class, new SortedList.Callback<BaseEvent>() {
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

        }

        @Override
        public void onInserted(int position, int count) {

        }

        @Override
        public void onRemoved(int position, int count) {

        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {

        }
    });

    public EventAdapter(List<? extends BaseEvent> events) {
        this.events.addAll(events.toArray(new BaseEvent[0]), true);
    }

    @Override
    public long getHeaderId(int position) {
        return TimeUnit.MILLISECONDS.toDays(events.get(position).getTime());
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder headerViewHolder, int position) {
        headerViewHolder.bind(events.get(position));
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.bind(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events == null ? 0 : events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        private TextView location;
        private TextView time;

        EventViewHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.title);
            description = v.findViewById(R.id.description);
            location = v.findViewById(R.id.location);
            time = v.findViewById(R.id.time);
        }

        void bind(BaseEvent event) {
            this.title.setText(event.getTitle());

            description.setVisibility(TextUtils.isEmpty(event.getDescription()) ? View.GONE : View.VISIBLE);
            description.setText(event.getDescription());

            location.setVisibility(TextUtils.isEmpty(event.getLocation()) ? View.GONE : View.VISIBLE);
            location.setText(event.getLocation());

            time.setVisibility(event.getTime() <= 0 ? View.GONE : View.VISIBLE);
            time.setText(timeFormat.format(event.getTime()));
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
            cal.setTimeInMillis(event.getTime());
            date.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
            day.setText(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
            if (DateUtils.isToday(event.getTime())) {
                itemView.setBackgroundResource(R.drawable.event_current_item_header);
            }
        }
    }
}
