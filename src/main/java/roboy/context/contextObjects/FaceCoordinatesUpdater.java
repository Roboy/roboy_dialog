package roboy.context.contextObjects;

import roboy.context.IntervalUpdater;

import java.util.Random;

/**
 * Asynchronously triggers ROS queries for face coordinates (in the future).
 */
public class FaceCoordinatesUpdater extends IntervalUpdater<FaceCoordinates> {
    static final int updateFrequencySeconds = 1;

    public FaceCoordinatesUpdater(FaceCoordinates target) {
        super(target, updateFrequencySeconds);
    }

    @Override
    protected void update() {
        Random r = new Random();
        // TODO replace dummy functionality! (And also update the test accordingly.)
        CoordinateSet set = new CoordinateSet(r.nextDouble(), r.nextDouble(), r.nextDouble());
        target.updateValue(set);
    }
}
