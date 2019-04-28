package com.jaiselrahman.agendacalendarsample.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckedTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jaiselrahman.agendacalendar.view.AgendaCalendar;
import com.jaiselrahman.agendacalendarsample.R;
import com.jaiselrahman.agendacalendarsample.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private AgendaCalendar agendaCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);

        List<Event> events = mockEvents();

        agendaCalendar = findViewById(R.id.calendar);

        agendaCalendar.setEvents(events);

        agendaCalendar.setOnEventClickListener(event -> {
            Toast.makeText(this, event.getTitle(), Toast.LENGTH_SHORT).show();
        });


        agendaCalendar.setListener(new AgendaCalendar.CalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                agendaCalendar.scrollTo(dateClicked.getTime());
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }
        });

        CheckedTextView currentMonth = findViewById(R.id.currentMonth);
        currentMonth.setText(Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
        currentMonth.setOnClickListener(v -> {
            if (currentMonth.isChecked()) {
                agendaCalendar.hideCalendar();
            } else {
                agendaCalendar.showCalendar();
            }
            currentMonth.setChecked(!currentMonth.isChecked());
        });

        agendaCalendar.setHeightAnimDuration(250);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.today) {
            agendaCalendar.scrollTo(System.currentTimeMillis());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Event> mockEvents() {
        List<Event> events = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            if (i % 2 == 0) {
                events.addAll(getEvents(i, 1));
            } else if (i % 3 == 0) {
                events.addAll(getEvents(i, 2));
            } else if (i % 5 == 0) {
                events.addAll(getEvents(i, 3));
            } else if (i == 1) {
                events.addAll(getEvents(i, 4));
            }
        }
        return events;
    }

    private List<Event> getEvents(int day, int count) {
        List<Event> events = new ArrayList<>();
        Calendar cal;
        switch (count) {
            case 4:
                cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, day);
                events.add(new Event("Event " + cal.get(Calendar.DAY_OF_MONTH),
                        "Description " + day, "Location " + day, cal, Color.RED));
            case 3:
                cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, day);
                events.add(new Event("Event " + cal.get(Calendar.DAY_OF_MONTH),
                        "Description " + day, "Location " + day, cal, Color.GREEN));
            case 2:
                cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, day);
                events.add(new Event("Event " + cal.get(Calendar.DAY_OF_MONTH),
                        "Description " + day, "Location " + day, cal, Color.BLUE));
            case 1:
                cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, day);
                events.add(new Event("Event " + cal.get(Calendar.DAY_OF_MONTH),
                        "Description " + day, "Location " + day, cal, Color.MAGENTA));
        }
        return events;
    }

}
