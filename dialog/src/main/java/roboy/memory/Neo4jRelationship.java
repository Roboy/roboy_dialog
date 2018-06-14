package roboy.memory;

import com.google.common.collect.Maps;

import java.util.Map;

import static roboy.memory.Neo4jLabel.*;

/**
 * Contains the relations available in Neo4j database.
 * Respective questions should be added to the questions.json file
 * and used in the QuestionRandomizerState.
 */
public enum Neo4jRelationship {
    FROM("FROM"),
    HAS_HOBBY("HAS_HOBBY"),
    LIVE_IN("LIVE_IN"),
    STUDY_AT("STUDY_AT"),
    OCCUPIED_AS("OCCUPIED_AS"),
    WORK_FOR("WORK_FOR"),
    FRIEND_OF("FRIEND_OF"),
    MEMBER_OF("MEMBER_OF"),
    CHILD_OF("CHILD_OF"),
    SIBLING_OF("SIBLING_OF"),
    KNOW("KNOW"),
    OTHER("OTHER"),
    IS("IS");

    public String type;

    Neo4jRelationship(String type) {
        this.type = type;
    }

    public static Neo4jLabel determineNodeType(Neo4jRelationship relationship) {
        // TODO expand list as new Node types are added.
        if(relationship.equals(HAS_HOBBY)) return Hobby;
        if(relationship.equals(FROM)) return Country;
        if(relationship.equals(WORK_FOR) || relationship.equals(STUDY_AT)) return Organization;
        if(relationship.equals(OCCUPIED_AS)) return Occupation;
        if(relationship.equals(OTHER)) return Other;
        else return None;
    }

    private static final Map<String, Neo4jRelationship> typeIndex =
            Maps.newHashMapWithExpectedSize(Neo4jRelationship.values().length);

    static {
        for (Neo4jRelationship relationship : Neo4jRelationship.values()) {
            typeIndex.put(relationship.type, relationship);
        }
    }

    public static Neo4jRelationship lookupByType(String type) {
        return typeIndex.get(type);
    }

    public static boolean contains(String type){
        return typeIndex.containsKey(type);
    }
}
