package roboy.context.visionContext;

import roboy.context.DataType;

/**
 * A coordinate set data structure for the interlocutor face.
 */
public class CoordinateSet implements DataType {
    final double x;
    final double y;
    final double z;

    public CoordinateSet(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
