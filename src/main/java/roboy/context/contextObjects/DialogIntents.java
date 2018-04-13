package roboy.context.contextObjects;

import roboy.context.ValueHistory;

/**
 * Store the history of intents
 */
public class DialogIntents extends ValueHistory<IntentValue> {
    // Limit amount of history entries to 15
    @Override
    public int getMaxLimit() {
        return 15;
    }
}
