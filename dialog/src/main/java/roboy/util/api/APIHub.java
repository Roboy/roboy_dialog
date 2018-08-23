package roboy.util.api;

import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class APIHub {

    private final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();

    public static String getData(Class<?> api, String ... arguments) throws IOException {
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
        return apiHandler.getData(arguments);
    }

    public static void main(String[] args) throws Exception{
        System.out.println(APIHub.getData(Movie.class, "title"));
        System.out.println(APIHub.getData(Movie.class, "overview"));
        System.out.println(APIHub.getData(Translate.class, "cheese is the best vegetable", "german"));
        System.out.println(APIHub.getData(Weather.class, "munich"));

    }
}
