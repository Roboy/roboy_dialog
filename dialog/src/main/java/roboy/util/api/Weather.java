package roboy.util.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.MalformedURLException;
import java.net.URL;

public class Weather extends APIHandler {

    @Override
    public URL getAPIURL(String APIKey, String... arguments) throws MalformedURLException {
        return new URL(String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&APPID=%s", arguments[0], APIKey));
    }

    @Override
    public String handleJSON(String JSON, String... arguments) {
        JsonObject jsonObject = (((JsonObject) new JsonParser().parse(JSON)).get("weather").getAsJsonArray()).get(0).getAsJsonObject();
        return verbifyWeatherString(jsonObject.get("main").getAsString());
    }

    @Override
    public String getKeyName() {
        return "weatherkey";
    }

    @Override
    public void validateArguments(String[] apiArg, String[] hJSONArgs) throws IllegalArgumentException {
        if(apiArg.length==1 && hJSONArgs==null);
        else throw new IllegalArgumentException("API Arg Expects one location as Argument, JSON Arguments expects null as argument");
    }


    private static String verbifyWeatherString(String weather){
        switch (weather.toLowerCase()){
            //https://openweathermap.org/weather-conditions
            case "clouds": return "cloudy";
            case "clear": return "clear";
            case "thunderstorm": return "stormy";
            case "rain": return "rainy";
            case "drizzle": return "drizzling";
            case "snow": return "snowing";

            //What do you call these
            case "atmosphere": return "unique";
            default: {
                logger.debug(weather+" is not handled.");
                return weather;
            }
        }
    }
}
