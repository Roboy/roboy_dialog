package roboy.context.dialogContext;

import roboy.context.InternalListUpdater;

/**
 * Updater available to all DM for adding new values to the DialogTopics attribute.
 */
public class DialogTopicsUpdater extends InternalListUpdater<Integer, String> {
    public DialogTopicsUpdater(DialogTopics target) {
        super(target);
    }
}
