package roboy.context.visionContext;

import roboy.context.Attribute;
import roboy.context.dataTypes.CoordinateSet;

/**
 * xzy-coordinates of a person in the field of vision.
 */
public class FaceCoordinates extends Attribute {

    FaceCoordinates() {
        history = new CoordinateHistory();
        lastKey = history.storeValue(new CoordinateSet(0,0,0));
    }

    @Override
    public synchronized Object getLatestValue() {
        return history.getValue(lastKey);
    }

    @Override
    public synchronized Object getValue(Object key) {
        return history.getValue(key);
    }

    public synchronized void putValue(CoordinateSet set) {
        lastKey = history.storeValue(set);
    }
}
