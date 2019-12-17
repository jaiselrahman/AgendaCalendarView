package com.jaiselrahman.agendacalendar.util;

import androidx.recyclerview.widget.SortedList;

import com.jaiselrahman.agendacalendar.model.BaseEvent;

public class EventUtils {

    public static int searchEvent(SortedList<BaseEvent> list, Object item) {
        int left = 0, right = list.size();
        while (left < right) {
            final int middle = (left + right) / 2;
            BaseEvent currentItem = list.get(middle);
            final int cmp = currentItem.compareTo(item);

            if (cmp < 0) {
                left = middle + 1;
            } else if (cmp == 0) {
                int index = middle;
                for (int i = index - 1; i >= 0; i--) {
                    BaseEvent prevItem = list.get(i);
                    if (prevItem.compareTo(currentItem) != 0) break;
                    index = i;
                }
                return index;
            } else {
                right = middle;
            }
        }
        return -1;
    }
}
