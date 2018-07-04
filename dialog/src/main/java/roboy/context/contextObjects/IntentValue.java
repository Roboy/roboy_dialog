package roboy.context.contextObjects;

import roboy.memory.Neo4jProperty;
import roboy.memory.Neo4jRelationship;

import java.util.Objects;

/**
 * The value of the question intent based on Neo4j Relationship or a string.
 * Referenced by the intents history id to distinguish between the States
 * which pushed the values to the history.
 */
public class IntentValue {
    private String id;
    private Neo4jRelationship neo4jRelationshipValue = null;
    private Neo4jProperty neo4jPropertyValue = null;
    private String stringValue;
    private String attribute = null;

    public IntentValue(String intentsHistoryId, Neo4jRelationship intentValue) {
        id = intentsHistoryId;
        neo4jRelationshipValue = intentValue;
        stringValue = neo4jRelationshipValue.type;
    }

    public IntentValue(String intentsHistoryId, Neo4jProperty intentValue) {
        id = intentsHistoryId;
        neo4jPropertyValue = intentValue;
        stringValue = neo4jRelationshipValue.type;
    }

    public IntentValue(String intentsHistoryId, String intentValue) {
        id = intentsHistoryId;
        neo4jRelationshipValue = null;
        stringValue = intentValue;
    }

    public IntentValue(String intentsHistoryId, Neo4jRelationship intentValue, String attribute) {
        id = intentsHistoryId;
        neo4jRelationshipValue = intentValue;
        stringValue = neo4jRelationshipValue.type;
        this.attribute = attribute;
    }

    public IntentValue(String intentsHistoryId, Neo4jProperty intentValue, String attribute) {
        id = intentsHistoryId;
        neo4jPropertyValue = intentValue;
        stringValue = neo4jPropertyValue.type;
        this.attribute = attribute;
    }

    public IntentValue(String intentsHistoryId, String intentValue, String attribute) {
        id = intentsHistoryId;
        neo4jRelationshipValue = null;
        stringValue = intentValue;
        this.attribute = attribute;
    }

    public String getId() {
        return id;
    }

    public Neo4jRelationship getNeo4jRelationshipValue() {
        return neo4jRelationshipValue;
    }

    public Neo4jProperty getNeo4jPropertyValue() {
        return neo4jPropertyValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public String getAttribute() {
        return attribute;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IntentValue)) {
            return false;
        }

        IntentValue comparableObject = (IntentValue) obj;

        boolean equality = false;

        if (stringValue != null) {
            equality = comparableObject.id.equals(this.id) && comparableObject.stringValue.equals(this.stringValue);
        }

        if (neo4jRelationshipValue != null) {
            equality = equality && comparableObject.neo4jRelationshipValue.equals(this.neo4jRelationshipValue);
        } else if (neo4jPropertyValue != null) {
            equality = equality && comparableObject.neo4jPropertyValue.equals(this.neo4jPropertyValue);
        }



        if (attribute != null) {
            equality = equality && comparableObject.attribute.equals(this.attribute);
        }

        return equality;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNeo4jRelationshipValue(), getNeo4jPropertyValue(), getStringValue(), getAttribute());
    }

    @Override
    public String toString() {
        return "IntentValue{" +
                "id='" + id + '\'' +
                ", neo4jRelationshipValue=" + neo4jRelationshipValue +
                ", neo4jPropertyValue=" + neo4jPropertyValue +
                ", stringValue='" + stringValue + '\'' +
                ", attribute='" + attribute + '\'' +
                '}';
    }
}
