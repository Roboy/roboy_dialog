package roboy.context.contextObjects;

import roboy.context.ROSTopicUpdater;
import roboy.ros.RosMainNode;
import roboy.ros.RosSubscribers;

/**
 * Pushes new values sent by the People Around ROS topic into the PeopleAround value history.
 */
public class PeopleAroundUpdater extends ROSTopicUpdater<std_msgs.Int8, PeopleAround> {

    public PeopleAroundUpdater(PeopleAround target, RosMainNode node) {
        super(target, node);
    }

    @Override
    protected synchronized void update() {
        target.updateValue(message);
    }

    @Override
    protected RosSubscribers getTargetSubscriber() {
        return RosSubscribers.NUMBER_PEOPLE_AROUND;
    }

}
