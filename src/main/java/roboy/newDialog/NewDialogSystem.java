package roboy.newDialog;


import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.Config;
import roboy.dialog.action.Action;
import roboy.io.*;
import roboy.linguistics.sentenceanalysis.*;
import roboy.ros.RosMainNode;
import roboy.talk.Verbalizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static roboy.dialog.Config.ConfigurationProfile.NOROS;

/**
 * Temporary class to test new state based personality.
 * Will be be extended and might replace the old DialogSystem in the future.
 */
public class NewDialogSystem {

    private final static Logger logger = LogManager.getLogger();


    private static String getPersonalityFilePathFromConfig() {
        String personalityPath = null;

        try {
            YAMLConfiguration yamlConfig = new YAMLConfiguration();
            File configFile = new File("config.properties");
            FileReader reader = new FileReader(configFile);
            yamlConfig.read(reader);
            personalityPath = yamlConfig.getString("PERSONALITY_FILE");

        } catch (Exception e) {
            logger.error("Invalid or missing configuration file! " + e.getMessage());
        }

        return personalityPath;
    }

    public static void main(String[] args) {

        new Config(NOROS);


        // initialize ROS node
        // TODO: refactor RosMainNode, Thread.sleep() after initialization is not nice at all
        RosMainNode rosMainNode = new RosMainNode();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error("InterruptedException after ROS initialization!");
        }


        MultiInputDevice multiIn = new MultiInputDevice(new CommandLineInput());
        MultiOutputDevice multiOut = new MultiOutputDevice(new CommandLineOutput());
        List<Analyzer> analyzers = new ArrayList<>();
        analyzers.add(new Preprocessor());
        analyzers.add(new SimpleTokenizer());
        // TODO: Emilka's parser

        StateBasedPersonality personality = new StateBasedPersonality(rosMainNode, new Verbalizer());
        String personalityFilePath = getPersonalityFilePathFromConfig();
        File personalityFile = new File(personalityFilePath);

        // Repeat conversation a few times
        for (int numConversations = 0; numConversations < 2; numConversations++) {

            logger.info("-------------- New Conversation --------------");
            // important: reset personality completely before every conversation
            // otherwise some states (with possibly bad implementation) will keep the old internal variables

            try {
                personality.loadFromFile(personalityFile);
            } catch (FileNotFoundException e) {
                logger.error("Personality file not found: " + e.getMessage());
                return;
            }

            List<Action> actions = personality.startConversation();

            while ( ! actions.isEmpty() ) {
                multiOut.act(actions);

                Input raw;
                try {
                    raw = multiIn.listen();
                } catch (Exception e) {
                    logger.error("Exception in input: " + e.getMessage());
                    return;
                }

                Interpretation interpretation = new Interpretation(raw.sentence, raw.attributes);
                for (Analyzer a : analyzers) {
                    interpretation = a.analyze(interpretation);
                }
                actions = personality.answer(interpretation);
            }

        }
    }



}
