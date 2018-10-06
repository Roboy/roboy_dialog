package roboy.context.contextObjects;

import roboy.context.ROSTopicUpdater;
import roboy.ros.RosMainNode;
import roboy.ros.RosSubscribers;

/**
 * For testing a ROS topic connection which sends simple String messages.
 */
public class CupGameSmachStateUpdater extends ROSTopicUpdater<std_msgs.String, CupGameSmachState> {

    @Override
    protected RosSubscribers getTargetSubscriber() {
        return RosSubscribers.CUP_GAME_STATE;
    }

    public CupGameSmachStateUpdater(CupGameSmachState target, RosMainNode node) {
        super(target, node);
    }

    @Override
    protected synchronized void update() {
        target.updateValue(message.getData());
    }

}
