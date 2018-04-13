package roboy.memory;

/**
 * Contains the relations available in Neo4j database.
 * Respective questions should be added to the questions.json file
 * and used in the QuestionRandomizerState.
 */
public enum Neo4jProperties {
    name("name"),
    sex("sex");

    public String type;

    Neo4jProperties(String type) {
        this.type=type;
    }
}
