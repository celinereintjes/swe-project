package com.alerts;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * A decorator that marks an alert as repeated and can schedule periodic callbacks.
 */
public class RepeatedAlertDecorator extends AlertDecorator {
    private final long repeatIntervalMs;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> future;
    private final AtomicBoolean repeating = new AtomicBoolean(false);

    public RepeatedAlertDecorator(Alert wrapped, long repeatIntervalMs) {
        super(wrapped);
        this.repeatIntervalMs = repeatIntervalMs;
    }

    public long getRepeatIntervalMs() {
        return repeatIntervalMs;
    }

    @Override
    public String getCondition() {
        return String.format("[REPEATED x%s] %s", repeatIntervalMs, super.getCondition());
    }

    /**
     * Start repeating; the provided callback will be invoked with this alert each interval.
     */
    public void startRepeating(Consumer<Alert> callback) {
        if (repeating.compareAndSet(false, true)) {
            future = scheduler.scheduleAtFixedRate(() -> callback.accept(this), repeatIntervalMs, repeatIntervalMs, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Stop scheduled repeating.
     */
    public void stopRepeating() {
        if (repeating.compareAndSet(true, false)) {
            if (future != null) future.cancel(false);
        }
    }
}
