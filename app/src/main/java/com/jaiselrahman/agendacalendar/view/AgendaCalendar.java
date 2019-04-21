package com.jaiselrahman.agendacalendar.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.jaiselrahman.agendacalendar.R;
import com.jaiselrahman.agendacalendar.adapter.EventAdapter;
import com.jaiselrahman.agendacalendar.model.BaseEvent;

import java.util.Date;
import java.util.List;

public class AgendaCalendar extends LinearLayout {
    private CompactCalendarView compactCalendarView;
    private AgendaView agendaView;

    public AgendaCalendar(Context context) {
        this(context, null);
    }

    public AgendaCalendar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AgendaCalendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AgendaCalendar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        setOrientation(VERTICAL);

        compactCalendarView = new CompactCalendarView(context, attrs, defStyleAttr);
        compactCalendarView.setVisibility(GONE);
        compactCalendarView.setId(R.id.calendar_view);
        addView(compactCalendarView);

        agendaView = new AgendaView(context, attrs, defStyleAttr);
        agendaView.setId(R.id.agenda_view);
        addView(agendaView);
    }

    public void setEvents(List<? extends BaseEvent> events) {
        agendaView.setEvents(events);
        for (BaseEvent event : events) {
            compactCalendarView.addEvent(new Event(Color.BLUE, event.getTime().getTimeInMillis()));
        }
    }

    public void scrollTo(long time) {
        agendaView.scrollTo(time);
        compactCalendarView.setCurrentDate(new Date(time));
    }

    public void setOnEventClickListener(EventAdapter.OnEventClickListener onEventClickListener) {
        agendaView.setOnEventClickListener(onEventClickListener);
    }

    public void showCalendar() {
        if (compactCalendarView.getVisibility() == GONE) {
            compactCalendarView.setVisibility(VISIBLE);
        }
        compactCalendarView.showCalendar();
    }

    public void hideCalendar() {
        compactCalendarView.hideCalendar();
    }

    public void setHeightAnimDuration(int durationMillis) {
        compactCalendarView.setHeightAnimDuration(durationMillis);
    }

    public void setIndicatorAnimDuration(int durationMillis) {
        compactCalendarView.setIndicatorAnimDuration(durationMillis);
    }

    public void setListener(CompactCalendarView.CompactCalendarViewListener compactCalendarViewListener) {
        compactCalendarView.setListener(compactCalendarViewListener);
    }
}
