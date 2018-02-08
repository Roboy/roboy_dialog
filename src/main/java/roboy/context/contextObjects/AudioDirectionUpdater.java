package roboy.context.contextObjects;

import com.google.gson.Gson;
import org.ros.message.MessageListener;
import roboy.context.ROSTopicUpdater;
import roboy.ros.RosMainNode;
import roboy.ros.RosSubscribers;
import roboy_communication_cognition.DirectionVector;

public class AudioDirectionUpdater extends ROSTopicUpdater<DirectionVector, AudioDirection> {
    Gson gson;

    public AudioDirectionUpdater(AudioDirection target, RosMainNode node) {
        super(target, node);
        gson = new Gson();
    }

    @Override
    protected synchronized void update() {
        // TODO Test if parsing works.
        target.updateValue(gson.toJson(message, roboy.ros.msg.DirectionVector.class));
    }

    @Override
    protected RosSubscribers getTargetSubscriber() {
        return RosSubscribers.DIRECTION_VECTOR;
    }

}
