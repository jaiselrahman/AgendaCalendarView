package com.jaiselrahman.agendacalendarsample;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jaiselrahman.agendacalendar.view.EventAdapter;
import com.jaiselrahman.agendacalendarsample.model.Event;

import java.text.DateFormat;

public class MyEventAdapter extends EventAdapter<Event> {
    private static DateFormat timeFormat = DateFormat.getTimeInstance();

    @Override
    public MyEventViewHolder createEventViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new MyEventViewHolder(v);
    }

    public static class MyEventViewHolder extends EventViewHolder<Event> {
        private TextView title;
        private TextView description;
        private TextView location;
        private TextView time;

        MyEventViewHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.title);
            description = v.findViewById(R.id.description);
            location = v.findViewById(R.id.location);
            time = v.findViewById(R.id.time);

        }

        @Override
        public void bind(Event event) {

            this.title.setText(event.getTitle());

            description.setVisibility(TextUtils.isEmpty(event.getDescription()) ? View.GONE : View.VISIBLE);
            description.setText(event.getDescription());

            location.setVisibility(TextUtils.isEmpty(event.getLocation()) ? View.GONE : View.VISIBLE);
            location.setText(event.getLocation());

            time.setText(timeFormat.format(event.getTime().getTimeInMillis()));
        }
    }
}
