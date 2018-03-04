package roboy.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import roboy.memory.Neo4jRelationships;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public class JsonUtils {
    /**
     * Fetches the complite JSON string, splits and converts
     * the most straightforward way into backward-compatible Map<> entries
     * initializing a backward-compatible JsonQAValues class
     */
    public static JsonQAValues getQuestionsAndAnswersFromJson(String file) {
        // TODO Design a good and smart way to do this for new Dialog SM. Needs discussion
        InputStream input = JsonUtils.class.getClassLoader().getResourceAsStream(file);
        BufferedReader br = new BufferedReader( new InputStreamReader(input));

        Gson gson = new Gson();
        JsonModel jsonObject = gson.fromJson(br, JsonModel.class);
        Map<String, List<String>> questions = new HashMap<>();
        Map<String, List<String>> successAnswers = new HashMap<>();
        Map<String, List<String>> failureAnswers = new HashMap<>();
        Map<String, List<String>> followUp = new HashMap<>();
        Map<String, List<String>> answersFollowUp = new HashMap<>();
        // Questions
        questions.put("name", jsonObject.name.Q);
        questions.put("FROM", jsonObject.FROM.Q);
        questions.put("HAS_HOBBY", jsonObject.HAS_HOBBY.Q);
        questions.put("LIVE_IN", jsonObject.LIVE_IN.Q);
        questions.put("FRIEND_OF", jsonObject.FRIEND_OF.Q);
        questions.put("STUDY_AT", jsonObject.STUDY_AT.Q);
        questions.put("MEMBER_OF", jsonObject.MEMBER_OF.Q);
        questions.put("WORK_FOR", jsonObject.WORK_FOR.Q);
        questions.put("OCCUPIED_AS", jsonObject.OCCUPIED_AS.Q);
        // Success Answer
        successAnswers.put("name", jsonObject.name.A.get("SUCCESS"));
        successAnswers.put("FROM", jsonObject.FROM.A.get("SUCCESS"));
        successAnswers.put("HAS_HOBBY", jsonObject.HAS_HOBBY.A.get("SUCCESS"));
        successAnswers.put("LIVE_IN", jsonObject.LIVE_IN.A.get("SUCCESS"));
        successAnswers.put("FRIEND_OF", jsonObject.FRIEND_OF.A.get("SUCCESS"));
        successAnswers.put("STUDY_AT", jsonObject.STUDY_AT.A.get("SUCCESS"));
        successAnswers.put("MEMBER_OF", jsonObject.MEMBER_OF.A.get("SUCCESS"));
        successAnswers.put("WORK_FOR", jsonObject.WORK_FOR.A.get("SUCCESS"));
        successAnswers.put("OCCUPIED_AS", jsonObject.OCCUPIED_AS.A.get("SUCCESS"));
        // Failure Answer
        failureAnswers.put("name", jsonObject.name.A.get("FAILURE"));
        failureAnswers.put("FROM", jsonObject.FROM.A.get("FAILURE"));
        failureAnswers.put("HAS_HOBBY", jsonObject.HAS_HOBBY.A.get("FAILURE"));
        failureAnswers.put("LIVE_IN", jsonObject.LIVE_IN.A.get("FAILURE"));
        failureAnswers.put("FRIEND_OF", jsonObject.FRIEND_OF.A.get("FAILURE"));
        failureAnswers.put("STUDY_AT", jsonObject.STUDY_AT.A.get("FAILURE"));
        failureAnswers.put("MEMBER_OF", jsonObject.MEMBER_OF.A.get("FAILURE"));
        failureAnswers.put("WORK_FOR", jsonObject.WORK_FOR.A.get("FAILURE"));
        failureAnswers.put("OCCUPIED_AS", jsonObject.OCCUPIED_AS.A.get("FAILURE"));
        // Follow Ups
        followUp.put("name", jsonObject.name.FUP.get("Q"));
        followUp.put("FROM", jsonObject.FROM.FUP.get("Q"));
        followUp.put("HAS_HOBBY", jsonObject.HAS_HOBBY.FUP.get("Q"));
        followUp.put("LIVE_IN", jsonObject.LIVE_IN.FUP.get("Q"));
        followUp.put("FRIEND_OF", jsonObject.FRIEND_OF.FUP.get("Q"));
        followUp.put("STUDY_AT", jsonObject.STUDY_AT.FUP.get("Q"));
        followUp.put("MEMBER_OF", jsonObject.MEMBER_OF.FUP.get("Q"));
        followUp.put("WORK_FOR", jsonObject.WORK_FOR.FUP.get("Q"));
        followUp.put("OCCUPIED_AS", jsonObject.OCCUPIED_AS.FUP.get("Q"));
        // Follow Up Answers
        answersFollowUp.put("name", jsonObject.name.FUP.get("A"));
        answersFollowUp.put("FROM", jsonObject.FROM.FUP.get("A"));
        answersFollowUp.put("HAS_HOBBY", jsonObject.HAS_HOBBY.FUP.get("A"));
        answersFollowUp.put("LIVE_IN", jsonObject.LIVE_IN.FUP.get("A"));
        answersFollowUp.put("FRIEND_OF", jsonObject.FRIEND_OF.FUP.get("A"));
        answersFollowUp.put("STUDY_AT", jsonObject.STUDY_AT.FUP.get("A"));
        answersFollowUp.put("MEMBER_OF", jsonObject.MEMBER_OF.FUP.get("A"));
        answersFollowUp.put("WORK_FOR", jsonObject.WORK_FOR.FUP.get("A"));
        answersFollowUp.put("OCCUPIED_AS", jsonObject.OCCUPIED_AS.FUP.get("A"));
        // Data
        JsonQAValues q = new JsonQAValues(questions, successAnswers, failureAnswers, followUp, answersFollowUp);
        return q;
    }

    /**
     * Fetches the map of (keyword) -> (lists of corresponding questions) from the
     * specified filename.
     */
    public static Map<String, List<String>> getSentencesFromJsonFile(String file) {
        Type t = new TypeToken<Map<String, List<String>>>(){}.getType();

        InputStream input = JsonUtils.class.getClassLoader().getResourceAsStream(file);
        BufferedReader br = new BufferedReader( new InputStreamReader(input));
        Gson gson = new Gson();

        Map<String, List<String>> q = gson.fromJson(br, t);
        return q;
    }

    /**
     * The success responses consist of an array of two strings, which enables reflecting
     * parsed answers back at the conversation partner.
     */
    public static Map<String,List<String[]>> getSentenceArraysFromJsonFile(String file) {
        Type t = new TypeToken<Map<String, List<String[]>>>(){}.getType();

        InputStream input = JsonUtils.class.getClassLoader().getResourceAsStream(file);
        BufferedReader br = new BufferedReader( new InputStreamReader(input));
        Gson gson = new Gson();

        Map<String, List<String[]>> q = gson.fromJson(br, t);
        return q;
    }
}

