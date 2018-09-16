package roboy.util;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.memory.Neo4jProperty;
import roboy.memory.Neo4jRelationship;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Getting values for personalStates and follow-up questions
 * from a JSON file
 * Parses files containing predefined questions and answers
 *
 * Expects the following input pattern:
 * {
 * "INTENT": {
 *      "Q": [
 *          "Question phrasing 1",
 *          "Question phrasing 2",
 *          "Question phrasing 3"
 *      ],
 *      "A": {
 *          "SUCCESS": [
 *              "Possible answer on success 1",
 *              "Possible answer on success 2"
 *          ],
 *          "FAILURE": [
 *              "Possible answer on failure"
 *          ]
 *      }
 *      "FUP": {
 *          "Q": [
 *              "Possible follow up question"
 *          ],
 *          "A": [
 *              "Possible follow up answer"
 *          ]
 *      }
 *  }
 * }
 *
 * See more examples in resources/sentences
 */
public class QAJsonParser {
    private final Logger LOGGER = LogManager.getLogger();

    private Map<String, RandomList<String>> questions;
    private Map<String, RandomList<String>> successAnswers;
    private Map<String, RandomList<String>> failureAnswers;
    private Map<String, RandomList<String>> followUpQuestions;
    private Map<String, RandomList<String>> followUpAnswers;

    private Gson gson;

    public QAJsonParser(String file) {
        parse(file);
    }

    public boolean parse(String file) {
        gson = new Gson();

        questions = new HashMap<>();
        successAnswers = new HashMap<>();
        failureAnswers = new HashMap<>();
        followUpQuestions = new HashMap<>();
        followUpAnswers = new HashMap<>();

        JsonParser jsonParser = new JsonParser();
        URL url = null;
        try {
            url = Paths.get(file).toUri().toURL();
        } catch (MalformedURLException e) {
            LOGGER.error("File not found, URL is incorrect: " + e.getMessage());
        }

        try {
            if (url != null) {
                String jsonString = Resources.toString(url, Charsets.UTF_8);
                JsonElement jsonTree = jsonParser.parse(jsonString);
                Set<Map.Entry<String, JsonElement>> entrySet = ((JsonObject) jsonTree).entrySet();
                Type listType = new TypeToken<RandomList<String>>() {}.getType();
                for (Map.Entry<String, JsonElement> entry : entrySet) {

                    JsonElement questionsJson = entry.getValue().getAsJsonObject().get("Q");
                    if (questionsJson != null) {
                        questionsJson = questionsJson.getAsJsonArray();
                    }
                    JsonElement successAnswersJson = entry.getValue().getAsJsonObject().get("A");
                    if (successAnswersJson != null) {
                        successAnswersJson = successAnswersJson.getAsJsonObject().get("SUCCESS");
                    }
                    JsonElement failureAnswersJson = entry.getValue().getAsJsonObject().get("A");
                    if (failureAnswersJson != null) {
                        failureAnswersJson = failureAnswersJson.getAsJsonObject().get("FAILURE");
                    }
                    JsonElement followUpQuestionsJson = entry.getValue().getAsJsonObject().get("FUP");
                    if (followUpQuestionsJson != null) {
                        followUpQuestionsJson = followUpQuestionsJson.getAsJsonObject().get("Q");
                    }
                    JsonElement followUpAnswersJson = entry.getValue().getAsJsonObject().get("FUP");
                    if (followUpAnswersJson != null) {
                        followUpAnswersJson = followUpAnswersJson.getAsJsonObject().get("A");
                    }

                    questions.put(entry.getKey(), gson.fromJson(questionsJson, listType));
                    successAnswers.put(entry.getKey(), gson.fromJson(successAnswersJson, listType));
                    failureAnswers.put(entry.getKey(), gson.fromJson(failureAnswersJson, listType));
                    followUpQuestions.put(entry.getKey(), gson.fromJson(followUpQuestionsJson, listType));
                    followUpAnswers.put(entry.getKey(), gson.fromJson(followUpAnswersJson, listType));
                }
                return true;
            }
        } catch (JsonSyntaxException e) {
            LOGGER.error("Wrong syntax in QA json file" + e.getMessage());
        } catch (JsonIOException e) {
            LOGGER.error("IO Error on parsing QA json file from BufferedReader: " + e.getMessage());
        } catch (JsonParseException e) {
            LOGGER.error("Parsing error on processing QA json file: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error while parsing QA values from json: " + e.getMessage());
        }
        return false;
    }

    public JsonEntryModel getEntry(Neo4jRelationship relationship) {
        return getJsonEntryModel(relationship.type);
    }

    public JsonEntryModel getEntry(Neo4jProperty property) {
        return getJsonEntryModel(property.type);
    }

    public Map<String, RandomList<String>> getQuestions() {
        return questions;
    }

    public RandomList<String> getQuestions(Neo4jRelationship relationship) {
        return questions.get(relationship.type);
    }

    public RandomList<String> getQuestions(Neo4jProperty property) {
        return questions.get(property.type);
    }

    public Map<String, RandomList<String>> getAnswers(Neo4jRelationship relationship) {
        Map<String, RandomList<String>> answers = new HashMap<>();
        answers.put("SUCCESS", successAnswers.get(relationship.type));
        answers.put("FAILURE", failureAnswers.get(relationship.type));

        return answers;
    }

    public Map<String, RandomList<String>> getAnswers(Neo4jProperty property) {
        Map<String, RandomList<String>> answers = new HashMap<>();
        answers.put("SUCCESS", successAnswers.get(property.type));
        answers.put("FAILURE", failureAnswers.get(property.type));

        return answers;
    }

    public Map<String, RandomList<String>> getSuccessAnswers() {
        return successAnswers;
    }

    public RandomList<String> getSuccessAnswers(Neo4jRelationship relationship) {
        return successAnswers.get(relationship.type);
    }

    public RandomList<String> getSuccessAnswers(Neo4jProperty property) {
        return successAnswers.get(property.type);
    }

    public Map<String, RandomList<String>> getFailureAnswers() {
        return failureAnswers;
    }

    public RandomList<String> getFailureAnswers(Neo4jRelationship relationship) {
        return failureAnswers.get(relationship.type);
    }

    public RandomList<String> getFailureAnswers(Neo4jProperty property) {
        return failureAnswers.get(property.type);
    }

    public Map<String, RandomList<String>> getFollowUp(Neo4jRelationship relationship) {
        Map<String, RandomList<String>> followUp = new HashMap<>();
        followUp.put("Q", followUpQuestions.get(relationship.type));
        followUp.put("A", followUpAnswers.get(relationship.type));

        return followUp;
    }

    public Map<String, RandomList<String>> getFollowUp(Neo4jProperty property) {
        Map<String, RandomList<String>> followUp = new HashMap<>();
        followUp.put("Q", followUpQuestions.get(property.type));
        followUp.put("A", followUpAnswers.get(property.type));

        return followUp;
    }

    public RandomList<String> getFollowUpQuestions(Neo4jRelationship relationship) {
        return followUpQuestions.get(relationship.type);
    }

    public RandomList<String> getFollowUpQuestions(Neo4jProperty property) {
        return followUpQuestions.get(property.type);
    }

    public RandomList<String> getFollowUpAnswers(Neo4jRelationship relationship) {
        return followUpAnswers.get(relationship.type);
    }

    public RandomList<String> getFollowUpAnswers(Neo4jProperty property) {
        return followUpAnswers.get(property.type);
    }

    private JsonEntryModel getJsonEntryModel(String type) {
        JsonEntryModel entryValue = new JsonEntryModel();

        entryValue.setIntent(type);
        entryValue.setQuestions(questions.get(type));
        Map<String, RandomList<String>> answers = new HashMap<>();
        answers.put("SUCCESS", successAnswers.get(type));
        answers.put("FAILURE", failureAnswers.get(type));
        entryValue.setAnswers(answers);
        Map<String, RandomList<String>> followUp = new HashMap<>();
        followUp.put("Q", followUpQuestions.get(type));
        followUp.put("A", followUpAnswers.get(type));
        entryValue.setFUP(followUp);

        return entryValue;
    }
}