package roboy.ros;

import roboy_communication_cognition.*;

public enum RosSubscribers {
    DIRECTION_VECTOR("/roboy/cognition/audio/direction_of_arrival", DirectionVector._TYPE),
    FACE_COORDINATES("/roboy/cognition/vision/coordinates", FaceCoordinates._TYPE),
    NEW_FACIAL_FEATURES("/roboy/cognition/vision/features", NewFacialFeatures._TYPE),
    TEST_TOPIC("/roboy", std_msgs.String._TYPE);

    String address;
    String type;

    RosSubscribers(String address, String type) {
        this.address=address;
        this.type=type;
    }
}
