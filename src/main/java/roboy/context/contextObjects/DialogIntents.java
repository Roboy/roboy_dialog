package roboy.context.contextObjects;

import roboy.context.ValueHistory;

import java.util.HashMap;

/**
 * Store the history of intents
 */
public class DialogIntents extends ValueHistory<IntentValue> {
    // Limit amount of history entries to 15
    @Override
    public int getMaxLimit() {
        return 50;
    }

    public boolean isAttributePresent(String attribute) {
        HashMap<Integer, IntentValue> historyValues = this.getLastNValues(getMaxLimit());
        for (IntentValue value : historyValues.values()) {
            if (value.getAttribute().equals(attribute)) {
                return true;
            }
        }
        return false;
    }
}
