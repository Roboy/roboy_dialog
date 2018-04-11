package roboy.context.contextObjects;

import roboy.context.ROSTopicUpdater;
import roboy.ros.RosMainNode;
import roboy.ros.RosSubscribers;

/**
 * For testing a ROS topic connection which sends simple String messages.
 */
public class ROSTestUpdater extends ROSTopicUpdater<std_msgs.String, ROSTest> {

    @Override
    protected RosSubscribers getTargetSubscriber() {
        return RosSubscribers.TEST_TOPIC;
    }

    public ROSTestUpdater(ROSTest target, RosMainNode node) {
        super(target, node);
    }

    @Override
    protected synchronized void update() {
        target.updateValue(message.getData());
    }

}
