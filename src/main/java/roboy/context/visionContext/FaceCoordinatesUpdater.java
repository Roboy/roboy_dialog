package roboy.context.visionContext;

import roboy.context.IntervalUpdatePolicy;
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
public class FaceCoordinatesUpdater extends IntervalUpdatePolicy<FaceCoordinates> {
    public FaceCoordinatesUpdater(FaceCoordinates target, int updateFrequencySeconds) {
        super(target, updateFrequencySeconds);
    }

    @Override
    protected void update() {
        Random r = new Random();
        // TODO replace dummy functionality! (And also update the test accordingly.)
        CoordinateSet set = new CoordinateSet(r.nextDouble(),r.nextDouble(),r.nextDouble());
        target.storeValue(set);
    }
}
