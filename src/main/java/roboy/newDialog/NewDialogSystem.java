package roboy.newDialog;


import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.dialog.action.Action;
import roboy.io.*;
import roboy.linguistics.sentenceanalysis.*;
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
import java.io.FileReader;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 * Temporary class to test new state based personality.
 * Will be be extended and might replace the old DialogSystem in the future.
 */
public class NewDialogSystem {

    private final static Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws SocketException, UnknownHostException {

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
            memory = new Neo4jMemory(rosMainNode);
        }
        else {
            memory = new DummyMemory();
        }

        List<Analyzer> analyzers = new ArrayList<>();
        analyzers.add(new Preprocessor());
        analyzers.add(new SimpleTokenizer());
//        analyzers.add(new SemanticParserAnalyzer(ConfigManager.PARSER_PORT));

        analyzers.add(new OpenNLPPPOSTagger());
        analyzers.add(new DictionaryBasedSentenceTypeDetector());
//        analyzers.add(new SentenceAnalyzer());
        analyzers.add(new OpenNLPParser());
        analyzers.add(new OntologyNERAnalyzer());
        analyzers.add(new AnswerAnalyzer());

        StateBasedPersonality personality = new StateBasedPersonality(rosMainNode, memory, new Verbalizer());
        File personalityFile = new File(ConfigManager.PERSONALITY_FILE);



        // Repeat conversation a few times
        for (int numConversations = 0; numConversations < 2; numConversations++) {

            logger.info("############## New Conversation ##############");

            // flush the interlocutor
            Interlocutor person = new Interlocutor(memory);
            Context.getInstance().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(person);

            try {
                // create "fresh" State objects using loadFromFile() at the beginning of every conversation
                // otherwise some states (with possibly bad implementation) will keep the old internal variables
                personality.loadFromFile(personalityFile);

            } catch (FileNotFoundException e) {
                logger.error("Personality file not found: " + e.getMessage());
                return;
            }

            List<Action> actions = personality.startConversation();

            while (!actions.isEmpty()) {
                multiOut.act(actions);

                // now stop if conversation ended
                if (personality.conversationEnded()) {
                    break;
                }

                // listen to interlocutor if conversation didn't end
                Input raw;
                try {
                    raw = multiIn.listen();
                } catch (Exception e) {
                    logger.error("Exception in input: " + e.getMessage());
                    return;
                }

                // analyze and answer
                Interpretation interpretation = new Interpretation(raw.sentence, raw.attributes);
                for (Analyzer a : analyzers) {
                    interpretation = a.analyze(interpretation);
                }
                actions = personality.answer(interpretation);
            }

            logger.info("############# Reset State Machine ############");
            // now reset --> conversationEnded() will now return false --> new conversation possible
            personality.reset();

        }
    }



}
