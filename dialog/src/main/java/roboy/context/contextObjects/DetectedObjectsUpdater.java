package roboy.context.contextObjects;

import roboy.context.ROSTopicUpdater;
import roboy.ros.RosMainNode;
import roboy.ros.RosSubscribers;
import roboy_communication_control.Strings;

/**
 * Pushes new values sent by the Detected Objects ROS topic into the DetectedObjects value history.
 */
public class DetectedObjectsUpdater extends ROSTopicUpdater<Strings, DetectedObjects> {

    public DetectedObjectsUpdater(DetectedObjects target, RosMainNode node) {
        super(target, node);
    }

    @Override
    protected synchronized void update() {
        target.updateValue(message);
    }

    @Override
    protected RosSubscribers getTargetSubscriber() {
        return RosSubscribers.DETECTED_OBJECTS;
    }

}
