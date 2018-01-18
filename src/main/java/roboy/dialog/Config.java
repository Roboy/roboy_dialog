package roboy.dialog;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Save runtime configurations (profiles) for Roboy.
 * 1) Configuration variables define alternating behaviors.
 * 2) To create a combination of configurations, add a new profile to Configurations and
 *    implement its setProfile method.
 */

public class Config {
    /**
     * List of profile names. The variables are set in the corresponding set<name>Profile() method.
     * String values make it possible to define the profile in start command with:
     * -Dprofile=<profileString>
     */
    public enum ConfigurationProfile {
        DEFAULT("DEFAULT"),
        NOROS("NOROS"),
        STANDALONE("STANDALONE"),
        DEBUG("DEBUG"),
        MEMORY_ONLY("MEMORY-ONLY");

        public String profileName;

        ConfigurationProfile(String profile) {
            this.profileName=profile;
        }
    }

    /* CONFIGURATION VARIABLES - always static, with a default value. */
    /* Profiles can overwrite the default values, but don't have to. */

    /** If true, Roboy avoids using network-based services such as DBpedia as well as ROS. */
    public static boolean STANDALONE = false;
    /** If true, Roboy avoids using ROS-based services. */
    public static boolean NOROS = false;
    /** If true, Roboy will not continue executing if the ROS main node fails to initialize. */
    public static boolean SHUTDOWN_ON_ROS_FAILURE = true;
    /** If true, Roboy will not continue executing if any of the ROS services failed to initialize. */
    public static boolean SHUTDOWN_ON_SERVICE_FAILURE = true;
    /** ROS hostname, will be fetched from the configuration file in the DEFAULT profile. */
    public static String ROS_HOSTNAME = null;
    /** If true, memory will be queried. Ensure that if NOROS=false, then MEMORY=true.
     * When NOROS=true, MEMORY can be either true or false. **/
    public static boolean MEMORY = true;
    /** Semantic parser socket port. */
    public static int PARSER_PORT = -1;

    /** Configuration file to store changing values. */
    private static String yamlConfigFile = "config.properties";
    private YAMLConfiguration yamlConfig;

    /**
     * Constructor switching to the correct profile.
     */
    public Config(ConfigurationProfile profile) {
        initializeYAMLConfig();
        // Initialize semantic parser socket port
        PARSER_PORT = yamlConfig.getInt("PARSER_PORT");
        switch(profile) {
            case DEFAULT:
                setDefaultProfile();
                break;
            case NOROS:
                setNoROSProfile();
                break;
            case STANDALONE:
                setStandaloneProfile();
                break;
            case DEBUG:
                setDebugProfile();
                break;
            case MEMORY_ONLY:
                setMemoryProfile();
                break;
            default:
                setDefaultProfile();
        }
    }

    /**
     *
     * @param profileString String value of the configuration profile name.
     * @return ConfigurationProfile instance which matches the profileString.
     */
    public static ConfigurationProfile getProfileFromEnvironment(String profileString) {
        for(ConfigurationProfile p : ConfigurationProfile.values()) {
            if(p.profileName.equals(profileString)){
                return p;
            }
        }
        return ConfigurationProfile.DEFAULT;
    }

    /* PROFILE DEFINITIONS */

    private void setDefaultProfile() {
        ROS_HOSTNAME = yamlConfig.getString("ROS_HOSTNAME");
    }

    private void setNoROSProfile() {
        NOROS = true;
        SHUTDOWN_ON_ROS_FAILURE = false;
        SHUTDOWN_ON_SERVICE_FAILURE = false;
        MEMORY = false;
    }

    private void setStandaloneProfile() {
        STANDALONE = true;
        // Also set NOROS, such that ROS-based service callers only need to check for NOROS setting.
        NOROS = true;
        SHUTDOWN_ON_ROS_FAILURE = false;
        SHUTDOWN_ON_SERVICE_FAILURE = false;
        MEMORY = false;
    }

    private void setDebugProfile() {
        SHUTDOWN_ON_ROS_FAILURE = false;
        SHUTDOWN_ON_SERVICE_FAILURE = false;
        ROS_HOSTNAME = yamlConfig.getString("ROS_HOSTNAME");
    }

    private void setMemoryProfile() {
        NOROS = true;
        SHUTDOWN_ON_ROS_FAILURE = false;
        SHUTDOWN_ON_SERVICE_FAILURE = false;
        MEMORY = true;
        ROS_HOSTNAME = yamlConfig.getString("ROS_HOSTNAME");
    }

    private void initializeYAMLConfig() {
        this.yamlConfig = new YAMLConfiguration();
        try
        {
            File propertiesFile = new File(yamlConfigFile);
            if(propertiesFile == null) {
                System.out.println("Could not find "+yamlConfigFile+" file in project path! YAML configurations will be unavailable.");
                return;
            }
            FileReader propertiesReader = new FileReader(propertiesFile);
            yamlConfig.read(propertiesReader);
        }
        catch(ConfigurationException | FileNotFoundException e)
        {
            System.out.println("Exception while reading YAML configurations from "+yamlConfigFile);
            e.printStackTrace();
        }
    }
}