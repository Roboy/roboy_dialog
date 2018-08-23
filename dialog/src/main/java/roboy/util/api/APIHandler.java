package roboy.util.api;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class APIHandler {

    final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();

    public abstract URL getAPIURL(String APIKey, String ... arguments) throws MalformedURLException;
    public abstract String handleJSON(String JSON, String ... arguments);
    public abstract String getKeyName();
    public abstract boolean validateArguments(String ... arguments);

    public String getData(String ... arguments) throws IOException {
        if(!validateArguments(arguments)) logger.warn("Arguments are Invalid");
        return handleJSON(getJSON(getAPIURL(keyGetter.getKey(getKeyName()), arguments)), arguments);
    }

    private String getJSON(URL url) throws IOException {
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        logger.debug(result.toString());
        return result.toString();
    }

    private static final class keyGetter {
        private static YAMLConfiguration yamlConfiguration = new YAMLConfiguration();

        static String getKey(String key) {
            try {
                yamlConfiguration.read(new FileReader("resources/knowledgebase/apiKeys.yml"));
            } catch (ConfigurationException e) {
                logger.error("Config File contains Errors");
                logger.error(e.getMessage());
            } catch (FileNotFoundException e) {
                logger.error("File Not Found: API KEY");
                logger.error(e.getMessage());
            }
            return yamlConfiguration.getString(key);
        }
    }
}
