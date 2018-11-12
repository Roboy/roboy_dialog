package roboy.context.contextObjects;

import roboy.context.ROSTopicUpdater;
import roboy.ros.RosMainNode;
import roboy.ros.RosSubscribers;

/**
 * For testing a ROS topic connection which sends simple String messages.
 */
public class CupGameCupUpdater extends ROSTopicUpdater<std_msgs.Int32, CupGameCup> {

    @Override
    protected RosSubscribers getTargetSubscriber() {
        return RosSubscribers.CUP_GAME_CUP;
    }

    public CupGameCupUpdater(CupGameCup target, RosMainNode node) {
        super(target, node);
    }

    @Override
    protected synchronized void update() {
        target.updateValue(message.getData());
    }

}
