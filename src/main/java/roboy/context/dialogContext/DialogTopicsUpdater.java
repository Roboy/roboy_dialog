package roboy.context.dialogContext;

import roboy.context.InternalListUpdater;
import roboy.context.InternalValueUpdater;
import roboy.context.Value;
import roboy.context.dataTypes.Topic;

/**
 * Updater available to all DM for adding new values to the DialogTopics attribute.
 */
public class DialogTopicsUpdater extends InternalListUpdater<Integer, String> {
    public DialogTopicsUpdater(DialogTopics target) {
        super(target);
    }
}
