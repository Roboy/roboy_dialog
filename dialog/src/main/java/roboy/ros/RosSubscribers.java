package roboy.ros;

import roboy_communication_cognition.*;
import roboy_communication_control.Strings;
import std_msgs.Int32;


public enum RosSubscribers {
    DIRECTION_VECTOR("roboy_audio", "/roboy/cognition/audio/direction_of_arrival", DirectionVector._TYPE),
    FACE_COORDINATES("roboy_vision", "/roboy/cognition/vision/coordinates", FaceCoordinates._TYPE),
    NEW_FACIAL_FEATURES("roboy_vision", "/roboy/cognition/vision/features", NewFacialFeatures._TYPE),
    TEST_TOPIC("roboy_test", "/roboy", std_msgs.String._TYPE),
    DETECTED_OBJECTS("roboy_vision", "/roboy/cognition/vision/detected_objects", Strings._TYPE),
    NUMBER_PEOPLE_AROUND("roboy_vision", "/roboy/cognition/vision/people_around", std_msgs.Int8._TYPE),
    PERSON_LISTENING("roboy_vision", "/roboy/cognition/vision/person_listening", std_msgs.Bool._TYPE),
    BOOTH_SENTENCE("roboy_nodered", "/roboy/cognition/nodered/boothsentence", std_msgs.String._TYPE),
    CUP_GAME_READY("roboy_soli", "/roboy/control/ball", std_msgs.Bool._TYPE),
    CUP_GAME_STATE("roboy_soli", "/roboy/control/smach", std_msgs.String._TYPE),
    CUP_GAME_CUP("roboy_soli", "/roboy/control/ball_detected", Int32._TYPE)

    ;

    String rosPackage;
    String address;
    String type;

    RosSubscribers(String rosPackage, String address, String type) {
        this.rosPackage=rosPackage;
        this.address=address;
        this.type=type;
    }
}
