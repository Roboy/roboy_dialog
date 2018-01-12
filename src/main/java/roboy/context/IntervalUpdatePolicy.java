package roboy.context;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * An implementation of the UpdatePolicy which performs regular updates on a target object.
 * The method update() needs to be implemented in the subclass.
 * @param <T> The class of the target object.
 */
public abstract class IntervalUpdatePolicy<T> extends AsyncUpdatePolicy {
    protected final T target;
    protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public final int updateFrequency;

    public IntervalUpdatePolicy(T target, int updateFrequencySeconds) {
        this.target = target;
        updateFrequency = updateFrequencySeconds;
        start();
    }

    /**
     * Starts the updating thread.
     */
    private void start() {
        final Runnable updater = new Runnable() {
            @Override
            public void run() {
                IntervalUpdatePolicy.this.update();
            }
        };
        final ScheduledFuture<?> updaterHandle = scheduler.scheduleAtFixedRate(
                updater, updateFrequency, updateFrequency, SECONDS);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                updaterHandle.cancel(true);
            }
        }, 60 * 60, SECONDS);
    }

}