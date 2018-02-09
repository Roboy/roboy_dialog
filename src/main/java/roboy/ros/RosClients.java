package roboy.ros;

import roboy_communication_cognition.*;
import roboy_communication_control.*;

/**
 * Stores the different client addresses and corresponding ROS message types.
 */

enum RosClients {
    SPEECHSYNTHESIS("/roboy/cognition/speech/synthesis/talk", Talk._TYPE),
    GENERATIVE("/roboy/cognition/generative_nlp/answer", GenerateAnswer._TYPE),
    FACEDETECTION("/speech_synthesis/talk", DetectFace._TYPE),
    OBJECTRECOGNITION("/speech_synthesis/talk", RecognizeObject._TYPE),
    STT("/roboy/cognition/speech/recognition", RecognizeSpeech._TYPE),
    EMOTION("/roboy/control/face/emotion", ShowEmotion._TYPE),
    CREATEMEMORY("/roboy/cognition/memory/create", DataQuery._TYPE),
    UPDATEMEMORY("/roboy/cognition/memory/update", DataQuery._TYPE),
    GETMEMORY("/roboy/cognition/memory/get", DataQuery._TYPE),
    DELETEMEMORY("/roboy/cognition/memory/remove", DataQuery._TYPE),
    CYPHERMEMORY("/roboy/cognition/memory/cypher", DataQuery._TYPE),
    INTENT("/roboy/cognition/detect_intent", DetectIntent._TYPE);

    String address;
    String type;

    RosClients(String address, String type) {
        this.address=address;
        this.type=type;
    }
}
