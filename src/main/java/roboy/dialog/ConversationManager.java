package roboy.dialog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.dialog.personality.StateBasedPersonality;
import roboy.io.MultiInputDevice;
import roboy.io.MultiOutputDevice;
import roboy.linguistics.sentenceanalysis.*;
import roboy.logic.Inference;
import roboy.logic.InferenceEngine;
import roboy.memory.DummyMemory;
import roboy.memory.Neo4jMemory;
import roboy.memory.Neo4jMemoryInterface;
import roboy.memory.nodes.Interlocutor;
import roboy.ros.RosMainNode;
import roboy.talk.Verbalizer;
import roboy.util.ConfigManager;
import roboy.util.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Central managing node for roboy_dialog.
 * ConversationManager coordinates conversation dispatching,running and stopping, IO flows and everything else that needs a central contact.
 * ConversationManager assumes that it runs on an actual roboy if ROS_ENABLED is true in config.properties.
 */
public class ConversationManager {

    private final static Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException {

        //Initialize the ROS node.
        RosMainNode rosMainNode;
        if(ConfigManager.ROS_ENABLED){
            rosMainNode = new RosMainNode();
        }
        else{
            // TODO: create a nice offline interface for RosMainNode, similar to DummyMemory
            rosMainNode = null;
        }

        // TODO deal with memory
        Neo4jMemoryInterface memory;
        if (ConfigManager.ROS_ENABLED && ConfigManager.ROS_ACTIVE_PKGS.contains("roboy_memory")) {
            memory = new Neo4jMemory(rosMainNode);
        }
        else {
            memory = new DummyMemory();
        }


        //Compose the analyzer chain.
        List<Analyzer> analyzers = new ArrayList<>();


        // Do not disable the following two analyzers!
        // They allow simple states to work without running SemanticParserAnalyzer
        analyzers.add(new Preprocessor());
        analyzers.add(new SimpleTokenizer());

        analyzers.add(new SemanticParserAnalyzer(ConfigManager.PARSER_PORT));
        //analyzers.add(new OpenNLPPPOSTagger());
        analyzers.add(new DictionaryBasedSentenceTypeDetector());
        //analyzers.add(new SentenceAnalyzer());
        analyzers.add(new OpenNLPParser());
        //analyzers.add(new OntologyNERAnalyzer());
        analyzers.add(new AnswerAnalyzer());


        InferenceEngine inference = new Inference();



        //Roboy mode mode: Repeat a conversation a few times.
        //if(ConfigManager.ROS_ENABLED) {//TODO adapt when dispatching functionality is implemented

            Conversation c = createConversation(rosMainNode, analyzers, inference, memory);

            //Repeat conversation c.
            for (int numConversations = 0; numConversations < 50; numConversations++) {
                logger.info("PRESS ENTER TO START THE DIALOG. ROBOY WILL WAIT FOR SOMEONE TO GREET HIM (HI, HELLO)");
                System.in.read();

                c.start();
                try {//Since this is roboy mode and only one conversation happens, we need to wait for it to finish so we don't clog the command line.
                    logger.info("Waiting for conversation to end.");
                    c.join();
                }
                catch(InterruptedException ie){
                    logger.error("ConversationManager has been interrupted: " + ie.getMessage());
                }
                //Reset the conversation before rerun.
                c.resetConversation(new Interlocutor(memory));
            }
            return;//don't execute non-roboy mode
        //}

    }

    private static Conversation createConversation(RosMainNode rosMainNode, List<Analyzer> analyzers, InferenceEngine inference, Neo4jMemoryInterface memory) throws IOException{
        logger.info("Creating new conversation...");

        //Create IODevices.
        MultiInputDevice multiIn = IO.getInputs(rosMainNode);
        MultiOutputDevice multiOut = IO.getOutputs(rosMainNode);

        //Create this conversations context.
        Context context = new Context();
        //Initialize ROS node if present.
        if(rosMainNode == null) {
            if (ConfigManager.ROS_ENABLED) {
                logger.warn("ROS is enabled but this conversation did not recieve a ROS node. Ignore this warning if this was intended.");
            }
        }
        else{
            context.initializeROS(rosMainNode);
        }

        //Create this conversations statemachine.
        StateBasedPersonality personality = new StateBasedPersonality(inference, rosMainNode, memory, context, new Verbalizer());
        File personalityFile = new File(ConfigManager.PERSONALITY_FILE);

        //Set the interlocutor.
        Interlocutor person = new Interlocutor(memory);
        context.ACTIVE_INTERLOCUTOR_UPDATER.updateValue(person);


        return new Conversation(personality, personalityFile, multiIn, multiOut, analyzers);
    }
}
