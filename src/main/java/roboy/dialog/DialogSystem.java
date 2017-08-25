package roboy.dialog;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonIOException;

import roboy.dialog.action.Action;
import roboy.dialog.action.ShutDownAction;
import roboy.dialog.personality.Personality;
import roboy.dialog.personality.SmallTalkPersonality;

import roboy.io.*;

import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.*;
import roboy.memory.Neo4jMemory;
import roboy.memory.nodes.RetrieveQueryTemplate;
import roboy.memory.nodes.GetQueryTemplate;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.talk.Verbalizer;

import roboy.ros.RosMainNode;


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

    public static boolean SHUTDOWN_ON_ROS_FAILURE = false;
	
	public static void main(String[] args) throws JsonIOException, IOException, InterruptedException {

        // initialize ROS node
        RosMainNode rosMainNode = new RosMainNode();

	    InputDevice input = new CommandLineInput();
//		 InputDevice input = new BingInput(rosMainNode);
        DatagramSocket ds = new DatagramSocket(55555);
//        InputDevice input = new UdpInput(ds);
		InputDevice celebInput = new CelebritySimilarityInput();
//		InputDevice roboyDetectInput = new RoboyNameDetectionInput();
		InputDevice multiIn = new MultiInputDevice(input);//, celebInput, roboyDetectInput);

		OutputDevice output1 = new CerevoiceOutput(rosMainNode);
//        CerevoiceOutput output2 = new CerevoiceOutput(rosMainNode);
		// OutputDevice output = new BingOutput();
        OutputDevice output2 = new UdpOutput(ds, "localhost", 55556);
		EmotionOutput emotion = new EmotionOutput(rosMainNode);
        OutputDevice output = new CommandLineOutput();
//        OutputDevice output = new CerevoiceOutput(emotion);
		OutputDevice multiOut = new MultiOutputDevice(output);//, output2, emotion);

		List<Analyzer> analyzers = new ArrayList<Analyzer>();
//		analyzers.add(new Preprocessor());
//		analyzers.add(new SimpleTokenizer());
//		analyzers.add(new OpenNLPPPOSTagger());
//		analyzers.add(new DictionaryBasedSentenceTypeDetector());
//		analyzers.add(new SentenceAnalyzer());
//		analyzers.add(new OpenNLPParser());
//		analyzers.add(new OntologyNERAnalyzer());
//		analyzers.add(new AnswerAnalyzer());
//        analyzers.add(new EmotionAnalyzer());
//        analyzers.add(new IntentAnalyzer(rosMainNode));



        // Race between main and rosMainNode threads, but there should be enough time.
        if (!rosMainNode.STARTUP_SUCCESS && SHUTDOWN_ON_ROS_FAILURE) {
            throw new RuntimeException("DialogSystem shutdown caused by ROS service initialization failure. " +
                    "Start the required services or set SHUTDOWN_ON_ROS_FAILURE to false.");
        }


        Thread.sleep(10000L);
        Neo4jMemory mem = new Neo4jMemory(rosMainNode);

        System.out.println("What is your name?");
        System.out.println("-> laura");
        //Check if person exists
        MemoryNodeModel nodeForExistenceCheck = new MemoryNodeModel(true);
        nodeForExistenceCheck.setProperty("name", "laura");
        ArrayList<Integer> result = (ArrayList<Integer>) mem.getByQuery(nodeForExistenceCheck);
        if(result == null || result.isEmpty()) {
            // Create person node in memory with name laura.
            MemoryNodeModel createPersonNode = new MemoryNodeModel(true);
            createPersonNode.setProperty("name", "laura");
            createPersonNode.setLabel("Person");
            int id = mem.create(createPersonNode);
            System.out.println("The id is: " + id);
            // Ask for hobby and create node.
            System.out.println("What is your hobby?");
            System.out.println("-> football");
            MemoryNodeModel createHobbyNode = new MemoryNodeModel(true);
            createHobbyNode.setLabel("Hobby");
            createHobbyNode.setProperty("name", "football");
            int hobbyId = mem.create(createHobbyNode);
            // Set relation HAS_HOBBY from person to hobby
            MemoryNodeModel getPersonNode = mem.getById(id);
            getPersonNode.setRelation("HAS_HOBBY", hobbyId);
            if (mem.save(getPersonNode)) System.out.println("Now I will remember your hobby!");
            else System.out.println("My memory is failing me.");
        } else {
            MemoryNodeModel getPersonNode = mem.getById(result.get(0));
            System.out.println("I remember you!");
        }

        System.out.println("Initialized...");

        while(true) {

//            while (!Vision.getInstance().findFaces()) {
//                emotion.act(new FaceAction("angry"));
//            }
//            emotion.act(new FaceAction("neutral"));

//            while (!multiIn.listen().attributes.containsKey(Linguistics.ROBOYDETECTED)) {
//            }

            Personality p = new SmallTalkPersonality(new Verbalizer(), rosMainNode);
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
                if(interpretation.getFeature(Linguistics.INTENT) != null) {
                    System.out.println("Found intent: "+ (String) interpretation.getFeature(Linguistics.INTENT) + " with confidence: "+ (float) interpretation.getFeature(Linguistics.INTENT_DISTANCE));
                } else {
                    System.out.println("No intent found!");
                }
                actions = p.answer(interpretation);
            }
            List<Action> lastwords = ((ShutDownAction) actions.get(0)).getLastWords();
            multiOut.act(lastwords);
            actions.clear();
        }
	}

}
