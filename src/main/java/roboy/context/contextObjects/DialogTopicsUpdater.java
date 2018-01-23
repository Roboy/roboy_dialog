package roboy.context.contextObjects;

import roboy.context.InternalUpdater;

/**
 * Updater available to all DM for adding new values to the DialogTopics attribute.
 */
public class DialogTopicsUpdater extends InternalUpdater<DialogTopics, String> {
    public DialogTopicsUpdater(DialogTopics target) {
        super(target);
    }
}
