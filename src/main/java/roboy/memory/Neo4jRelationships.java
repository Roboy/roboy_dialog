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
    OCCUPATION("OCCUPATION"),
    WORK_FOR("WORK_FOR"),
    FRIEND_OF("FRIEND_OF"),
    MEMBER_OF("MEMBER_OF");

    public String type;

    Neo4jRelationships(String type) {
        this.type=type;
    }
}
