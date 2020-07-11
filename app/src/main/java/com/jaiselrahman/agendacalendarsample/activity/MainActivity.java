package com.jaiselrahman.agendacalendarsample.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckedTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.paging.PositionalDataSource;

import com.jaiselrahman.agendacalendar.view.AgendaCalendar;
import com.jaiselrahman.agendacalendarsample.MyEventAdapter;
import com.jaiselrahman.agendacalendarsample.R;
import com.jaiselrahman.agendacalendarsample.model.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private AgendaCalendar agendaCalendar;

    private DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
            .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT)
            .toFormatter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);

        List<Event> events = mockEvents();

        agendaCalendar = findViewById(R.id.calendar);

        MyEventAdapter myEventAdapter = new MyEventAdapter();
        agendaCalendar.setAdapter(myEventAdapter);
        myEventAdapter.setEvents(getPagedList());

        myEventAdapter.setOnEventClickListener(event -> {
            Toast.makeText(this, event.getTitle(), Toast.LENGTH_SHORT).show();
        });

        agendaCalendar.hideElevationFor(findViewById(R.id.appBar));

        CheckedTextView currentMonth = findViewById(R.id.currentMonth);
        currentMonth.setText(dateFormatter.format(MonthDay.now().getMonth()));
        currentMonth.setOnClickListener(v -> {
            if (currentMonth.isChecked()) {
                agendaCalendar.hideCalendar();
            } else {
                agendaCalendar.showCalendar();
            }
        });

        agendaCalendar.setListener(new AgendaCalendar.CalendarListener() {

            @Override
            public void onDayClick(LocalDate date) {

            }

            @Override
            public void onMonthScroll(YearMonth yearMonth) {
                currentMonth.setText(dateFormatter.format(yearMonth.getMonth()));
            }

            @Override
            public void onCalendarVisibilityChange(boolean isVisible) {
                currentMonth.setChecked(isVisible);
            }
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
            agendaCalendar.scrollTo(LocalDate.now());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private PagedList<Event> getPagedList() {
        Handler handler = new Handler();
        return new PagedList.Builder<>(new PositionalDataSource<Event>() {
            private List<Event> events = mockEvents();

            @Override
            public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Event> callback) {
                int start = params.requestedStartPosition;
                int toPosition = start + params.pageSize;
                if (toPosition > events.size())
                    toPosition = events.size();
                Log.d("AC", "load initial " + start + " " + toPosition);
                callback.onResult(events.subList(start, toPosition), start, events.size());
            }

            @Override
            public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Event> callback) {
                int toPosition = params.startPosition + params.loadSize;
                if (toPosition > events.size())
                    toPosition = events.size();
                Log.d("AC", "load range " + params.startPosition + " " + toPosition);
                callback.onResult(events.subList(params.startPosition, toPosition));
            }
        }, new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(50)
                .setPrefetchDistance(1)
                .build())
                .setInitialKey(100)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .setNotifyExecutor(handler::post)
                .build();
    }

    private List<Event> mockEvents() {
        LocalDateTime date = LocalDateTime.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        List<Event> events = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            date = date.plusDays(1);
            if (i % 2 == 0) {
                events.addAll(getEvents(date, 1));
            } else if (i % 3 == 0) {
                events.addAll(getEvents(date, 2));
            } else if (i % 5 == 0) {
                events.addAll(getEvents(date, 3));
            } else if (i == 1) {
                events.addAll(getEvents(date, 4));
            }
        }
        return events;
    }

    private List<Event> getEvents(LocalDateTime date, int count) {
        List<Event> events = new ArrayList<>();
        switch (count) {
            case 4:
                events.add(new Event("Event " + date.getDayOfMonth(),
                        "Description " + date.getDayOfMonth(),
                        "Location " + date.getDayOfMonth(),
                        date,
                        Color.RED));
            case 3:
                events.add(new Event("Event " + date.getDayOfMonth(),
                        "Description " + date.getDayOfMonth(),
                        "Location " + date.getDayOfMonth(),
                        date,
                        Color.GREEN));
            case 2:
                events.add(new Event("Event " + date.getDayOfMonth(),
                        "Description " + date.getDayOfMonth(),
                        "Location " + date.getDayOfMonth(),
                        date,
                        Color.BLUE));
            case 1:
                events.add(new Event("Event " + date.getDayOfMonth(),
                        "Description " + date.getDayOfMonth(),
                        "Location " + date.getDayOfMonth(),
                        date,
                        Color.MAGENTA));
        }
        return events;
    }

}
