package roboy.context;

import org.ros.message.MessageListener;
import roboy.ros.RosMainNode;
import roboy.ros.RosSubscribers;

public abstract class ROSTopicUpdater<Message,Target> extends ExternalUpdater {
    protected final Target target;
    protected volatile Message message;
    protected final RosSubscribers targetSubscriber;

    /**
     * Implement this in the subclass to define the ROS subscriber this updater should use.
     */
    protected abstract RosSubscribers getTargetSubscriber();


    public ROSTopicUpdater(Target target, RosMainNode ros) {
        this.target = target;
        targetSubscriber = getTargetSubscriber();
        start(ros);
    }

    /**
     * Starts a new MessageListener.
     */
    private void start(RosMainNode ros) {
        MessageListener<Message> listener = new MessageListener<Message>() {
            @Override
            public void onNewMessage(Message m) {
                message = m;
                update();
            }
        };
        addListener(listener, ros);
    }

    protected void addListener(MessageListener listener, roboy.ros.RosMainNode ros) {
        ros.addListener(listener, getTargetSubscriber());
    }
}
