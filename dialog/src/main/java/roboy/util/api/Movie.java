package roboy.util.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class Movie{
    static String KEY = keyGetter.getKey("moviekey");
    static int randomInt = new Random().nextInt(20);

    //FIELD TITLE or OVERVIEW
    public static String getData(String field) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(String.format("https://api.themoviedb.org/3/movie/now_playing?api_key=%s&language=en-US&page=1", KEY));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();

        JsonElement jsonElement = ((JsonObject) new JsonParser().parse(result.toString()))
                .getAsJsonArray("results")
                .get(randomInt).getAsJsonObject().get(field);
//        System.out.println(jsonObject);
        return jsonElement.getAsString();
    }

    public static void main(String[] args)throws Exception{
        System.out.println(getData("title")+":\t"+getData("overview"));
    }
}
