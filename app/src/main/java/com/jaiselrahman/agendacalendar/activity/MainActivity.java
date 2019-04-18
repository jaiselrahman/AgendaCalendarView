package com.jaiselrahman.agendacalendar.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.jaiselrahman.agendacalendar.R;
import com.jaiselrahman.agendacalendar.adapter.EventAdapter;
import com.jaiselrahman.agendacalendar.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

public class MainActivity extends AppCompatActivity {
    private LinearLayoutManager layoutManager;
    private EventAdapter eventAdapter;
    private CompactCalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);

        List<Event> events = mockEvents();

        eventAdapter = new EventAdapter(events);

        layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.eventList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(eventAdapter);

        StickyHeaderDecoration notificationHeader = new StickyHeaderDecoration(eventAdapter, false);
        recyclerView.addItemDecoration(notificationHeader);

        calendar = findViewById(R.id.calendar);
        calendar.setVisibility(View.GONE);
        calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                int pos = eventAdapter.getPosition(dateClicked.getTime());
                if (pos >= 0) {
                    layoutManager.scrollToPositionWithOffset(pos, 0);
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }
        });

        for (Event event : events) {
            calendar.addEvent(new com.github.sundeepk.compactcalendarview.domain.Event(Color.BLUE, event.getTime().getTimeInMillis()));
        }

        CheckedTextView currentMonth = findViewById(R.id.currentMonth);
        currentMonth.setText(Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
        currentMonth.setOnClickListener(v -> {
            if (calendar.isAnimating()) return;

            if (calendar.getVisibility() == View.GONE) {
                calendar.setVisibility(View.VISIBLE);
            }

            if (currentMonth.isChecked()) {
                calendar.hideCalendar();
            } else {
                calendar.showCalendar();
            }
            currentMonth.setChecked(!currentMonth.isChecked());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.today) {
            int pos = eventAdapter.getPosition(System.currentTimeMillis());
            if (pos >= 0) {
                layoutManager.scrollToPositionWithOffset(pos, 0);
            }
            calendar.setCurrentDate(new Date());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Event> mockEvents() {
        List<Event> events = new ArrayList<>();
        int curDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < 10; i++) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, curDay + i + (i % 2));
            events.add(new Event("Event " + cal.get(Calendar.DAY_OF_MONTH), "Description " + i, "Location " + i, cal));
        }
        events.add(new Event("Event 1", null, "Location 1", Calendar.getInstance()));
        events.add(new Event("Event 2", null, "Location 2", Calendar.getInstance()));
        events.add(new Event("Event 3", "Description 3", null, Calendar.getInstance()));
        return events;
    }

}
