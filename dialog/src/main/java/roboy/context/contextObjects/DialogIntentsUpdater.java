package roboy.context.contextObjects;

import roboy.context.InternalUpdater;

/**
 * Update the history of intents
 */
public class DialogIntentsUpdater extends InternalUpdater<DialogIntents, IntentValue> {
    public DialogIntentsUpdater(DialogIntents target) {
        super(target);
    }
}
