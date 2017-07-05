package roboy.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    /**
     * Fetches the map of (keyword) -> (lists of corresponding questions) from the
     * specified filename.
     *
     * This method was made specifically for the format of questions.json file.
     * It can be adapted for other formats by changing the Type t definition.
     */
    public static Map<String, List<String>> getQuestionFromJsonFile(String file) {
        Type t = new TypeToken<Map<String, List<String>>>(){}.getType();

        InputStream input = JsonUtils.class.getClassLoader().getResourceAsStream(file);
        BufferedReader br = new BufferedReader( new InputStreamReader(input));
        Gson gson = new Gson();

        Map<String, List<String>> q = gson.fromJson(br, t);
        return q;
    }
}
