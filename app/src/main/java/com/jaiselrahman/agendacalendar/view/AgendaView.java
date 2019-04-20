package com.jaiselrahman.agendacalendar.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaiselrahman.agendacalendar.adapter.EventAdapter;
import com.jaiselrahman.agendacalendar.model.BaseEvent;

import java.util.List;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

public class AgendaView extends RecyclerView {
    private EventAdapter eventAdapter = new EventAdapter();
    private LinearLayoutManager linearLayoutManager;

    public AgendaView(@NonNull Context context) {
        this(context, null);
    }

    public AgendaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AgendaView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (isInEditMode()) return;

        super.setAdapter(eventAdapter);

        linearLayoutManager = new LinearLayoutManager(context);
        super.setLayoutManager(linearLayoutManager);

        addItemDecoration(new StickyHeaderDecoration(eventAdapter, false));
    }

    public void setEvents(List<? extends BaseEvent> events) {
        eventAdapter.setEvents(events);
    }

    public void scrollTo(long time) {
        int pos = eventAdapter.getPosition(time);
        if (pos >= 0) {
            linearLayoutManager.scrollToPositionWithOffset(pos, 0);
        }
    }

    public void setOnEventClickListener(EventAdapter.OnEventClickListener onEventClickListener) {
        eventAdapter.setOnEventClickListener(onEventClickListener);
    }

    @Override
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public final void setLayoutManager(@Nullable LayoutManager layout) {
        if (isInEditMode()) {
            super.setLayoutManager(layout);
            return;
        }
        throw new RuntimeException("LayoutManager cannot be changed");
    }

    @Override
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public final void setAdapter(@Nullable Adapter adapter) {
        if (isInEditMode()) {
            super.setAdapter(adapter);
            return;
        }
        throw new RuntimeException("Adapter should not be changed");
    }
}
