package com.jaiselrahman.agendacalendar.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.jaiselrahman.agendacalendar.R;
import com.jaiselrahman.agendacalendar.model.BaseEvent;
import com.jaiselrahman.agendacalendar.util.DateUtils;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.model.ScrollMode;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.YearMonth;
import org.threeten.bp.temporal.WeekFields;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;

public class AgendaCalendar extends CoordinatorLayout {
    private CalendarView calendarView;
    private AgendaView agendaView;

    private CalenderListener calenderListener;

    private View hideElevationFor = null;
    private AppBarLayout calendarViewParent;

    private boolean isCalendarViewVisible = false;

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

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        View v = inflate(context, R.layout.agendar_calendar, this);

        calendarViewParent = v.findViewById(R.id.calendarViewParent);
        calendarViewParent.setExpanded(false, false);
        calendarViewParent.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            isCalendarViewVisible = verticalOffset == 0; // Not fully collapsed
            if (hideElevationFor != null &&
                    Math.abs(verticalOffset) < appBarLayout.getTotalScrollRange() // Fully collapsed
            ) {
                ViewCompat.setElevation(hideElevationFor, 0);
            }
        });

        calendarView = v.findViewById(R.id.calendarView);

        calendarView.setScrollMode(ScrollMode.PAGED);
        calendarView.setOrientation(RecyclerView.HORIZONTAL);
        calendarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        int dayHeight = getResources().getDimensionPixelSize(R.dimen.calendar_day_height);

        calendarView.setDayHeight(dayHeight);
        calendarView.setDayWidth(getResources().getDisplayMetrics().widthPixels / 7);
        calendarView.getLayoutParams().height = dayHeight * 7;

        calendarView.setDayBinder(new CDayBinder(agendaView));
        calendarView.setDayViewResource(R.layout.calendar_day_layout);
        calendarView.setMonthHeaderBinder(new CMonthHeaderBinder());
        calendarView.setMonthHeaderResource(R.layout.calendar_header);

        YearMonth currentYearMonth = YearMonth.now();
        YearMonth firstMonth = currentYearMonth.minusMonths(10);
        YearMonth lastMonth = currentYearMonth.plusMonths(10);
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();

        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek);
        calendarView.scrollToMonth(currentYearMonth);

        calendarView.setMonthScrollListener(calendarMonth -> {
            if (calenderListener != null) {
                calenderListener.onMonthScroll(DateTimeUtils.toSqlDate(calendarMonth.getYearMonth().atDay(1)));
            }
            return Unit.INSTANCE;
        });

        agendaView = v.findViewById(R.id.agendaView);
    }

    public <T extends BaseEvent> void setAdapter(EventAdapter<T> eventAdapter) {
        agendaView.setAdapter(eventAdapter);
        agendaView.scrollTo(System.currentTimeMillis());
    }

    public void scrollTo(long time) {
        scrollAgendaViewTo(time);
        scrollCalendarTo(time);
    }

    public void scrollAgendaViewTo(long time) {
        agendaView.scrollTo(time);
    }

    public void scrollCalendarTo(long time) {
        LocalDate date = DateTimeUtils.toLocalDate(new java.sql.Date(time));
        calendarView.smoothScrollToMonth(YearMonth.of(date.getYear(), date.getMonth()));
    }

    public void showCalendar() {
        calendarViewParent.setExpanded(true);
    }

    public void hideCalendar() {
        calendarViewParent.setExpanded(false);
    }

    public boolean isCalendarViewVisible() {
        return isCalendarViewVisible;
    }

    public void hideElevationFor(@NonNull AppBarLayout toolbar) {
        this.hideElevationFor = toolbar;
    }

    public void setListener(CalenderListener calenderListener) {
        this.calenderListener = calenderListener;
        ((CDayBinder) calendarView.getDayBinder()).setCalenderListener(calenderListener);
    }

    public interface CalenderListener {
        void onDayClick(Date dateClicked);

        void onMonthScroll(Date firstDayOfNewMonth);
    }

    private static class CDayBinder implements DayBinder<DayViewContainer> {
        private AgendaView agendaView;
        private CalenderListener calenderListener;

        CDayBinder(AgendaView agendaView) {
            this.agendaView = agendaView;
        }

        @Override
        @NonNull
        public DayViewContainer create(@NonNull View view) {
            return new DayViewContainer(view, calenderListener);
        }

        @Override
        public void bind(@NonNull DayViewContainer container, @NotNull CalendarDay day) {
            long time = TimeUnit.DAYS.toMillis(day.getDate().toEpochDay());
            //noinspection unchecked
            List<BaseEvent> events = ((EventAdapter) agendaView.getAdapter()).getEventsOn(time);

            int[] eventColors = new int[events.size()];
            for (int i = 0; i < eventColors.length; i++) {
                eventColors[i] = events.get(i).getColor();
            }
            container.bind(day, eventColors);
        }

        void setCalenderListener(CalenderListener calenderListener) {
            this.calenderListener = calenderListener;
        }
    }

    private static class DayViewContainer extends ViewContainer {
        private CalendarDay currentDay;
        private TextView textView;
        private EventIndicatorView indicator;

        DayViewContainer(View view, CalenderListener calenderListener) {
            super(view);
            textView = view.findViewById(R.id.calendarDayText);
            indicator = view.findViewById(R.id.eventIndicator);

            view.setOnClickListener(v -> calenderListener.onDayClick(DateTimeUtils.toSqlDate(currentDay.getDate())));
        }

        void bind(CalendarDay calendarDay, int[] eventColors) {
            currentDay = calendarDay;

            textView.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));
            if (calendarDay.getOwner() == DayOwner.THIS_MONTH) {
                textView.setTextColor(Color.BLACK);
            } else {
                textView.setTextColor(Color.GRAY);
            }

            indicator.setEventColors(eventColors);
        }
    }

    private static class CMonthHeaderBinder implements MonthHeaderFooterBinder<MonthHeaderContainer> {
        @Override
        public void bind(@NotNull MonthHeaderContainer monthHeaderContainer, @NotNull CalendarMonth calendarMonth) {
        }

        @NotNull
        @Override
        public MonthHeaderContainer create(@NotNull View view) {
            return new MonthHeaderContainer(view);
        }
    }

    private static class MonthHeaderContainer extends ViewContainer {
        private static final String[] DAYS_OF_WEEK = DateUtils.getDaysOfWeek();

        MonthHeaderContainer(View v) {
            super(v);

            TextView[] weekDays = new TextView[7];
            weekDays[0] = v.findViewById(R.id.weekDay1);
            weekDays[1] = v.findViewById(R.id.weekDay2);
            weekDays[2] = v.findViewById(R.id.weekDay3);
            weekDays[3] = v.findViewById(R.id.weekDay4);
            weekDays[4] = v.findViewById(R.id.weekDay5);
            weekDays[5] = v.findViewById(R.id.weekDay6);
            weekDays[6] = v.findViewById(R.id.weekDay7);

            for (int i = 0; i < DAYS_OF_WEEK.length; i++) {
                weekDays[i].setText(DAYS_OF_WEEK[i]);
            }
        }
    }
}
