package roboy.util;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private static String yamlConfigFile = "config.properties";

    public static boolean ROS_ENABLED = false;
    public static String ROS_MASTER_IP = "127.0.0.1";
    public static List<String> ROS_ACTIVE_PKGS = new ArrayList<>();
    public static String ACTION_CLIENT_SCRIPT = "/home/roboy/workspace/Roboy/src/roboy_dialog/resources/scripts/action_client.py";

    public static boolean DEBUG = true;
    public static boolean DEMO_MODE = false;
    public static boolean INFINITE_REPETITION = false;

    public static String INPUT = "cmdline";
    public static List<String> OUTPUTS = new ArrayList<>();

    public static String UDP_HOST_ADDRESS = "127.0.0.1";
    public static int UDP_IN_SOCKET = 55555;
    public static int UDP_OUT_SOCKET = 55556;
    public static DatagramSocket DATAGRAM_SOCKET;


    public static int PARSER_PORT = -1;

    public static String PERSONALITY_FILE = "resources/personalityFiles/tutorial/ToyStateMachine.json";

    public static String IBM_TTS_USER = "";
    public static String IBM_TTS_PASS = "";

    public static boolean CONTEXT_GUI_ENABLED = false;

    public static String TELEGRAM_API_TOKENS_FILE = "";

    public static String MEMORY_LOG_MODE = "INFO";
    public static String DIALOG_LOG_MODE = "INFO";
    public static String PARSER_LOG_MODE = "ALL";

    static {
        // this block is called once at and will initialize config
        // alternative: create a singleton for this class
        initializeConfig();
    }

    /**
     * This function reads the YAML config file and initializes all fields.
     * It is called only once at the beginning
     */
    private static void initializeConfig() {
        Logger LOGGER = LogManager.getLogger();
        LOGGER.info("Initializing Config");

        YAMLConfiguration yamlConfig = new YAMLConfiguration();
        try
        {
            File propertiesFile = new File(yamlConfigFile);

            if (!propertiesFile.exists()) { // propertiesFile == null doesn't work!
                LOGGER.error("Could not find "+yamlConfigFile+" file in project path! YAML configurations will be unavailable.");
                return;
            }

            FileReader propertiesReader = new FileReader(propertiesFile);
            yamlConfig.read(propertiesReader);

            ROS_ENABLED = yamlConfig.getBoolean("ROS_ENABLED");
            if (ROS_ENABLED)
            {
                ROS_MASTER_IP = yamlConfig.getString("ROS_MASTER_IP");
                List<String> activePkgs = yamlConfig.getList(String.class, "ROS_ACTIVE_PKGS");
                if (activePkgs != null) {
                    ROS_ACTIVE_PKGS = activePkgs;
                }

            }

            DEBUG = yamlConfig.getBoolean("DEBUG");
            DEMO_MODE = yamlConfig.getBoolean("DEMO_MODE");
            INFINITE_REPETITION = yamlConfig.getBoolean("INFINITE_REPETITION");

            INPUT = yamlConfig.getString( "INPUT");
            List<String> outputs = yamlConfig.getList(String.class, "OUTPUTS");
            if (outputs != null) {
                OUTPUTS = outputs;
            }

            UDP_HOST_ADDRESS = yamlConfig.getString("UDP_HOST_ADDRESS");
            UDP_IN_SOCKET = yamlConfig.getInt("UDP_IN_SOCKET");
            UDP_OUT_SOCKET = yamlConfig.getInt("UDP_OUT_SOCKET");

            try {
                DATAGRAM_SOCKET = new DatagramSocket(UDP_IN_SOCKET);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }

            PARSER_PORT = yamlConfig.getInt("PARSER_PORT");

            PERSONALITY_FILE = yamlConfig.getString("PERSONALITY_FILE");

            IBM_TTS_USER = yamlConfig.getString("IBM_TTS_USER");
            IBM_TTS_PASS = yamlConfig.getString("IBM_TTS_PASS");

            CONTEXT_GUI_ENABLED = yamlConfig.getBoolean("CONTEXT_GUI_ENABLED");

            ACTION_CLIENT_SCRIPT = yamlConfig.getString("ACTION_CLIENT_SCRIPT");

            TELEGRAM_API_TOKENS_FILE = yamlConfig.getString("TELEGRAM_API_TOKENS_FILE");

            MEMORY_LOG_MODE = (yamlConfig.getString("MEMORY_LOG_MODE"));
            PARSER_LOG_MODE = (yamlConfig.getString("PARSER_LOG_MODE"));
            DIALOG_LOG_MODE = (yamlConfig.getString("DIALOG_LOG_MODE"));

        } catch(ConfigurationException | FileNotFoundException e) {
            LOGGER.error("Exception while reading YAML configurations from "+yamlConfigFile);
            LOGGER.error(e.getMessage());
        }
    }




}
