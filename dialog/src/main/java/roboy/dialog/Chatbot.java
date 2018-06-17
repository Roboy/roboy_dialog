package roboy.dialog;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.dialog.action.SpeechAction;
import roboy.dialog.personality.StateBasedPersonality;
import roboy.io.MultiInputDevice;
import roboy.io.MultiOutputDevice;
import roboy.linguistics.sentenceanalysis.*;
import roboy.logic.Inference;
import roboy.logic.InferenceEngine;
import roboy.memory.DummyMemory;
import roboy.memory.Neo4jMemory;
import roboy.memory.Neo4jMemoryInterface;
import roboy.ros.RosMainNode;
import roboy.talk.Verbalizer;
import roboy.util.ConfigManager;
import roboy.util.IO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Temporary class to test new state based personality.
 * Will be be extended and might replace the old DialogSystem in the future.
 */
public class Chatbot {

    private final static Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        // initialize ROS node

        RosMainNode rosMainNode;

        if (ConfigManager.ROS_ENABLED) {
            rosMainNode = new RosMainNode();
        } else {
            // TODO: create a nice offline interface for RosMainNode, similar to DummyMemory
            rosMainNode = null;
        }

        MultiInputDevice multiIn = IO.getInputs(rosMainNode);
        MultiOutputDevice multiOut = IO.getOutputs(rosMainNode);

        // TODO deal with memory
        Neo4jMemoryInterface memory;
        if (ConfigManager.ROS_ENABLED && ConfigManager.ROS_ACTIVE_PKGS.contains("roboy_memory")) {
            memory = new Neo4jMemory();
        } else {
            memory = new DummyMemory();
        }

        Context context = new Context();

        logger.info("Initializing analyzers...");

        List<Analyzer> analyzers = new ArrayList<>();

        // Do not disable the following two analyzers!
        // They allow simple states to work without running SemanticParserAnalyzer
        analyzers.add(new Preprocessor());
        analyzers.add(new SimpleTokenizer());

        analyzers.add(new SemanticParserAnalyzer());
        //analyzers.add(new OpenNLPPPOSTagger());
        analyzers.add(new DictionaryBasedSentenceTypeDetector());
        //analyzers.add(new SentenceAnalyzer());
        analyzers.add(new OpenNLPParser());
        //analyzers.add(new OntologyNERAnalyzer());
        analyzers.add(new AnswerAnalyzer());

        InferenceEngine inference = new Inference();

        logger.info("Creating StateBasedPersonality...");

        StateBasedPersonality personality = new StateBasedPersonality(inference, rosMainNode, memory, context, new Verbalizer());
        File personalityFile = new File(ConfigManager.PERSONALITY_FILE);


        // Repeat conversation a few times
        for (int numConversations = 0; numConversations < 50; numConversations++) {
            while (true) {
                // do all actions defined in startConversation() or answer()
                try {
                    String input = multiIn.listen().getSentence();
                    String out = rosMainNode.GenerateAnswer(input);
                    multiOut.act(Arrays.asList(new SpeechAction(out)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
