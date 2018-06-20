package roboy.context.contextObjects;

/**
 * A coordinate set data structure for the interlocutor face.
 */
public class CoordinateSet {
    final double x;
    final double y;
    final double z;

    public CoordinateSet(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        //TODO for demo purposes, rewrite once Vision integrated.
        return ("x:"+Math.round(x*100)+" y:"+Math.round(y*100)+" z:"+Math.round(z*100));
    }
}
