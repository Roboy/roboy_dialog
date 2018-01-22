package roboy.context.visionContext;

import roboy.context.IntervalUpdater;
import roboy.context.dataTypes.CoordinateSet;

import java.util.Random;

/**
 * Asynchronously triggers ROS queries for face coordinates (in the future).
 */
public class FaceCoordinatesUpdater extends IntervalUpdater<FaceCoordinates> {
    public FaceCoordinatesUpdater(FaceCoordinates target, int updateFrequencySeconds) {
        super(target, updateFrequencySeconds);
    }

    @Override
    protected void update() {
        Random r = new Random();
        // TODO replace dummy functionality! (And also update the test accordingly.)
        CoordinateSet set = new CoordinateSet(r.nextDouble(),r.nextDouble(),r.nextDouble());
        target.updateValue(set);
    }
}
