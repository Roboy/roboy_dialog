package roboy.context.contextObjects;

import roboy.context.ROSTopicUpdater;
import roboy.ros.RosMainNode;
import roboy.ros.RosSubscribers;

/**
 * Pushes new values sent by the Booth Sentence ROS topic into the Booth Sentence value history.
 */
public class BoothSentenceUpdater extends ROSTopicUpdater<std_msgs.String, BoothSentence> {

    public BoothSentenceUpdater(BoothSentence target, RosMainNode node) {
        super(target, node);
    }

    @Override
    protected synchronized void update() {
        target.updateValue(message);
    }

    @Override
    protected RosSubscribers getTargetSubscriber() {
        return RosSubscribers.BOOTH_SENTENCE;
    }

}
