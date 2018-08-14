package edu.stanford.nlp.sempre.roboy.api;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class keyGetter {
    static YAMLConfiguration yamlConfiguration = new YAMLConfiguration();
    static String getKey(String key){
        try {
            yamlConfiguration.read(new FileReader("nlu/parser/src/main/java/edu/stanford/nlp/sempre/roboy/api/apiKeys.yml"));
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return yamlConfiguration.getString(key);
    }

}
