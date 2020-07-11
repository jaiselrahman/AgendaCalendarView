package com.jaiselrahman.agendacalendar.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.elevation.ElevationOverlayProvider;
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
import com.kizitonwose.calendarview.ui.CalendarAdapter;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import kotlin.Unit;

public class AgendaCalendar extends CoordinatorLayout implements NestedScrollingChild {
    private static final String[] DAYS_OF_WEEK = DateUtils.getDaysOfWeek();

    private LocalDate today = LocalDate.now();
    private LocalDate selectedDay = today;

    private CalendarView calendarView;
    private AgendaView agendaView;

    private CalendarListener calendarListener;

    private View hideElevationFor = null;
    private AppBarLayout calendarViewParent;

    private NestedScrollingChildHelper nestedScrollingChildHelper;

    private boolean onInit = true;

    int calendarBackgroundColor;

    int calendarDateColor;
    float calendarDateFontSize;

    int calendarCurrentDayColor;
    int calendarCurrentDayBackgroundRes;

    int calendarSelectedDayColor;
    int calendarSelectedDayBackgroundRes;

    boolean calendarShowAdjacentMonthDate;
    int calendarAdjacentMonthDateColor;

    int agendaDateColor;
    int agendaCurrentDayColor;
    int agendaCurrentDayBackgroundRes;

    private EventList.OnEventSetListener onEventSetListener = new EventList.OnEventSetListener() {
        @Override
        public <E extends BaseEvent> void onEventSet(List<E> events) {
            if (events != null) {
                Set<YearMonth> yearMonths = new HashSet<>();

                for (E event : events) {
                    YearMonth yearMonth = YearMonth.from(event.getTime());

                    if (yearMonths.add(yearMonth)) {
                        calendarView.notifyMonthChanged(yearMonth);
                    }
                }
            }

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
        nestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);

        View v = inflate(context, R.layout.agendar_calendar, this);

        Resources res = getResources();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AgendaCalendar, defStyleAttr, 0);

        calendarBackgroundColor = a.getColor(R.styleable.AgendaCalendar_calendarBackground, res.getColor(R.color.colorPrimary));
        calendarDateColor = a.getColor(R.styleable.AgendaCalendar_calendarDateColor, res.getColor(android.R.color.primary_text_dark));
        calendarDateFontSize = a.getDimensionPixelOffset(R.styleable.AgendaCalendar_calendarDateFontSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, res.getDisplayMetrics()));

        calendarCurrentDayColor = a.getColor(R.styleable.AgendaCalendar_calendarCurrentDayColor, res.getColor(android.R.color.white));
        calendarCurrentDayBackgroundRes = a.getResourceId(R.styleable.AgendaCalendar_calendarCurrentDayBackground, R.drawable.current_day);

        calendarSelectedDayColor = a.getColor(R.styleable.AgendaCalendar_calendarSelectedDayColor, res.getColor(android.R.color.white));
        calendarSelectedDayBackgroundRes = a.getResourceId(R.styleable.AgendaCalendar_calendarSelectedDayBackground, R.drawable.selected_day);

        calendarShowAdjacentMonthDate = a.getBoolean(R.styleable.AgendaCalendar_calendarShowAdjacentMonthDate, false);
        calendarAdjacentMonthDateColor = a.getColor(R.styleable.AgendaCalendar_calendarAdjacentMonthDateColor, res.getColor(android.R.color.secondary_text_dark));

        agendaDateColor = a.getColor(R.styleable.AgendaCalendar_agendaDateColor, res.getColor(android.R.color.primary_text_light));
        agendaCurrentDayColor = a.getColor(R.styleable.AgendaCalendar_agendaCurrentDayColor, res.getColor(android.R.color.white));
        agendaCurrentDayBackgroundRes = a.getResourceId(R.styleable.AgendaCalendar_agendaCurrentDayBackground, R.drawable.current_day);

        a.recycle();

        calendarViewParent = v.findViewById(R.id.calendarViewParent);
        calendarViewParent.setExpanded(false, false);
        calendarViewParent.setBackgroundColor(calendarBackgroundColor);
        calendarViewParent.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            verticalOffset = Math.abs(verticalOffset);

            //Fully visible
            if (verticalOffset == 0 && calendarListener != null) {
                calendarListener.onCalendarVisibilityChange(true);
            }

            //Partially visible
            if (verticalOffset < appBarLayout.getTotalScrollRange()) {
                if (hideElevationFor != null)
                    ViewCompat.setElevation(hideElevationFor, 0);
            }

            //Fully collapsed
            if (verticalOffset == appBarLayout.getTotalScrollRange()) {
                if (calendarListener != null) {
                    calendarListener.onCalendarVisibilityChange(false);
                }
            }
        });

        calendarView = v.findViewById(R.id.calendarView);

        calendarView.setScrollMode(ScrollMode.PAGED);
        calendarView.setOrientation(RecyclerView.HORIZONTAL);

        calendarView.setDayHeight(getResources().getDimensionPixelSize(R.dimen.calendar_day_height));
        calendarView.setDayWidth(getResources().getDisplayMetrics().widthPixels / 7);

        agendaView = v.findViewById(R.id.agendaView);
        agendaView.setItemAnimator(null);
        agendaView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                BaseEvent event = agendaView.firstVisibleEvent();
                if (event != null && newState != RecyclerView.SCROLL_STATE_DRAGGING) {
                    YearMonth yearMonth = YearMonth.from(event.getTime().toLocalDate());
                    //noinspection ConstantConditions,KotlinInternalInJava
                    calendarView.scrollToPosition(((CalendarAdapter) calendarView.getAdapter())
                            .getAdapterPosition$com_github_kizitonwose_CalendarView(yearMonth));
                    if (calendarListener != null) {
                        calendarListener.onMonthScroll(yearMonth);
                    }
                }
            }
        });

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

        calendarView.setMonthScrollListener(calendarMonth -> {

            if (calendarListener != null) {
                calendarListener.onMonthScroll(calendarMonth.getYearMonth());
            }

            LocalDate date = calendarMonth.getYearMonth().atDay(1);
            if (today.equals(selectedDay)
                    && date.getYear() == today.getYear()
                    && date.getMonthValue() == today.getMonthValue()) {
                date = today;
            }

            setSelectedDate(date);
            scrollAgendaViewTo(date);

            return Unit.INSTANCE;
        });
    }

    public <E extends BaseEvent, T extends List<E>> void setAdapter(EventAdapter<E, T> eventAdapter) {
        eventAdapter.setCurrentDayBackground(agendaCurrentDayBackgroundRes);
        eventAdapter.setCurrentDayTextColor(agendaCurrentDayColor);
        eventAdapter.setDayTextColor(agendaDateColor);
        eventAdapter.setOnEventSetListener(onEventSetListener);
        agendaView.setAdapter(eventAdapter);
    }

    public void scrollTo(LocalDate date) {
        scrollCalendarTo(date);
        scrollAgendaViewTo(date);
        setSelectedDate(date);
    }

    public void scrollAgendaViewTo(LocalDate date) {
        agendaView.scrollTo(date);
    }

    public void scrollCalendarTo(LocalDate date) {
        if (!(selectedDay.getYear() == date.getYear()
                && selectedDay.getMonthValue() == date.getMonthValue()))
            calendarView.smoothScrollToDate(date);
    }

    public void showCalendar() {
        calendarViewParent.setExpanded(true);
    }

    public void hideCalendar() {
        calendarViewParent.setExpanded(false);
    }

    public void hideElevationFor(@NonNull AppBarLayout toolbar) {
        this.hideElevationFor = toolbar;

        if (isNightMode()) {
            calendarBackgroundColor = new ElevationOverlayProvider(getContext()).compositeOverlayIfNeeded(calendarBackgroundColor, 4, calendarViewParent);
            calendarViewParent.setBackgroundColor(calendarBackgroundColor);

            ViewCompat.setElevation(toolbar, 0);
            toolbar.setBackgroundColor(calendarBackgroundColor);
        }
    }

    public void setListener(CalendarListener calendarListener) {
        this.calendarListener = calendarListener;
        //noinspection ConstantConditions
        ((CDayBinder) calendarView.getDayBinder()).setCalendarListener(calendarListener);
    }

    public interface CalendarListener {
        void onDayClick(LocalDate date);

        void onMonthScroll(YearMonth yearMonth);

        default void onCalendarVisibilityChange(boolean isVisible) {
        }
    }

    private void setSelectedDate(LocalDate date) {
        if (selectedDay != null && selectedDay.equals(date)) return;

        if (calendarView.isComputingLayout()) return;

        if (selectedDay != null)
            calendarView.notifyDateChanged(selectedDay);

        selectedDay = date;

        if (selectedDay != null)
            calendarView.notifyDateChanged(selectedDay);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
        super.onNestedPreScroll(target, dx, dy, consumed, type);
        dispatchNestedPreScroll(dx, dy, consumed, null);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null);
    }

    @Override
    public void onStopNestedScroll(View target, int type) {
        super.onStopNestedScroll(target, type);
        stopNestedScroll();
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes, int type) {
        boolean handled = super.onStartNestedScroll(child, target, nestedScrollAxes, type);
        return startNestedScroll(nestedScrollAxes) || handled;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        boolean handled = super.onStartNestedScroll(child, target, nestedScrollAxes);
        return startNestedScroll(nestedScrollAxes) || handled;
    }

    @Override
    public void onStopNestedScroll(View target) {
        super.onStopNestedScroll(target);
        stopNestedScroll();
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        boolean handled = super.onNestedPreFling(target, velocityX, velocityY);
        return dispatchNestedPreFling(velocityX, velocityY) || handled;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        boolean handled = super.onNestedFling(target, velocityX, velocityY, consumed);
        return dispatchNestedFling(velocityX, velocityY, consumed) || handled;
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return nestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        nestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return nestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        nestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return nestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return nestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return nestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return nestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    private class CDayBinder implements DayBinder<DayViewContainer> {
        private AgendaView agendaView;
        private CalendarListener calendarListener;

        CDayBinder(AgendaView agendaView) {
            this.agendaView = agendaView;
        }

        @Override
        @NonNull
        public DayViewContainer create(@NonNull View view) {
            return new DayViewContainer(view, calendarListener);
        }

        @Override
        public void bind(@NonNull DayViewContainer container, @NotNull CalendarDay day) {
            @SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
            List<BaseEvent> events = ((EventAdapter) agendaView.getAdapter()).getEventsOn(day.getDate());

            int[] eventColors = new int[events.size()];
            for (int i = 0; i < eventColors.length; i++) {
                eventColors[i] = events.get(i).getColor();
            }
            container.bind(day, eventColors);
        }

        void setCalendarListener(CalendarListener calendarListener) {
            this.calendarListener = calendarListener;
        }
    }

    private class DayViewContainer extends ViewContainer {
        private CalendarDay currentDay;
        private TextView textView;
        private EventIndicatorView indicator;

        DayViewContainer(View view, CalendarListener calendarListener) {
            super(view);
            textView = view.findViewById(R.id.calendarDayText);
            indicator = view.findViewById(R.id.eventIndicator);

            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, calendarDateFontSize);

            view.setOnClickListener(v -> {
                setSelectedDate(currentDay.getDate());
                scrollAgendaViewTo(currentDay.getDate());
                calendarListener.onDayClick(currentDay.getDate());
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

    private boolean isNightMode() {
        return (getContext().getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}
