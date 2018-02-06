package roboy.ros;

import roboy_communication_cognition.*;
import roboy_communication_control.*;
import std_msgs.String;

/**
 * Stores the different client addresses and corresponding ROS message types.
 */

enum RosClients {
    SPEECHSYNTHESIS("/roboy/cognition/speech/synthesis/talk", Talk._TYPE, true),
    GENERATIVE("/roboy/cognition/generative_nlp/answer", GenerateAnswer._TYPE, true),
    FACEDETECTION("/speech_synthesis/talk", DetectFace._TYPE, true),
    OBJECTRECOGNITION("/speech_synthesis/talk", RecognizeObject._TYPE, true),
    STT("/roboy/cognition/speech/recognition", RecognizeSpeech._TYPE, true),
    EMOTION("/roboy/control/face/emotion", ShowEmotion._TYPE, true),
    CREATEMEMORY("/roboy/cognition/memory/create", DataQuery._TYPE, true),
    UPDATEMEMORY("/roboy/cognition/memory/update", DataQuery._TYPE, true),
    GETMEMORY("/roboy/cognition/memory/get", DataQuery._TYPE, true),
    DELETEMEMORY("/roboy/cognition/memory/remove", DataQuery._TYPE, true),
    CYPHERMEMORY("/roboy/cognition/memory/cypher", DataQuery._TYPE, true),
    INTENT("/roboy/cognition/detect_intent", DetectIntent._TYPE, true),
    DUMMY_SPEECH_SYNTH("dummy/address", SpeechSynthesis._TYPE, false),
    TEST_TOPIC("/roboy", String._TYPE, false);

    java.lang.String address;
    java.lang.String type;
    boolean isService;

    RosClients(java.lang.String address, java.lang.String type, boolean isService) {
        this.address=address;
        this.type=type;
        this.isService = isService;
    }
}
