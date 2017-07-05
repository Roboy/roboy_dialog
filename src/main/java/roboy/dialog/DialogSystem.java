package roboy.dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.net.URI;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import roboy.dialog.action.Action;
import roboy.dialog.action.FaceAction;
import roboy.dialog.action.ShutDownAction;
import roboy.dialog.personality.CuriousPersonality;
import roboy.dialog.personality.DefaultPersonality;
import roboy.dialog.personality.KnockKnockPersonality;
import roboy.dialog.personality.Personality;
import roboy.dialog.personality.SmallTalkPersonality;

import roboy.io.*;

import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Analyzer;
import roboy.linguistics.sentenceanalysis.DictionaryBasedSentenceTypeDetector;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.linguistics.sentenceanalysis.OntologyNERAnalyzer;
import roboy.linguistics.sentenceanalysis.OpenNLPPPOSTagger;
import roboy.linguistics.sentenceanalysis.OpenNLPParser;
import roboy.linguistics.sentenceanalysis.SentenceAnalyzer;
import roboy.linguistics.sentenceanalysis.SimpleTokenizer;
import roboy.talk.Verbalizer;

import roboy.memory.RoboyMind;

import roboy.util.Concept;
import roboy.util.Ros;
import roboy.util.RosMainNode;

import org.ros.node.*;
import org.ros.RosRun;
import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;


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
 *    attributes of the Input object in the corresponding fields
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


	    InputDevice input = new CommandLineInput();
		// InputDevice input = new BingInput();
		InputDevice celebInput = new CelebritySimilarityInput();
//		InputDevice roboyDetectInput = new RoboyNameDetectionInput();
		InputDevice multiIn = new MultiInputDevice(input);//, celebInput, roboyDetectInput);
		
		OutputDevice output1 = new CerevoiceOutput();
        CerevoiceOutput output2 = new CerevoiceOutput();
		// OutputDevice output = new BingOutput();

		EmotionOutput emotion = new EmotionOutput();
        OutputDevice output = new CommandLineOutput();
//        OutputDevice output = new CerevoiceOutput(emotion);
		OutputDevice multiOut = new MultiOutputDevice(output, emotion);

        // initialize ROS node
		//RosMainNode.getInstance();

		// connect to ROS bridge
		//Ros.getInstance();

		List<Analyzer> analyzers = new ArrayList<Analyzer>();
		analyzers.add(new SimpleTokenizer());
		analyzers.add(new OpenNLPPPOSTagger());
		analyzers.add(new DictionaryBasedSentenceTypeDetector());
		analyzers.add(new SentenceAnalyzer());
		analyzers.add(new OpenNLPParser());
		analyzers.add(new OntologyNERAnalyzer());


        System.out.println("Initialized...");

        while(true) {

//            while (!Vision.getInstance().findFaces()) {
//                emotion.act(new FaceAction("angry"));
//            }
//            emotion.act(new FaceAction("neutral"));

//            while (!multiIn.listen().attributes.containsKey(Linguistics.ROBOYDETECTED)) {
//            }

            Personality p = new SmallTalkPersonality(new Verbalizer());
            Input raw;
            Interpretation interpretation;
            List<Action> actions = p.answer(new Interpretation(""));


            while (actions.isEmpty() || !(actions.get(0) instanceof ShutDownAction)) {
                multiOut.act(actions);
                raw = multiIn.listen();
                interpretation = new Interpretation(raw.sentence, raw.attributes); //TODO: Input devices should immediately produce Interpretation objects
                for (Analyzer a : analyzers) {
                    interpretation = a.analyze(interpretation);
                }
                actions = p.answer(interpretation);
            }
            List<Action> lastwords = ((ShutDownAction) actions.get(0)).getLastWords();
            multiOut.act(lastwords);
            actions.clear();
        }
	}

}
