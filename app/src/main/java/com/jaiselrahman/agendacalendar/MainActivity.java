package com.jaiselrahman.agendacalendar;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Event> events = new ArrayList<>();
        int curDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < 10; i++) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, curDay + i + (i % 2));
            events.add(new Event("Event " + cal.get(Calendar.DAY_OF_MONTH), "Description " + i, "Location " + i, cal.getTimeInMillis()));
        }

        EventAdapter eventAdapter = new EventAdapter(events);

        RecyclerView recyclerView = findViewById(R.id.eventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(eventAdapter);

        StickyHeaderDecoration notificationHeader = new StickyHeaderDecoration(eventAdapter, false);
        recyclerView.addItemDecoration(notificationHeader);
    }

    public static class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>
            implements StickyHeaderAdapter<EventAdapter.HeaderViewHolder> {
        private List<? extends BaseEvent> events;

        public EventAdapter(List<? extends BaseEvent> events) {
            this.events = events;
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(events.get(position));
        }

        @Override
        public int getItemCount() {
            return events == null ? 0 : events.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView title;
            private TextView description;
            private TextView location;
            private TextView time;

            ViewHolder(@NonNull View v) {
                super(v);
                title = v.findViewById(R.id.title);
                description = v.findViewById(R.id.description);
                location = v.findViewById(R.id.location);
                time = v.findViewById(R.id.time);
            }

            void bind(BaseEvent event) {
                title.setText(event.getTitle());
                description.setText(event.getDescription());
                location.setText(event.getLocation());
                time.setText(DateFormat.getTimeInstance().format(event.getTime()));
            }
        }

        public static class HeaderViewHolder extends RecyclerView.ViewHolder {
            private TextView day, date;
            public HeaderViewHolder(@NonNull View v) {
                super(v);
                day = v.findViewById(R.id.day);
                date = v.findViewById(R.id.date);
            }

            public void bind(BaseEvent event) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(event.getTime());
                date.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                day.setText(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
                if (DateUtils.isToday(event.getTime())) {
                    itemView.setBackgroundResource(R.drawable.event_current_item_header);
                }
            }
        }
    }

    public static class Event implements BaseEvent {
        private String title, description, location;
        private long time;

        public Event(String title, String description, String location, long time) {
            this.title = title;
            this.description = description;
            this.location = location;
            this.time = time;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getLocation() {
            return location;
        }

        @Override
        public long getTime() {
            return time;
        }
    }
}
