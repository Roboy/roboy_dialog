package roboy.context.dataTypes;

public class Topic {
    public final String topic;

    public Topic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return topic;
    }
}
