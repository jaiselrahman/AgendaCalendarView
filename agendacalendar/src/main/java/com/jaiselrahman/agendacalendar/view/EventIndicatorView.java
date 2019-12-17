package com.jaiselrahman.agendacalendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class EventIndicatorView extends View {
    private int[] eventColors;
    private float radius;
    private float offset;
    private float padding;

    private Paint dayPaint = new Paint();

    public EventIndicatorView(Context context) {
        super(context);
    }

    public EventIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EventIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        padding = radius = MeasureSpec.getSize(heightMeasureSpec) / 2;
        offset = MeasureSpec.getSize(widthMeasureSpec) / 2;
    }

    public void setEventColors(int[] eventColors) {
        this.eventColors = eventColors;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawEvents(canvas);
    }

    private void drawEvents(Canvas canvas) {
        switch (eventColors.length) {
            case 0:
                return;
            case 1:
                drawSingleEvent(canvas, eventColors[0]);
                break;
            case 2:
                drawTwoEvents(canvas, eventColors);
                break;
            case 3:
                drawThreeEvents(canvas, eventColors);
                break;
            default:
                drawThreeEventsPlus(canvas, eventColors);
        }
    }

    private void drawSingleEvent(Canvas canvas, int color) {
        drawCircle(canvas, offset, color);
    }

    private void drawTwoEvents(Canvas canvas, int[] color) {
        drawCircle(canvas, offset - radius - padding / 2, color[0]);
        drawCircle(canvas, offset + radius + padding / 2, color[1]);

    }

    private void drawThreeEvents(Canvas canvas, int[] color) {
        drawCircle(canvas, offset - (radius * 2) - padding, color[0]);
        drawCircle(canvas, offset * 1, color[1]);
        drawCircle(canvas, offset + (radius * 2) + padding, color[2]);
    }

    private void drawThreeEventsPlus(Canvas canvas, int[] color) {
        drawCircle(canvas, offset - (radius * 2) - padding, color[0]);
        drawCircle(canvas, offset * 1, color[1]);
        drawPlus(canvas, offset + (radius * 2) + padding, radius, color[2], (int) (radius / 2));
    }

    private void drawCircle(Canvas canvas, float x, int color) {
        dayPaint.setColor(color);
        dayPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, radius, radius, dayPaint);
    }

    private void drawPlus(Canvas canvas, float x, float y, int color, int width) {
        dayPaint.setColor(color);
        dayPaint.setStrokeWidth(width);
        canvas.drawLine(x - radius, y, x + radius, y, dayPaint);
        canvas.drawLine(x, y - radius, x, y + radius, dayPaint);
        dayPaint.setStrokeWidth(0);
    }
}
