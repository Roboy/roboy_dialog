package roboy.context.contextObjects;

import roboy.context.ROSTopicUpdater;
import roboy.ros.RosMainNode;
import roboy.ros.RosSubscribers;

/**
 * Pushes new values sent by the Person Listening ROS topic into the DetectedPerson value history.
 */
public class DetectedPersonUpdater extends ROSTopicUpdater<std_msgs.Bool, DetecedPerson> {

    public DetectedPersonUpdater(DetecedPerson target, RosMainNode node) {
        super(target, node);
    }

    @Override
    protected synchronized void update() {
        target.updateValue(message);
    }

    @Override
    protected RosSubscribers getTargetSubscriber() {
        return RosSubscribers.PERSON_LISTENING;
    }

}
