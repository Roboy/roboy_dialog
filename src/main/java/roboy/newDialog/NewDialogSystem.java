package roboy.newDialog;


import roboy.dialog.Config;
import roboy.dialog.action.Action;
import roboy.io.*;
import roboy.linguistics.sentenceanalysis.*;
import roboy.talk.Verbalizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static roboy.dialog.Config.ConfigurationProfile.NOROS;

/**
 * Temporary class to test new state based personality.
 *
 */
public class NewDialogSystem {



    public static void main(String[] args) throws IOException, InterruptedException {

        new Config(NOROS);


        MultiInputDevice multiIn = new MultiInputDevice(new CommandLineInput());
        MultiOutputDevice multiOut = new MultiOutputDevice(new CommandLineOutput());
        List<Analyzer> analyzers = new ArrayList<>();
        analyzers.add(new Preprocessor());
        analyzers.add(new SimpleTokenizer());

        StateBasedPersonality personality = new StateBasedPersonality(new Verbalizer());
        personality.loadFromFile(new File("resources/personalityFiles/ExamplePersonality.json"));


        Input raw;
        Interpretation interpretation;

        // Repeat conversation a few times
        for (int numConversations = 0; numConversations < 3; numConversations++) {

            System.out.println("-------------- new conversation --------------");
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
