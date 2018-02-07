package roboy.newDialog;


import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import roboy.dialog.Config;
import roboy.dialog.action.Action;
import roboy.io.*;
import roboy.linguistics.sentenceanalysis.*;
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

    private static String getPersonalityFilePathFromConfig() throws FileNotFoundException, ConfigurationException {
        YAMLConfiguration yamlConfig = new YAMLConfiguration();
        yamlConfig.read(new FileReader(new File("config.properties")));
        return yamlConfig.getString("PERSONALITY_FILE");
    }

    public static void main(String[] args) throws Exception {

        // TODO: catch all exceptions or make sure none are thrown
        new Config(NOROS);


        MultiInputDevice multiIn = new MultiInputDevice(new CommandLineInput());
        MultiOutputDevice multiOut = new MultiOutputDevice(new CommandLineOutput());
        List<Analyzer> analyzers = new ArrayList<>();
        analyzers.add(new Preprocessor());
        analyzers.add(new SimpleTokenizer());

        StateBasedPersonality personality = new StateBasedPersonality(new Verbalizer());
        String personalityFilePath = getPersonalityFilePathFromConfig();


        Input raw;
        Interpretation interpretation;

        // Repeat conversation a few times
        for (int numConversations = 0; numConversations < 2; numConversations++) {

            System.out.println("-------------- new conversation --------------");
            // important: reset personality completely before every conversation
            // otherwise some states (with possibly bad implementation) will keep the old internal variables
            personality.loadFromFile(new File(personalityFilePath));
            List<Action> actions = personality.startConversation();

            while ( ! actions.isEmpty() ) {
                multiOut.act(actions);
                raw = multiIn.listen();
                interpretation = new Interpretation(raw.sentence, raw.attributes);
                for (Analyzer a : analyzers) {
                    interpretation = a.analyze(interpretation);
                }
                actions = personality.answer(interpretation);
            }

        }
    }



}
