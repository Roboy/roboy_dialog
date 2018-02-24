package roboy.util;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private static String yamlConfigFile = "config.properties";
    final Logger LOGGER = LogManager.getLogger();

    public static boolean ROS_ENABLED = false;
    public static String ROS_MASTER_IP = "127.0.0.1";
    public static List<String> ROS_ACTIVE_PKGS = new ArrayList<>();

    public static boolean DEBUG = true;

    public static List<String> INPUTS = new ArrayList<>();
    public static List<String> OUTPUTS = new ArrayList<>();

    public static int PARSER_PORT = -1;

    public static boolean DEMO_GUI = false;

    public static String PERSONALITY_FILE = "resources/personalityFiles/ExamplePersonality.json";

    private static final ConfigManager manager = new ConfigManager();


    private ConfigManager() {
        YAMLConfiguration yamlConfig = new YAMLConfiguration();
        try
        {
            File propertiesFile = new File(yamlConfigFile);
            if(! propertiesFile.exists()) { // propertiesFile == null doesn't work!
                LOGGER.error("Could not find "+yamlConfigFile+" file in project path! YAML configurations will be unavailable.");
                return;
            }
            FileReader propertiesReader = new FileReader(propertiesFile);
            yamlConfig.read(propertiesReader);

            ROS_ENABLED = yamlConfig.getBoolean("ROS_ENABLED");
            if (ROS_ENABLED)
            {
                ROS_MASTER_IP = yamlConfig.getString("ROS_MASTER_IP");
                ROS_ACTIVE_PKGS = yamlConfig.getList(String.class, "ROS_ACTIVE_PKGS");
            }

            DEBUG = yamlConfig.getBoolean("DEBUG");

            INPUTS = yamlConfig.getList(String.class, "INPUTS");
            OUTPUTS = yamlConfig.getList(String.class, "OUTPUTS");

            PARSER_PORT = yamlConfig.getInt("PARSER_PORT");

            DEMO_GUI = yamlConfig.getBoolean("DEMO_GUI");

            PERSONALITY_FILE = yamlConfig.getString("PERSONALITY_FILE");

        }
        catch(ConfigurationException | FileNotFoundException e)
        {
            LOGGER.error("Exception while reading YAML configurations from "+yamlConfigFile);
            LOGGER.error(e.getMessage());
        }
    }

//
//    public static boolean isDebug() {
//        return DEBUG;
//    }
//
//    public boolean isRosEnabled() {
//        return ROS_ENABLED;
//    }
//
//    public String getRosMasterIP() {
//        return ROS_MASTER_IP;
//    }
//
//    public List<String> getRosActivePackages() {
//        return ROS_ACTIVE_PKGS;
//    }
//
//    public List<String> getInputs() {
//        return INPUTS;
//    }
//
//    public List<String> getOutputs() {
//        return OUTPUTS;
//    }
//
//    public int getParserPort() {
//        return PARSER_PORT;
//    }
//
//    public boolean isDemoGUI() {
//        return DEMO_GUI;
//    }
//
//    public String getPersonalityFile() {
//        return PERSONALITY_FILE;
//    }
}
