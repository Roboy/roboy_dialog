package roboy.util;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.memory.Neo4jRelationships;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

class JsonModel {
    // Add the new entry
    JsonEntryModel name;
    JsonEntryModel FROM;
    JsonEntryModel HAS_HOBBY;
    JsonEntryModel LIVE_IN;
    JsonEntryModel FRIEND_OF;
    JsonEntryModel STUDY_AT;
    JsonEntryModel MEMBER_OF;
    JsonEntryModel WORK_FOR;
    JsonEntryModel OCCUPIED_AS;
}

/**
 * Getting values for personal and follow-up questions
 * from a JSON file
 */
public class PFUAValues {
    private Gson gson;
    private JsonModel jsonObject;

    final Logger LOGGER = LogManager.getLogger();

    public PFUAValues(String file) {
        try {
            InputStream input = JsonUtils.class.getClassLoader().getResourceAsStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            gson = new Gson();
            jsonObject = gson.fromJson(br, JsonModel.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Wrong syntax in QA json file" + e.getMessage());
        } catch (JsonIOException e) {
            LOGGER.error("IO Error on parsing QA json file from BufferedReader: " + e.getMessage());
        } catch (JsonParseException e) {
            LOGGER.error("Parsing error on processing QA json file: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error while parsing QA values from json: " + e.getMessage());
        }
    }

    public JsonModel getQA() {
        return jsonObject;
    }

    public JsonEntryModel getEntry(Neo4jRelationships relationship) {
        JsonEntryModel entryValue = null;
        try {
            Class<JsonEntryModel> types = JsonEntryModel.class;
            Field field = jsonObject.getClass().getDeclaredField(relationship.type);
            field.setAccessible(true);
            Object value = field.get(types);
            entryValue = (JsonEntryModel) value;
        } catch (NoSuchFieldException e) {
            LOGGER.error("No such entry in QA model: " + e.getMessage());
        } catch (IllegalAccessException e) {
            LOGGER.error("Illegal access to the QA entries: " + e.getMessage());
        }
        return entryValue;
    }
}
