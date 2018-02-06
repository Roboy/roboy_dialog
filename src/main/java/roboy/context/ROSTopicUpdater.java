package roboy.context;

import org.ros.message.MessageListener;
import roboy.ros.RosMainNode;

public abstract class ROSTopicUpdater<Message,Target> extends ExternalUpdater {
    protected final Target target;
    protected volatile Message message;

    public ROSTopicUpdater(Target target, RosMainNode ros) {
        this.target = target;
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

    /**
     * Implement this to add the listener over the ROS main node, for example:
     * ros.addDummyListener(listener);
     */
    protected abstract void addListener(MessageListener listener, RosMainNode ros);
}
