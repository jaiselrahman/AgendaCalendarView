package com.jaiselrahman.agendacalendar.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

public class AgendaView extends RecyclerView {
    private EventAdapter eventAdapter = null;
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

        linearLayoutManager = new LinearLayoutManager(context);
        super.setLayoutManager(linearLayoutManager);

        setItemAnimator(null);
    }

    public void scrollTo(long time) {
        if (eventAdapter == null) return;

        int pos = eventAdapter.getPosition(time);
        if (pos >= 0) {
            linearLayoutManager.scrollToPositionWithOffset(pos, 0);
        }
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
        if (adapter instanceof EventAdapter) {
            eventAdapter = (EventAdapter) adapter;
            super.setAdapter(eventAdapter);
            addItemDecoration(new StickyHeaderDecoration(eventAdapter.getEventStickyHeader(), false));
            return;
        }
        if (isInEditMode()) {
            super.setAdapter(adapter);
            return;
        }
        throw new RuntimeException("Adapter should not be changed");
    }
}
