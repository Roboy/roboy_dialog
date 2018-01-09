package roboy.context.visionContext;

import roboy.context.UpdatePolicy;
import roboy.context.dataTypes.CoordinateSet;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Asynchronously triggers ROS queries for face coordinates (in the future).
 */
public class FaceCoordinatesUpdater implements UpdatePolicy {
    // The attribute instance which is updated after each run.
    private final FaceCoordinates target;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    // Update frequency in seconds.
    public static final int updateFrequency = 1;

    public FaceCoordinatesUpdater(FaceCoordinates target) {
        this.target = target;
        final Runnable updater = () -> update();
        final ScheduledFuture<?> updaterHandle = scheduler.scheduleAtFixedRate(updater,
                updateFrequency, updateFrequency, SECONDS);
        scheduler.schedule((Runnable) () -> updaterHandle.cancel(true), 60 * 60, SECONDS);
    }

    @Override
    public void update() {
        Random r = new Random();
        // TODO replace dummy functionality! (And also update the test accordingly.)
        CoordinateSet set = new CoordinateSet(r.nextDouble(),r.nextDouble(),r.nextDouble());
        target.storeValue(set);
    }
}
