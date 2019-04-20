package com.jaiselrahman.agendacalendar.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckedTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.jaiselrahman.agendacalendar.R;
import com.jaiselrahman.agendacalendar.model.Event;
import com.jaiselrahman.agendacalendar.view.AgendaCalendar;

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


        agendaCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
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
