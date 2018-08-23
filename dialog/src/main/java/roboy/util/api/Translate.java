package roboy.util.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Translate extends APIHandler {

    private static String handleLanguage(String language) {
        switch (language.toLowerCase()){
            case "azerbaijan": return "az";
            case "malayalam": return "ml";
            case "albanian": return "sq";
            case "maltese": return "mt";
            case "amharic": return "am";
            case "macedonian": return "mk";
            case "english": return "en";
            case "maori": return "mi";
            case "arabic": return "ar";
            case "marathi": return "mr";
            case "armenian": return "hy";
            case "mari": return "mhr";
            case "afrikaans": return "af";
            case "mongolian": return "mn";
            case "basque": return "eu";
            case "german": return "de";
            case "bashkir": return "ba";
            case "nepali": return "ne";
            case "belarusian": return "be";
            case "norwegian": return "no";
            case "bengali": return "bn";
            case "punjabi": return "pa";
            case "burmese": return "my";
            case "papiamento": return "pap";
            case "bulgarian": return "bg";
            case "persian": return "fa";
            case "bosnian": return "bs";
            case "polish": return "pl";
            case "welsh": return "cy";
            case "portuguese": return "pt";
            case "hungarian": return "hu";
            case "romanian": return "ro";
            case "vietnamese": return "vi";
            case "russian": return "ru";
            case "haitian": return "ht";
            case "cebuano": return "ceb";
            case "galician": return "gl";
            case "serbian": return "sr";
            case "dutch": return "nl";
            case "sinhala": return "si";
            case "slovakian": return "sk";
            case "greek": return "el";
            case "slovenian": return "sl";
            case "georgian": return "ka";
            case "swahili": return "sw";
            case "gujarati": return "gu";
            case "sundanese": return "su";
            case "danish": return "da";
            case "tajik": return "tg";
            case "hebrew": return "he";
            case "thai": return "th";
            case "yiddish": return "yi";
            case "tagalog": return "tl";
            case "indonesian": return "id";
            case "tamil": return "ta";
            case "irish": return "ga";
            case "tatar": return "tt";
            case "italian": return "it";
            case "telugu": return "te";
            case "icelandic": return "is";
            case "turkish": return "tr";
            case "spanish": return "es";
            case "udmurt": return "udm";
            case "kazakh": return "kk";
            case "uzbek": return "uz";
            case "kannada": return "kn";
            case "ukrainian": return "uk";
            case "catalan": return "ca";
            case "urdu": return "ur";
            case "kyrgyz": return "ky";
            case "finnish": return "fi";
            case "chinese": return "zh";
            case "french": return "fr";
            case "korean": return "ko";
            case "hindi": return "hi";
            case "xhosa": return "xh";
            case "croatian": return "hr";
            case "khmer": return "km";
            case "czech": return "cs";
            case "laotian": return "lo";
            case "swedish": return "sv";
            case "latin": return "la";
            case "scottish": return "gd";
            case "latvian": return "lv";
            case "estonian": return "et";
            case "lithuanian": return "lt";
            case "esperanto": return "eo";
            case "luxembourgish": return "lb";
            case "javanese": return "jv";
            case "malagasy": return "mg";
            case "japanese": return "ja";
            case "malay": return "ms";
            default: return language;
        }
    }



    @Override
    public URL APIrequestURL(String APIKey, String... arguments) throws MalformedURLException {
        return new URL((String.format("https://translate.yandex.net/api/v1.5/tr.json/translate?key=%s&text=%s&lang=%s", APIKey, arguments[0].replace(" ", "%20"), handleLanguage(arguments[1]))));
    }

    @Override
    public String handleJSON(String JSON, String... arguments) {
        Object jsonObject = ((JsonObject) new JsonParser().parse(JSON.toString())).get("text").getAsJsonArray().get(0).getAsString();
        return jsonObject.toString();
    }

    @Override
    public String getKeyName() {
        return "translatekey";
    }

    @Override
    public boolean validateArguments(String... arguments) {
        return arguments.length==2;
    }
}
