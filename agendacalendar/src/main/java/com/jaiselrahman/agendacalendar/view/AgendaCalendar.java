package com.jaiselrahman.agendacalendar.view;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
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
import com.kizitonwose.calendarview.model.InDateStyle;
import com.kizitonwose.calendarview.model.OutDateStyle;
import com.kizitonwose.calendarview.model.ScrollMode;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.YearMonth;
import org.threeten.bp.temporal.WeekFields;

import java.util.List;
import java.util.Locale;

import kotlin.Unit;

public class AgendaCalendar extends CoordinatorLayout {
    private static final String[] DAYS_OF_WEEK = DateUtils.getDaysOfWeek();

    private LocalDate today = LocalDate.now();
    private LocalDate selectedDay = null;

    private CalendarView calendarView;
    private AgendaView agendaView;

    private CalenderListener calenderListener;

    private View hideElevationFor = null;
    private AppBarLayout calendarViewParent;

    private boolean onInit = true;

    int calendarDateColor;
    float calendarDateFontSize;

    int calendarCurrentDayColor;
    int calendarCurrentDayBackgroundRes;

    int calendarSelectedDayColor;
    int calendarSelectedDayBackgroundRes;

    boolean calendarShowAdjacentMonthDate;
    int calendarAdjacentMonthDateColor;

    private EventList.OnEventSetListener onEventSetListener = new EventList.OnEventSetListener() {
        @Override
        public void onEventSet() {
            calendarView.notifyMonthChanged(YearMonth.now());
            if (onInit) {
                agendaView.scrollTo(LocalDate.now());
                onInit = false;
            }
        }
    };

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

        Resources res = getResources();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AgendaCalendar, defStyleAttr, 0);

        int calendarBackgroundColor = a.getColor(R.styleable.AgendaCalendar_calendarBackground, res.getColor(R.color.colorPrimary));
        calendarDateColor = a.getColor(R.styleable.AgendaCalendar_calendarDateColor, res.getColor(android.R.color.primary_text_dark));
        calendarDateFontSize = a.getDimensionPixelOffset(R.styleable.AgendaCalendar_calendarDateFontSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, res.getDisplayMetrics()));

        calendarCurrentDayColor = a.getColor(R.styleable.AgendaCalendar_calendarCurrentDayColor, res.getColor(android.R.color.white));
        calendarCurrentDayBackgroundRes = a.getResourceId(R.styleable.AgendaCalendar_calendarCurrentDayBackground, R.drawable.current_day);

        calendarSelectedDayColor = a.getColor(R.styleable.AgendaCalendar_calendarSelectedDayColor, res.getColor(android.R.color.white));
        calendarSelectedDayBackgroundRes = a.getResourceId(R.styleable.AgendaCalendar_calendarSelectedDayBackground, R.drawable.selected_day);

        calendarShowAdjacentMonthDate = a.getBoolean(R.styleable.AgendaCalendar_calendarShowAdjacentMonthDate, false);
        calendarAdjacentMonthDateColor = a.getColor(R.styleable.AgendaCalendar_calendarAdjacentMonthDateColor, res.getColor(android.R.color.secondary_text_dark));

        a.recycle();

        calendarViewParent = v.findViewById(R.id.calendarViewParent);
        calendarViewParent.setExpanded(false, false);
        calendarViewParent.setBackgroundColor(calendarBackgroundColor);
        calendarViewParent.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            verticalOffset = Math.abs(verticalOffset);

            //Fully visible
            if (verticalOffset == 0 && calenderListener != null) {
                calenderListener.onCalendarVisibilityChange(true);
            }

            //Partially visible
            if (verticalOffset < appBarLayout.getTotalScrollRange()) {
                if (hideElevationFor != null)
                    ViewCompat.setElevation(hideElevationFor, 0);
            }

            //Fully collapsed
            if (verticalOffset == appBarLayout.getTotalScrollRange()) {
                if (calenderListener != null) {
                    calenderListener.onCalendarVisibilityChange(false);
                }
            }
        });

        calendarView = v.findViewById(R.id.calendarView);

        calendarView.setScrollMode(ScrollMode.PAGED);
        calendarView.setOrientation(RecyclerView.HORIZONTAL);

        calendarView.setDayHeight(getResources().getDimensionPixelSize(R.dimen.calendar_day_height));
        calendarView.setDayWidth(getResources().getDisplayMetrics().widthPixels / 7);

        agendaView = v.findViewById(R.id.agendaView);

        if (calendarShowAdjacentMonthDate) {
            calendarView.setInDateStyle(InDateStyle.ALL_MONTHS);
            calendarView.setOutDateStyle(OutDateStyle.END_OF_ROW);
        } else {
            calendarView.setInDateStyle(InDateStyle.ALL_MONTHS);
            calendarView.setOutDateStyle(OutDateStyle.NONE);
        }

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

        calendarView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            }
        });

        calendarView.setMonthScrollListener(calendarMonth -> {
            if (calenderListener != null) {
                calenderListener.onMonthScroll(calendarMonth.getYearMonth());
            }
            setSelectedDate(calendarMonth.getYearMonth().atDay(1));
            return Unit.INSTANCE;
        });

        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        setLayoutTransition(layoutTransition);
    }

    public <E extends BaseEvent, T extends List<E>> void setAdapter(EventAdapter<E, T> eventAdapter) {
        eventAdapter.setOnEventSetListener(onEventSetListener);
        agendaView.setAdapter(eventAdapter);
    }

    public void scrollTo(LocalDate date) {
        scrollAgendaViewTo(date);
        scrollCalendarTo(date);
    }

    public void scrollAgendaViewTo(LocalDate date) {
        agendaView.scrollTo(date);
    }

    public void scrollCalendarTo(LocalDate date) {
        calendarView.smoothScrollToMonth(YearMonth.of(date.getYear(), date.getMonth()));
    }

    public void showCalendar() {
        calendarViewParent.setExpanded(true);
    }

    public void hideCalendar() {
        calendarViewParent.setExpanded(false);
    }

    public void hideElevationFor(@NonNull AppBarLayout toolbar) {
        this.hideElevationFor = toolbar;
    }

    public void setListener(CalenderListener calenderListener) {
        this.calenderListener = calenderListener;
        ((CDayBinder) calendarView.getDayBinder()).setCalenderListener(calenderListener);
    }

    public interface CalenderListener {
        void onDayClick(LocalDate date);

        void onMonthScroll(YearMonth yearMonth);

        default void onCalendarVisibilityChange(boolean isVisible) {
        }
    }

    private void setSelectedDate(LocalDate date) {
        if (selectedDay != null)
            calendarView.notifyDateChanged(selectedDay);
        selectedDay = date;
        calendarView.notifyDateChanged(selectedDay);
    }

    private class CDayBinder implements DayBinder<DayViewContainer> {
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
            //noinspection unchecked
            List<BaseEvent> events = ((EventAdapter) agendaView.getAdapter()).getEventsOn(day.getDate());

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

    private class DayViewContainer extends ViewContainer {
        private View view;
        private CalendarDay currentDay;
        private TextView textView;
        private EventIndicatorView indicator;

        DayViewContainer(View view, CalenderListener calenderListener) {
            super(view);
            this.view = view;
            textView = view.findViewById(R.id.calendarDayText);
            indicator = view.findViewById(R.id.eventIndicator);

            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, calendarDateFontSize);

            view.setOnClickListener(v -> {
                calenderListener.onDayClick(currentDay.getDate());
                setSelectedDate(currentDay.getDate());
            });
        }

        void bind(CalendarDay calendarDay, int[] eventColors) {
            currentDay = calendarDay;

            textView.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));

            if (calendarDay.getOwner() == DayOwner.THIS_MONTH) {
                textView.setVisibility(View.VISIBLE);
                textView.setTextColor(calendarDateColor);
                indicator.setEventColors(eventColors);
            } else {
                if (calendarShowAdjacentMonthDate) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setTextColor(calendarAdjacentMonthDateColor);
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
                indicator.setEventColors(null);
            }

            if (calendarDay.getDate().equals(today)) {
                textView.setTextColor(calendarCurrentDayColor);
                textView.setBackgroundResource(calendarCurrentDayBackgroundRes);
            } else if (calendarDay.getDate().equals(selectedDay)) {
                textView.setTextColor(calendarSelectedDayColor);
                textView.setBackgroundResource(calendarSelectedDayBackgroundRes);
            } else {
                textView.setBackgroundResource(0);
            }
        }
    }

    private class CMonthHeaderBinder implements MonthHeaderFooterBinder<MonthHeaderContainer> {
        @Override
        public void bind(@NotNull MonthHeaderContainer monthHeaderContainer, @NotNull CalendarMonth calendarMonth) {
        }

        @NotNull
        @Override
        public MonthHeaderContainer create(@NotNull View view) {
            return new MonthHeaderContainer(view);
        }
    }

    private class MonthHeaderContainer extends ViewContainer {

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
                weekDays[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, calendarDateFontSize);
                weekDays[i].setTextColor(calendarDateColor);
            }
        }
    }
}
