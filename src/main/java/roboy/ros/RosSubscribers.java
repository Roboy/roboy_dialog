package roboy.ros;

import roboy_communication_cognition.*;

public enum RosSubscribers {
    DIRECTION_VECTOR("roboy_audio", "/roboy/cognition/audio/direction_of_arrival", DirectionVector._TYPE),
    FACE_COORDINATES("roboy_vision", "/roboy/cognition/vision/coordinates", FaceCoordinates._TYPE),
    NEW_FACIAL_FEATURES("roboy_vision", "/roboy/cognition/vision/features", NewFacialFeatures._TYPE),
    TEST_TOPIC("roboy_test", "/roboy", std_msgs.String._TYPE);

    String rosPackage;
    String address;
    String type;

    RosSubscribers(String rosPackage, String address, String type) {
        this.rosPackage=rosPackage;
        this.address=address;
        this.type=type;
    }
}
