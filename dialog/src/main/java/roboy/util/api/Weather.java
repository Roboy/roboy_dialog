package roboy.util.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Weather extends APIHandler {
//    public static String getHTML(String country) throws Exception {
//        StringBuilder result = new StringBuilder();
//        URL url = new URL(String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&APPID=%s", country, KEY));
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("GET");
//        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        String line;
//        while ((line = rd.readLine()) != null) {
//            result.append(line);
//        }
//        rd.close();
//
//        JsonObject jsonObject = (((JsonObject) new JsonParser().parse(result.toString())).get("weather").getAsJsonArray()).get(0).getAsJsonObject();
////        System.out.println(jsonObject.get("main"));
//        return jsonObject.get("main").getAsString();
//    }
//
//    public static void main(String[] args) throws Exception
//    {
//        //Find Munich
//        System.out.println(getData("munich"));
//    }


    @Override
    public URL APIrequestURL(String APIKey, String... arguments) throws MalformedURLException {
        return new URL(String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&APPID=%s", arguments[0], APIKey));
    }

    @Override
    public String handleJSON(String JSON, String... arguments) {
        JsonObject jsonObject = (((JsonObject) new JsonParser().parse(JSON.toString())).get("weather").getAsJsonArray()).get(0).getAsJsonObject();
        return jsonObject.get("main").getAsString();
    }

    @Override
    public String getKeyName() {
        return "weatherkey";
    }

    @Override
    public boolean validateArguments(String... arguments) {
        return arguments.length==1;
    }
}
