package roboy.context.contextObjects;

import roboy.context.ROSTopicUpdater;
import roboy.ros.RosMainNode;
import roboy.ros.RosSubscribers;
import roboy_communication_cognition.DirectionVector;

/**
 * Pushes new values sent by the Audio ROS topic into the AudioDirection value history.
 */
public class AudioDirectionUpdater extends ROSTopicUpdater<DirectionVector, AudioDirection> {

    public AudioDirectionUpdater(AudioDirection target, RosMainNode node) {
        super(target, node);
    }

    @Override
    protected synchronized void update() {
        target.updateValue(message);
    }

    @Override
    protected RosSubscribers getTargetSubscriber() {
        return RosSubscribers.DIRECTION_VECTOR;
    }

}
