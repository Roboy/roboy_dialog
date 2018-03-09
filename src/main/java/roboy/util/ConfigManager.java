package roboy.util;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacv.ImageTransformer;
import roboy.io.CommandLineInput;
import roboy.io.MultiInputDevice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private static String yamlConfigFile = "config.properties";
    private final Logger LOGGER = LogManager.getLogger();

    public static boolean ROS_ENABLED = false;
    public static String ROS_MASTER_IP = "127.0.0.1";
    public static List<String> ROS_ACTIVE_PKGS = new ArrayList<>();

    public static boolean DEBUG = true;

    public static String INPUT = "cmdline";
    public static List<String> OUTPUTS = new ArrayList<>();

    public static String UDP_HOST_ADDRESS = "127.0.0.1";
    public static int UDP_IN_SOCKET = 55555;
    public static int UDP_OUT_SOCKET = 55556;
    public static DatagramSocket DATAGRAM_SOCKET;


    public static int PARSER_PORT = -1;

    public static boolean DEMO_GUI = false;

    public static String PERSONALITY_FILE = "resources/personalityFiles/ExamplePersonality.json";

    public static String IBM_TTS_USER = "";
    public static String IBM_TTS_PASS = "";

    private static final ConfigManager manager = new ConfigManager();


    private ConfigManager() {
        LOGGER.info("Initializing Config");

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

            INPUT = yamlConfig.getString( "INPUT");
            OUTPUTS = yamlConfig.getList(String.class, "OUTPUTS");

            UDP_HOST_ADDRESS = yamlConfig.getString("UDP_HOST_ADDRESS");
            UDP_IN_SOCKET = yamlConfig.getInt("UDP_IN_SOCKET");
            UDP_OUT_SOCKET = yamlConfig.getInt("UDP_OUT_SOCKET");

            try {
                DATAGRAM_SOCKET = new DatagramSocket(UDP_IN_SOCKET);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage());
            }

            PARSER_PORT = yamlConfig.getInt("PARSER_PORT");

            DEMO_GUI = yamlConfig.getBoolean("DEMO_GUI");

            PERSONALITY_FILE = yamlConfig.getString("PERSONALITY_FILE");

            IBM_TTS_USER = yamlConfig.getString("IBM_TTS_USER");
            IBM_TTS_PASS = yamlConfig.getString("IBM_TTS_PASS");

        }
        catch(ConfigurationException | FileNotFoundException e)
        {
            LOGGER.error("Exception while reading YAML configurations from "+yamlConfigFile);
            LOGGER.error(e.getMessage());
        }
    }

}
