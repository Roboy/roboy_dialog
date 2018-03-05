package roboy.context.contextObjects;

import roboy.context.ValueHistory;

/**
 * Store the history of intents
 */
public class DialogIntents extends ValueHistory<String> {
    // Limit amount of history entries to 15
    @Override
    public int getMaxLimit() {
        return 15;
    }
}
