package roboy.util.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class Movie extends APIHandler{
    private static int randomInt = 0;


    @Override
    public URL getAPIURL(String key, String... arguments) throws MalformedURLException {
        return new URL(String.format("https://api.themoviedb.org/3/movie/now_playing?api_key=%s&language=en-US&page=1", key));
    }

    @Override
    public String handleJSON(String JSON, String ... arguments) {
        JsonElement jsonElement = ((JsonObject) new JsonParser().parse(JSON.toString()))
                .getAsJsonArray("results")
                .get(randomInt).getAsJsonObject().get(arguments[0]);
        return jsonElement.getAsString();
    }

    @Override
    public String getKeyName() {
        return "moviekey";
    }

    @Override
    public void validateArguments(String[] apiArg, String[] hJSONArgs) throws IllegalArgumentException {
        if(apiArg==null && hJSONArgs.length==1);
        else throw new IllegalArgumentException("API Arg Expects Null as Argument, JSON Arguments expects one Parameter");
    }


    /**
     * If you want a new film, call this method
     */
    public static void newRandomFilm(){
        randomInt = new Random().nextInt(20);
    }


}
