package roboy.context.dialogContext;

import roboy.context.DirectUpdatePolicy;
import roboy.context.dataTypes.Topic;

/**
 * Updater available to all DM for adding new values to the DialogTopics attribute.
 */
public class DialogTopicsUpdater implements DirectUpdatePolicy<Topic>{
    DialogTopics target;

    public DialogTopicsUpdater(DialogTopics target) {
        this.target = target;
    }

    /**
     * Put a new Topic value to the DialogTopics history.
     * @param value The topic object to add.
     */
    @Override
    public void putValue(Topic value) {
        target.storeValue(value);
    }
}
