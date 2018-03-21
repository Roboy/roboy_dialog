package roboy.context.contextObjects;

import roboy.memory.Neo4jRelationships;

public class IntentValue {
    private String id;
    private Neo4jRelationships value;

    public IntentValue(String stateIdentifier, Neo4jRelationships intentValue) {
        id = stateIdentifier;
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
