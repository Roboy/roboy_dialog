package roboy.util;

/**
 * Communication with ROS.
 */
public class Ros {

    private static edu.wpi.rail.jrosbridge.Ros ros;

    private static final String ROS_URL = System.getenv("ROS_IP");

    private Ros() {
        ros = new edu.wpi.rail.jrosbridge.Ros(ROS_URL);
        ros.connect();
        System.out.println("ROS bridge is set up");
    }

    public static edu.wpi.rail.jrosbridge.Ros getInstance(){
        if(ros == null){
        	new Ros();
        }
        return ros;
    }

    public static void close() {
        ros.disconnect();
    }
}
