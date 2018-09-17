package roboy.util.api;

import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class APIHub {

    private final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();

    public static String getData(Class<?> api, String[] apiRequestArgs, String[] handleJSONArgs) throws IOException, IllegalArgumentException {
        APIHandler apiHandler;
        try {
            apiHandler = (APIHandler) api.newInstance();
        } catch (InstantiationException e) {
            logger.error(e.getMessage());
            return null;
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
            return null;
        }
        return apiHandler.getData(apiRequestArgs, handleJSONArgs);
    }

    public static void main(String[] args) throws Exception{
        System.out.println(APIHub.getData(Movie.class, null, new String[]{"title"}));
        System.out.println(APIHub.getData(Movie.class, null, new String[]{"overview"}));
        System.out.println(APIHub.getData(Translate.class, new String[]{"cheese is the best vegetable", "german"}, null));
        System.out.println(APIHub.getData(Weather.class, new String[]{"munich"}, new String[]{"argument"}));

    }
}
