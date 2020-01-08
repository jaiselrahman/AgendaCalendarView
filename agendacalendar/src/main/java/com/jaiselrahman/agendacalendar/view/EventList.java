package com.jaiselrahman.agendacalendar.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.AsyncPagedListDiffer;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.jaiselrahman.agendacalendar.model.BaseEvent;
import com.jaiselrahman.agendacalendar.util.EventCache;

import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class EventList<E extends BaseEvent, T extends List<E>> {
    protected EventCallback<E> eventCallback;

    public EventList(EventCallback<E> eventCallback) {
        this.eventCallback = eventCallback;
    }

    public abstract void setEvents(T events);

    public abstract E get(int position);

    public abstract int size();

    public int indexOf(BaseEvent event) {
        int left = 0, right = size();
        while (left < right) {
            final int middle = (left + right) / 2;
            E currentItem = get(middle);
            final int cmp = currentItem.compareTo(event);

            if (cmp < 0) {
                left = middle + 1;
            } else if (cmp == 0) {
                int index = middle;
                for (int i = index - 1; i >= 0; i--) {
                    E prevItem = get(i);
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

    public int possibleIndexOf(BaseEvent event) {
        return indexOf(event);
    }

    abstract void setOnEventSetListener(OnEventSetListener onEventSetListener);

    abstract void setAdapter(RecyclerView.Adapter adapter);

    public static abstract class EventCallback<T> {

        public abstract boolean areEventsTheSame(@NonNull T oldEvent, @NonNull T newEvent);

        public abstract boolean areContentsTheSame(@NonNull T oldEvent, @NonNull T newEvent);

        @Nullable
        public Object getChangePayload(@NonNull T oldEvent, @NonNull T newEvent) {
            return null;
        }
    }

    public static class PagedEventList<E extends BaseEvent> extends EventList<E, PagedList<E>> {
        private OnEventSetListener onEventSetListener;
        private AsyncPagedListDiffer<E> differ;

        public PagedEventList(EventCallback<E> eventCallback) {
            super(eventCallback);
        }

        @Override
        void setAdapter(RecyclerView.Adapter adapter) {
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    refreshEventCache(positionStart, itemCount);
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    refreshEventCache(positionStart, itemCount);
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    refreshEventCache(positionStart, itemCount);
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    refreshEventCache(fromPosition, itemCount);
                }
            });

            differ = new AsyncPagedListDiffer<>(adapter, new DiffUtil.ItemCallback<E>() {
                @Override
                public boolean areItemsTheSame(@NonNull E oldItem, @NonNull E newItem) {
                    return eventCallback.areEventsTheSame(oldItem, newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull E oldItem, @NonNull E newItem) {
                    return eventCallback.areContentsTheSame(oldItem, newItem);
                }
            });
        }

        @Override
        public void setEvents(PagedList<E> events) {
            differ.submitList(events);
        }

        @Override
        public E get(int position) {
            return differ.getItem(position);
        }

        @Override
        public int size() {
            return differ.getItemCount();
        }

        @Override
        public int possibleIndexOf(BaseEvent event) {
            if (differ.getCurrentList() == null)
                return -1;
            else {
                int pos = super.indexOf(event);
                if (pos == -1) {
                    pos = Collections.binarySearch(differ.getCurrentList().snapshot(), event, BaseEvent::compareTo);
                    return (pos < 0) ? Math.abs(pos) - 1 : pos;
                } else {
                    return pos;
                }
            }
        }

        @Override
        public void setOnEventSetListener(OnEventSetListener onEventSetListener) {
            this.onEventSetListener = onEventSetListener;
        }

        private void refreshEventCache(int positionStart, int itemCount) {
            int end = positionStart + itemCount;

            if (size() > end) {
                EventCache.clearAll();
            } else for (int i = positionStart; i < end && i < size(); i++) {
                EventCache.clear(get(i).getTime().toLocalDate());
            }

            if (onEventSetListener != null) {
                onEventSetListener.onEventSet();
            }
        }
    }

    public static class SortedEventList<E extends BaseEvent> extends EventList<E, List<E>> {
        private OnEventSetListener onEventSetListener;
        private RecyclerView.Adapter adapter;
        private EventCallback<E> eventCallback;

        public SortedEventList(EventCallback<E> eventCallback) {
            super(eventCallback);
            this.eventCallback = eventCallback;
        }

        @Override
        void setAdapter(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        private SortedList<BaseEvent> eventItems = new SortedList<>(BaseEvent.class, new SortedList.Callback<BaseEvent>() {
            @Override
            public int compare(BaseEvent o1, BaseEvent o2) {
                return o1.compareTo(o2);
            }

            @Override
            public boolean areContentsTheSame(BaseEvent oldItem, BaseEvent newItem) {
                //noinspection unchecked
                return eventCallback.areEventsTheSame((E) oldItem, (E) newItem);
            }

            @Override
            public boolean areItemsTheSame(BaseEvent item1, BaseEvent item2) {
                //noinspection unchecked
                return eventCallback.areEventsTheSame((E) item1, (E) item2);
            }

            @Override
            public void onChanged(int position, int count) {
                adapter.notifyItemChanged(position, count);
            }

            @Override
            public void onInserted(int position, int count) {
                adapter.notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                adapter.notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                adapter.notifyItemMoved(fromPosition, toPosition);
            }
        });

        @Override
        public void setEvents(List<E> events) {
            eventItems.beginBatchedUpdates();

            //noinspection unchecked
            eventItems.replaceAll((List<BaseEvent>) events);
            fillNoEvents();
            EventCache.clearAll();

            eventItems.endBatchedUpdates();

            if (onEventSetListener != null)
                onEventSetListener.onEventSet();
        }

        @Override
        public E get(int position) {
            //noinspection unchecked
            return (E) eventItems.get(position);
        }

        @Override
        public int size() {
            return eventItems.size();
        }

        @Override
        public void setOnEventSetListener(OnEventSetListener onEventSetListener) {
            this.onEventSetListener = onEventSetListener;
        }

        private void fillNoEvents() {
            List<BaseEvent> emptyEvents = new ArrayList<>();

            int toBeAdded = 0, eventSize = eventItems.size();
            BaseEvent lastChecked = eventItems.get(toBeAdded);

            while (toBeAdded < eventSize) {
                boolean hasEvent = false;

                for (int j = toBeAdded; j < eventSize; ) {
                    BaseEvent event = eventItems.get(j);
                    long dayDiff = ChronoUnit.DAYS.between(lastChecked.getTime(), event.getTime());
                    if (dayDiff <= 1) {
                        lastChecked = event;
                        hasEvent = true;
                        toBeAdded = ++j;
                    } else {
                        break;
                    }
                }

                if (!hasEvent) {
                    lastChecked = new BaseEvent.Empty(lastChecked.getTime().plusDays(1));
                    emptyEvents.add(lastChecked);
                }
            }

            eventItems.addAll(emptyEvents);
        }
    }

    public interface OnEventSetListener {
        void onEventSet();
    }
}
