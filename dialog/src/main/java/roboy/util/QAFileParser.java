package roboy.util;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

/**
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
 *  }
 * }
 *
 * See more examples in resources/sentences
 */

public class QAFileParser {

    private final Logger logger = LogManager.getLogger();

    private static Map<String, List<String>> questions;
    private static  Map<String, List<String>> successAnswers;
    private static Map<String, List<String>> failureAnswers;

    public QAFileParser(String file) {

        questions = new HashMap<>();
        successAnswers = new HashMap<>();
        failureAnswers = new HashMap<>();

        JsonParser jsonParser = new JsonParser();
        URL url = Resources.getResource(file);

        try {
            String jsonString = Resources.toString(url, Charsets.UTF_8);
            JsonElement jsonTree = jsonParser.parse(jsonString);
            Set<Map.Entry<String, JsonElement>> entrySet = ((JsonObject) jsonTree).entrySet();
            Type listType = new TypeToken<List<String>>() {}.getType();
            for(Map.Entry<String,JsonElement> entry : entrySet) {

                JsonElement questionsJson = entry.getValue().getAsJsonObject().get("Q").getAsJsonArray();
                JsonElement successAnswersJson = entry.getValue().getAsJsonObject().get("A").getAsJsonObject().get("SUCCESS");
                JsonElement failureAnswersJson = entry.getValue().getAsJsonObject().get("A").getAsJsonObject().get("FAILURE");

                questions.put(entry.getKey(), new Gson().fromJson(questionsJson, listType));
                successAnswers.put(entry.getKey(), new Gson().fromJson(successAnswersJson, listType));
                failureAnswers.put(entry.getKey(), new Gson().fromJson(failureAnswersJson, listType));
            }
        }catch (Exception e)
        {
            logger.error("JSON was not parsed correctly");
            logger.error(e.getMessage());
        }

    }

    public Map<String, List<String>> getQuestions() {
        return questions;
    }

    public Map<String, List<String>> getSuccessAnswers() {
        return successAnswers;
    }

    public Map<String, List<String>> getFailureAnswers() {
        return failureAnswers;
    }
}
