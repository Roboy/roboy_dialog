package edu.stanford.nlp.sempre.roboy.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Weather  {
    final static String KEY = keyGetter.getKey("weatherkey");
    public static String getData(String... args) throws Exception{
        try {
            return getHTML(args[0]);
        }catch (Exception e) {return null;}
    }
    public static String getHTML(String country) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&APPID=%s", country, KEY));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();

        JsonObject jsonObject = (((JsonObject) new JsonParser().parse(result.toString())).get("weather").getAsJsonArray()).get(0).getAsJsonObject();
//        System.out.println(jsonObject.get("main"));
        return jsonObject.get("main").getAsString();
    }

    public static void main(String[] args) throws Exception
    {
        //Find Munich
        System.out.println(getData("munich"));
    }


}
