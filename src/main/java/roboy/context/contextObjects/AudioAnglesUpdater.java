package roboy.context.contextObjects;

import org.ros.message.MessageListener;
import roboy.context.ROSTopicUpdater;
import roboy.ros.RosMainNode;
import roboy.ros.msg.DirVec;
import roboy_communication_cognition.SpeechSynthesis;

import java.util.Random;

public class AudioAnglesUpdater extends ROSTopicUpdater<SpeechSynthesis, AudioAngles> {

    public AudioAnglesUpdater(AudioAngles target, RosMainNode node) {
        super(target, node);
    }

    @Override
    protected synchronized void update() {
        // TODO Parse the real message.
        DirVec value = new DirVec();
        Random random = new Random();
        value.azimutal_angle = random.nextFloat();
        value.polar_angle = random.nextFloat();
        target.updateValue(value);
    }

    @Override
    protected void addListener(MessageListener listener, RosMainNode ros) {
        ros.addDummyListener(listener);
    }
}
