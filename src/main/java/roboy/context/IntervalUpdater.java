package roboy.context;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * An implementation of the UpdatePolicy which performs regular updates on a target object.
 * The method update() needs to be implemented in the subclass.
 *
 * @param <T> The class of the target object.
 */
public abstract class IntervalUpdater<T> extends ExternalUpdater {
    protected final T target;
    protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public final int updateFrequency;

    /**
     * Create a new updater service, executing the update() method at regular time intervals.
     *
     * @param target                 The target attribute of the update() method.
     * @param updateFrequencySeconds Delay in seconds between calls to the update() method.
     */
    public IntervalUpdater(T target, int updateFrequencySeconds) {
        this.target = target;
        updateFrequency = updateFrequencySeconds;
        start();
    }

    /**
     * Starts the ScheduledExecutorService of the updating thread.
     */
    private void start() {
        final Runnable updater = () -> update();
        // Schedules regular updates, starting 1 second after initialization.
        final ScheduledFuture<?> updaterHandle = scheduler.scheduleAtFixedRate(
                updater, 1, updateFrequency, SECONDS);
        // Cancel each scheduled task after 30 seconds of runtime - prevent excessive threads if the goal is down.
        scheduler.schedule((Runnable) () -> updaterHandle.cancel(true), 30, SECONDS);
    }

}
