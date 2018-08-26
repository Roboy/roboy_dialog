package org.roboy.dialog;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.roboy.context.Context;
import org.roboy.dialog.action.SpeechAction;
import org.roboy.dialog.personality.StateBasedPersonality;
import org.roboy.io.MultiInputDevice;
import org.roboy.io.MultiOutputDevice;
import org.roboy.linguistics.sentenceanalysis.*;
import org.roboy.logic.Inference;
import org.roboy.logic.InferenceEngine;
import org.roboy.memory.util.DummyMemory;
import org.roboy.memory.Neo4jMemory;
import org.roboy.memory.interfaces.Neo4jMemoryInterface;
import org.roboy.ros.RosMainNode;
import org.roboy.talk.Verbalizer;
import org.roboy.util.ConfigManager;
import org.roboy.util.IO;

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
