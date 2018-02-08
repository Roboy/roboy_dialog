package roboy.dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonIOException;

import roboy.context.Context;
import roboy.context.GUI.ContextGUI;
import roboy.dialog.action.Action;
import roboy.dialog.action.ShutDownAction;
import roboy.dialog.personality.Personality;
import roboy.dialog.personality.SmallTalkPersonality;

import roboy.io.*;

import roboy.linguistics.sentenceanalysis.*;
import roboy.memory.Neo4jMemory;
import roboy.talk.Verbalizer;

import roboy.ros.RosMainNode;

import static roboy.dialog.Config.ConfigurationProfile.*;


/**
 * The dialog manager's main class.
 * 
 * Here, the used components are put together and executed using the main method. In the future,
 * the different combinations of components should probably be transfered to configuration files.
 * 
 * The workflow in the dialog manager is the following:
 * 1. Input devices produce an Input object
 * 2. The Input object is transformed into an Interpretation object containing
 *    the input sentence in the Linguistics.SENTENCE attribute and all other
 *    lists of the Input object in the corresponding fields
 * 3. Linguistic Analyzers are used on the Interpretation object to add additional information
 * 4. The Personality class takes the Interpretation object and decides how to answer
 *    to this input
 * 5. The list of actions returned by Personality.answer is performed by the Output devices
 * 6. If one of these actions is a ShutDownAction the program terminates
 * 7. Otherwise repeat
 * 
 * Input devices:
 * - For testing from command line: CommandLineInput
 * - For speech to text: BingInput (requires internet)
 * - For combining multiple inputs: MultiInputDevice
 * - Others for specific tasks
 * 
 * Analyzers:
 * - Tokenization: SimpleTokenizer
 * - Part-of-speech-tagging: OpenNLPPOSTagger
 * - Semantic role labeling: OpenNLPParser
 * - DBpedia question answering: AnswerAnalyzer
 * - Other more stupid ones
 * 
 * Personalities:
 * - SmallTalkPersonality: main one
 * - Others for testing specific things
 * 
 * Output devices:
 * - For testing with command line: CommandLineOutput
 * - For text to speech: BingOutput (requires internet)
 * - For combining multiple outputs: MultiOutputDevice
 * - For text to speech + facial expressions: CerevoiceOutput
 * - For facial expressions: EmotionOutput
 * - For text to speech (worse quality): FreeTTSOutput
 * 
 * The easiest way to create ones own Roboy communication application is to pick the 
 * input and output devices provided here, use the tokenization, POS tagging and possibly
 * semantic role labeling (though still under development) if needed and write an own 
 * personality. If one wants to use the DBpedia, Protege, generative model or state machine
 * stuff, one has to dig deeper into the small talk personality and see how it is used there.
 */
public class DialogSystem {

	public static void main(String[] args) throws JsonIOException, IOException, InterruptedException {

        // This sets a configuration profile for the entire run.
        // Profiles can be added in roboy.dialog.Config.ConfigurationProfile
        if(System.getProperty("profile")!=null) {
            new Config(Config.getProfileFromEnvironment(System.getProperty("profile")));
        } else {
            new Config(DEFAULT);
        }

        // initialize ROS node
        RosMainNode rosMainNode = new RosMainNode();
        // initialize Memory with ROS
        Neo4jMemory.getInstance(rosMainNode);
        if(Config.CONTEXT_DEMO) {
            final Runnable gui = () -> ContextGUI.run();
            Thread t = new Thread(gui);
            t.start();
            for(int i = 0; i < 1000; i++) {
                Context.InternalUpdaters.DIALOG_TOPICS_UPDATER.updateValue("test no." + i);
                Thread.sleep(200);
            }
        }
        /*
         * I/O INITIALIZATION
         */
        MultiInputDevice multiIn;
        // By default, all output is also written to the command line.
        MultiOutputDevice multiOut = new MultiOutputDevice(new CommandLineOutput());
        if(Config.NOROS) {
            multiIn = new MultiInputDevice(new CommandLineInput());
        } else {
            multiIn = new MultiInputDevice(new BingInput(rosMainNode));
            multiOut.add(new CerevoiceOutput(rosMainNode));
        }
        // OPTIONAL INPUTS
        // DatagramSocket ds = new DatagramSocket(55555);
        // multiIn.add(new UdpInput(ds));
        // multiIn.add(new CelebritySimilarityInput());
        // multiIn.add(new RoboyNameDetectionInput());
        // OPTIONAL OUTPUTS
        // multiOut.add(new BingOutput());
        // multiOut.add(new UdpOutput(ds, "localhost", 55556));
        // multiOut.add(new EmotionOutput(rosMainNode));

        /*
         * ANALYZER INITIALIZATION
         */
		List<Analyzer> analyzers = new ArrayList<Analyzer>();
		analyzers.add(new Preprocessor());
		analyzers.add(new SimpleTokenizer());
		analyzers.add(new OpenNLPPPOSTagger());
		analyzers.add(new DictionaryBasedSentenceTypeDetector());
		analyzers.add(new SentenceAnalyzer());
		analyzers.add(new OpenNLPParser());
		analyzers.add(new OntologyNERAnalyzer());
		analyzers.add(new AnswerAnalyzer());
        analyzers.add(new EmotionAnalyzer());
        analyzers.add(new SemanticParserAnalyzer(Config.PARSER_PORT));
        //if(!Config.NOROS) {
        //    analyzers.add(new IntentAnalyzer(rosMainNode));
        //}

        if (!rosMainNode.STARTUP_SUCCESS && Config.SHUTDOWN_ON_ROS_FAILURE) {
            throw new RuntimeException("DialogSystem shutdown caused by ROS main node initialization failure.");
        }

        System.out.println("DM initialized...");

        while(true) {

//            while (!Vision.getInstance().findFaces()) {
//                emotion.act(new FaceAction("angry"));
//            }
//            emotion.act(new FaceAction("neutral"));
//            while (!multiIn.listen().lists.containsKey(Linguistics.ROBOYDETECTED)) {
//            }

            Personality smallTalk = new SmallTalkPersonality(new Verbalizer(), rosMainNode);
            Input raw;
            Interpretation interpretation;
            List<Action> actions = smallTalk.answer(new Interpretation(""));


            while (actions.isEmpty() || !(actions.get(0) instanceof ShutDownAction)) {
                multiOut.act(actions);
                raw = multiIn.listen();
                interpretation = new Interpretation(raw.sentence, raw.attributes); //TODO: Input devices should immediately produce Interpretation objects
                for (Analyzer a : analyzers) {
                    interpretation = a.analyze(interpretation);
                }
                actions = smallTalk.answer(interpretation);
            }
            List<Action> lastwords = ((ShutDownAction) actions.get(0)).getLastWords();
            multiOut.act(lastwords);
            actions.clear();
        }
	}
}
