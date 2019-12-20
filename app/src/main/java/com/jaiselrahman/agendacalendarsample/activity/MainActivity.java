package com.jaiselrahman.agendacalendarsample.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.Month;
import org.threeten.bp.MonthDay;
import org.threeten.bp.YearMonth;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.format.TextStyle;
import org.threeten.bp.temporal.ChronoField;

import java.util.ArrayList;
import java.util.Calendar;
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

        agendaCalendar.hideCalendar();

        agendaCalendar.hideElevationFor(findViewById(R.id.appBar));

        CheckedTextView currentMonth = findViewById(R.id.currentMonth);
        currentMonth.setText(dateFormatter.format(MonthDay.now().getMonth()));
        currentMonth.setOnClickListener(v -> {
            if (agendaCalendar.isCalendarViewVisible()) {
                agendaCalendar.hideCalendar();
            } else {
                agendaCalendar.showCalendar();
            }
            currentMonth.setChecked(!agendaCalendar.isCalendarViewVisible());
        });

        agendaCalendar.setListener(new AgendaCalendar.CalenderListener() {
            private Month lastMonth = MonthDay.now().getMonth();

            @Override
            public void onDayClick(LocalDate date) {
                agendaCalendar.scrollTo(date);
            }

            @Override
            public void onMonthScroll(YearMonth yearMonth) {
                currentMonth.setText(dateFormatter.format(yearMonth.getMonth()));

                if (lastMonth != yearMonth.getMonth()) {
                    agendaCalendar.scrollAgendaViewTo(yearMonth.atDay(1));
                }
                lastMonth = yearMonth.getMonth();
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
                callback.onResult(events.subList(0, params.pageSize), 0);
            }

            @Override
            public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Event> callback) {
                int toPosition = params.startPosition + params.loadSize;
                if (toPosition > events.size())
                    toPosition = events.size();
                callback.onResult(events.subList(params.startPosition, toPosition));
            }
        }, new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .build())
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .setNotifyExecutor(handler::post)
                .build();
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
                        "Description " + day,
                        "Location " + day,
                        LocalDateTime.now().withDayOfMonth(day),
                        Color.RED));
            case 3:
                cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, day);
                events.add(new Event("Event " + cal.get(Calendar.DAY_OF_MONTH),
                        "Description " + day,
                        "Location " + day,
                        LocalDateTime.now().withDayOfMonth(day),
                        Color.GREEN));
            case 2:
                cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, day);
                events.add(new Event("Event " + cal.get(Calendar.DAY_OF_MONTH),
                        "Description " + day,
                        "Location " + day,
                        LocalDateTime.now().withDayOfMonth(day),
                        Color.BLUE));
            case 1:
                cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, day);
                events.add(new Event("Event " + cal.get(Calendar.DAY_OF_MONTH),
                        "Description " + day,
                        "Location " + day,
                        LocalDateTime.now().withDayOfMonth(day),
                        Color.MAGENTA));
        }
        return events;
    }

}
