package roboy.context.contextObjects;

import roboy.util.ConfigManager;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Currently dummy functionality.
 * In the future, could trigger the rotation of the head towards the speaker.
 * Observes the last location head was turned towards, and calls action if difference passes a threshold.
 *
 * TODO: To implement head turning, change the dummy functionality in turnHead() method.
 */

public class FaceCoordinatesObserver implements Observer {
    double lastUpdatedX;
    double lastUpdatedY;
    double lastUpdatedZ;
    long nextUpdateTime;

    static double TRIGGER_DIFFERENCE = 0.5;
    static long UPDATE_INTERVAL_MILLIS = 10000;

    // An ExecutorService is used for the turnHead method.
    // This way, update() returns without having to wait for return from ROS call to turn head.
    // With ExecutorService, avoid the overhead of creating new Thread for each turn.
    ExecutorService executor;

    public FaceCoordinatesObserver() {
        executor = Executors.newSingleThreadExecutor();
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
                executor.submit(() -> turnHead(newValue.x, newValue.y, newValue.z));
            }
        }
    }

    public void turnHead(double x, double y, double z) {
        lastUpdatedX = x;
        lastUpdatedY = y;
        lastUpdatedZ = z;
        nextUpdateTime = System.currentTimeMillis() + UPDATE_INTERVAL_MILLIS;
        if (ConfigManager.CONTEXT_GUI_ENABLED) {
            System.out.println("[FaceCoordinatesObserver] Triggering head turning.");
        }
    }
}
