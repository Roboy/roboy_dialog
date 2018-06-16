package roboy.util;


import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;


public class SynonymFileParser {

    private Gson gson;

    private final Logger LOGGER = LogManager.getLogger();

    private Map<String, List<String>> synonyms;

    public SynonymFileParser(String file) {
        synonyms = parse(file);
    }

    public Map<String, List<String>> parse(String file) {

        try {
            synonyms = new HashMap<>();
            Type listType = new TypeToken<Map<String, List<String>>>() {}.getType();
            File f = new File(file);
            InputStream input = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            gson = new Gson();
            return gson.fromJson(br, listType);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Wrong syntax in Synonym json file" + e.getMessage());
        } catch (JsonIOException e) {
            LOGGER.error("IO Error on parsing Synonym json file from BufferedReader: " + e.getMessage());
        } catch (JsonParseException e) {
            LOGGER.error("Parsing error on processing Synonym json file: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error while parsing Synonym values from json: " + e.getMessage());
        }
        return null;
    }

    public String getRandomKey(){
        Object[] allKeys = synonyms.keySet().toArray();
        Object key = allKeys[new Random().nextInt(allKeys.length)];
        return key.toString();
    }

    public Map<String, List<String>> getSynonyms(){
        return synonyms;
    }

}
