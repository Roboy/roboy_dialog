package edu.stanford.nlp.sempre.roboy.api;

import com.github.jsonldjava.utils.Obj;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Translate  {
    static final String KEY = keyGetter.getKey("translatekey");

    public static String getData(String... args) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(APIify(args[0], args[1]));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();

        Object jsonObject = ((JsonObject) new JsonParser().parse(result.toString())).get("text").getAsJsonArray().get(0).getAsString();
//        System.out.println(jsonObject);
        return result.toString();
    }

    private static String APIify(String text, String targetLang){
        String s = String.format("https://translate.yandex.net/api/v1.5/tr.json/translate?key=%s&text=%s&lang=%s", KEY, text.replace(" ", "%20"), targetLang);
//        System.out.println(s);
        return s;
    }

    public static void main(String[] args) throws Exception{
        System.out.println(getData("cheese is the best vegetable", "de"));
    }
}
