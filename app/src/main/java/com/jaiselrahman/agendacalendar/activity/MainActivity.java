package com.jaiselrahman.agendacalendar.activity;

import android.os.Bundle;

import com.jaiselrahman.agendacalendar.R;
import com.jaiselrahman.agendacalendar.adapter.EventAdapter;
import com.jaiselrahman.agendacalendar.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Event> events = mockEvents();

        EventAdapter eventAdapter = new EventAdapter(events);

        RecyclerView recyclerView = findViewById(R.id.eventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(eventAdapter);

        StickyHeaderDecoration notificationHeader = new StickyHeaderDecoration(eventAdapter, false);
        recyclerView.addItemDecoration(notificationHeader);
    }

    private List<Event> mockEvents() {
        List<Event> events = new ArrayList<>();
        int curDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < 10; i++) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, curDay + i + (i % 2));
            events.add(new Event("Event " + cal.get(Calendar.DAY_OF_MONTH), "Description " + i, "Location " + i, cal.getTimeInMillis()));
        }
        events.add(new Event("Event 1", null, "Location 1", 0));
        events.add(new Event("Event 2", null, "Location 2", System.currentTimeMillis()));
        events.add(new Event("Event 3", "Description 3", null, 0));
        events.add(new Event("Event 3", null, null, System.currentTimeMillis()));
        return events;
    }

}
