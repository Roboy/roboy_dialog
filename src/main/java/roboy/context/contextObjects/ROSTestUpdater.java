package roboy.context.contextObjects;

import org.ros.message.MessageListener;
import roboy.context.ROSTopicUpdater;
import roboy.ros.RosMainNode;
import roboy.ros.msg.DirVec;
import roboy_communication_cognition.SpeechSynthesis;

import java.util.Random;

public class ROSTestUpdater extends ROSTopicUpdater<std_msgs.String, ROSTest> {

    public ROSTestUpdater(ROSTest target, RosMainNode node) {
        super(target, node);
    }

    @Override
    protected synchronized void update() {
        target.updateValue(message.getData());
    }

    @Override
    protected void addListener(MessageListener listener, RosMainNode ros) {
        ros.addDummyListener(listener);
    }
}
