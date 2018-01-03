package roboy.context.visionContext;

import roboy.context.SimpleHistory;

import java.util.HashMap;
import java.util.UUID;

/**
 * Functional HashMap-based History for Coordinates.
 */
public class CoordinateHistory extends SimpleHistory<String, CoordinateSet> {

    CoordinateHistory() {
        this.data = new HashMap<String, CoordinateSet>();
    }

    @Override
    public String generateKey() {
        return UUID.randomUUID().toString();
    }
}
