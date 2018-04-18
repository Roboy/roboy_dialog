package roboy.memory;

/**
 * Contains the relations available in Neo4j database.
 * Respective questions should be added to the questions.json file
 * and used in the QuestionRandomizerState.
 */
public enum Neo4jRelationships {
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
    OTHER("OTHER"),
    IS("IS");

    public String type;

    Neo4jRelationships(String type) {
        this.type=type;
    }

    public static String determineNodeType(String relationship) {
        // TODO expand list as new Node types are added.
        if(relationship.equals(HAS_HOBBY.type)) return "Hobby";
        if(relationship.equals(FROM.type)) return "Country";
        if(relationship.equals(WORK_FOR.type)) return "Organization";
        if(relationship.equals(STUDY_AT.type)) return "Organization";
        if(relationship.equals(OCCUPIED_AS.type)) return "Occupation";
        if(relationship.equals(OTHER.type)) return "Other";
        else return "";
    }
}
