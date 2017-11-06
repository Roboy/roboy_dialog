package roboy.dialog;

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
        OFFLINE("OFFLINE");

        public String profileName;

        ConfigurationProfile(String profile) {
            this.profileName=profile;
        }
    }

    /* CONFIGURATION VARIABLES - always static, with a default value. */

    /** If true, Roboy avoids using network-based services such as memory. */
    public static boolean OFFLINE = false;
    /** If true, Roboy will not continue executing if any of the ROS services failed to initialize. */
    public static boolean SHUTDOWN_ON_ROS_FAILURE = false;

    /**
     * Constructor switching to the correct profile.
     */
    public Config(ConfigurationProfile profile) {
        switch(profile) {
            case DEFAULT:
                setDefaultProfile();
                break;
            case OFFLINE:
                setOfflineProfile();
                break;
            default:
                setDefaultProfile();
        }
    }

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
        OFFLINE = false;
    }

    private void setOfflineProfile() {
        OFFLINE = true;
    }
}