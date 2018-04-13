package roboy.context.contextObjects;

import roboy.memory.Neo4jRelationships;

/**
 * The value of the question intent based on Neo4j Relationship.
 * Referenced by the intents history id to distinguish between the States
 * which pushed the values to the history.
 */
public class IntentValue {
    private String id;
    private Neo4jRelationships value;

    public IntentValue(String intentsHistoryId, Neo4jRelationships intentValue) {
        id = intentsHistoryId;
        value = intentValue;
    }

    public String getStateId() {
        return id;
    }

    public Neo4jRelationships getIntentValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IntentValue)) {
            return false;
        }

        IntentValue comparableObject = (IntentValue) obj;

        return comparableObject.id.equals(this.id) && comparableObject.value.equals(this.value);
    }
}
