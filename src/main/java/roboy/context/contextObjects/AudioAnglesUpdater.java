package roboy.context.contextObjects;

import org.ros.message.MessageListener;
import roboy.context.ExternalUpdater;
import roboy.ros.RosMainNode;
import roboy.ros.msg.DirVec;
import roboy_communication_cognition.SpeechSynthesis;

import java.util.Random;

public class AudioAnglesUpdater extends ExternalUpdater {
    static SpeechSynthesis message;
    AudioAngles target;

    AudioAnglesUpdater(AudioAngles target) {
        this.target = target;
        MessageListener<SpeechSynthesis> listener = new MessageListener<SpeechSynthesis>() {
            @Override
            public void onNewMessage(SpeechSynthesis speechSynthesis) {
                message = speechSynthesis;
                update();
            }
        };
        RosMainNode node = new RosMainNode();
        node.addDummyListener(listener);
    }

    @Override
    protected void update() {
        // TODO Extract parts, like message.getDuration();
        DirVec value = new DirVec();
        Random random = new Random();
        value.azimutal_angle = random.nextFloat();
        value.polar_angle = random.nextFloat();
        target.updateValue(value);
    }
}
