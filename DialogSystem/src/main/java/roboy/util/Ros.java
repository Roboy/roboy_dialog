package roboy.util;

public class Ros {

    private static edu.wpi.rail.jrosbridge.Ros ros;

//    private static final String ROS_URL = "localhost";

    private Ros(String ros_url) {
        ros = new edu.wpi.rail.jrosbridge.Ros(ros_url);
        ros.connect();
        System.out.println("ROS bridge is set up");
    }

    public static edu.wpi.rail.jrosbridge.Ros getInstance(){
        if(ros == null){
            System.out.println("ROS bridge has not been set up yet. Shutting down...");
            System.exit(0);
        }
        return ros;
    }

    public static void close() {
        ros.disconnect();
    }
}
