package com.jaiselrahman.agendacalendarsample;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;

import com.jaiselrahman.agendacalendar.view.EventAdapter;
import com.jaiselrahman.agendacalendar.view.EventList;
import com.jaiselrahman.agendacalendarsample.model.Event;

import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

public class MyEventAdapter extends EventAdapter<Event, PagedList<Event>> {

    public MyEventAdapter() {
        super(new EventList.PagedEventList<>(EVENT_CALLBACK));
    }

    @Override
    public MyEventViewHolder createEventViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new MyEventViewHolder(v);
    }

    public static class MyEventViewHolder extends EventViewHolder<Event> {
        private View root;
        private TextView title;
        private TextView description;
        private TextView location;
        private TextView time;
        private DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

        MyEventViewHolder(@NonNull View v) {
            super(v);
            root = v.findViewById(R.id.root);
            title = v.findViewById(R.id.title);
            description = v.findViewById(R.id.description);
            location = v.findViewById(R.id.location);
            time = v.findViewById(R.id.time);

        }

        @Override
        public void bind(Event event) {

            root.setBackgroundColor(event.getColor());

            title.setText(event.getTitle());

            description.setVisibility(TextUtils.isEmpty(event.getDescription()) ? View.GONE : View.VISIBLE);
            description.setText(event.getDescription());

            location.setVisibility(TextUtils.isEmpty(event.getLocation()) ? View.GONE : View.VISIBLE);
            location.setText(event.getLocation());

            time.setText(event.getTime().format(timeFormatter));
        }
    }

    private static final EventList.EventCallback<Event> EVENT_CALLBACK = new EventList.EventCallback<Event>() {
        @Override
        public boolean areEventsTheSame(@NonNull Event oldEvent, @NonNull Event newEvent) {
            return oldEvent.equals(newEvent);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Event oldEvent, @NonNull Event newEvent) {
            return oldEvent.equals(newEvent);
        }
    };
}
