package roboy.memory;

import com.google.common.collect.Maps;

import java.util.HashSet;
import java.util.Map;

/**
 * Contains the relations available in Neo4j database.
 * Respective questions should be added to the questions.json file
 * and used in the QuestionRandomizerState.
 */
public enum Neo4jLabel {
    Person("Person"),
    TelegramPerson("Telegram_person"),
    FacebookPerson("Facebook_person"),
    SlackPerson("Slack_person"),
    Robot("Robot"),
    Company("Company"),
    University("University"),
    City("City"),
    Country("Country"),
    Hobby("Hobby"),
    Occupation("Occupation"),
    Object("Object"),
    Location("Location"),
    Organization("Organization"),
    Other("Other"),
    None("");

    public String type;

    Neo4jLabel(String type) {
        this.type = type;
    }

    private static final Map<String, Neo4jLabel> typeIndex =
            Maps.newHashMapWithExpectedSize(Neo4jLabel.values().length);

    static {
        for (Neo4jLabel label : Neo4jLabel.values()) {
            typeIndex.put(label.type, label);
        }
    }

    public static Neo4jLabel lookupByType(String type) {
        return typeIndex.get(type);
    }

    public static boolean contains(String type){
        return typeIndex.containsKey(type);
    }
}
