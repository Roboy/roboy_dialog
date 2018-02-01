package roboy.context.contextObjects;

import roboy.dialog.Config;

import java.util.Observable;
import java.util.Observer;

/**
 * Currently dummy functionality.
 * In the future, could trigger the rotation of the head towards the speaker.
 * Observes the last location head was turned towards, and calls action if difference passes a threshold.
 */

public class FaceCoordinatesObserver implements Observer {
    double lastUpdatedX;
    double lastUpdatedY;
    double lastUpdatedZ;
    long nextUpdateTime;

    static double TRIGGER_DIFFERENCE = 0.5;
    static long UPDATE_INTERVAL_MILLIS = 10000;

    public FaceCoordinatesObserver() {
        nextUpdateTime = System.currentTimeMillis();
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof FaceCoordinates) {
            CoordinateSet newValue = ((FaceCoordinates) observable).getValue();
            if((nextUpdateTime < System.currentTimeMillis()) &&
                    (Math.abs(lastUpdatedX - newValue.x) > TRIGGER_DIFFERENCE
                    || Math.abs(lastUpdatedY - newValue.y) > TRIGGER_DIFFERENCE
                    || Math.abs(lastUpdatedZ - newValue.z) > TRIGGER_DIFFERENCE)) {
                turnHead(newValue.x, newValue.y, newValue.z);
            }
        }
    }

    public void turnHead(double x, double y, double z) {
        lastUpdatedX = x;
        lastUpdatedY = y;
        lastUpdatedZ = z;
        nextUpdateTime = System.currentTimeMillis() + UPDATE_INTERVAL_MILLIS;
        if (Config.CONTEXT_DEMO) {
            System.out.println("[FaceCoordinatesObserver] Triggering head turning.");
        }
    }
}
