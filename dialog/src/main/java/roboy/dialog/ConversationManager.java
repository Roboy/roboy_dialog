package roboy.dialog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
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
import roboy.memory.Neo4jProperty;
import roboy.memory.nodes.Interlocutor;
import roboy.ros.RosMainNode;
import roboy.talk.Verbalizer;
import roboy.util.ConfigManager;
import roboy.util.IO;
import roboy.util.TelegramCommunicationHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


/**
 * Central managing node for roboy_dialog.
 * ConversationManager coordinates conversation dispatching,running and stopping, IO flows and everything else that needs a central contact.
 * ConversationManager assumes that it runs on an actual roboy if ROS_ENABLED is true in config.properties.
 */
public class ConversationManager {

    private final static Logger logger = LogManager.getLogger();
    private final static HashMap<String, Conversation> conversations = new HashMap<>();
    private static RosMainNode rosMainNode;
    private static List<Analyzer> analyzers;
    private static Neo4jMemoryInterface memory;

    public static void main(String[] args) throws IOException {

        //Initialize the ROS node.
        if(ConfigManager.ROS_ENABLED){
            rosMainNode = new RosMainNode();
        } else{
            // TODO: create a nice offline interface for RosMainNode, similar to DummyMemory
            rosMainNode = null;
        }

        memory = new Neo4jMemory();


        //Compose the analyzer chain.

        logger.info("Initializing analyzers...");

        analyzers = new ArrayList<>();


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


        analyzers.add(new EmotionAnalyzer());
        analyzers.add(new ProfanityAnalyzer());


        //Roboy mode: Repeat a conversation a few times.
        if(ConfigManager.ROS_ENABLED) {

            Conversation c = createConversation(rosMainNode, analyzers, new Inference(), memory, "local");

            //Repeat conversation c.
            for (int numConversations = 0; numConversations < 50; numConversations++) {
                logger.info("PRESS ENTER TO START THE DIALOG. ROBOY WILL WAIT FOR SOMEONE TO GREET HIM (HI, HELLO)");
                System.in.read();

                c.start();
                try {//Since this is roboy mode and only one conversation happens, we need to wait for it to finish so we don't clog the command line.
                    logger.info("Waiting for conversation to end.");
                    c.join();
                }catch (InterruptedException ie) {
                    logger.error("ConversationManager has been interrupted: " + ie.getMessage());
                }
                //Reset the conversation before rerun.
                c.resetConversation(new Interlocutor(memory));
            }
        } else {//non-roboy mode
            if (ConfigManager.INPUT.contains("telegram")) {

                // initialize telegram bot
                ApiContextInitializer.init();
                TelegramBotsApi telegramBotApi = new TelegramBotsApi();

                try {
                    telegramBotApi.registerBot(TelegramCommunicationHandler.getInstance());
                } catch (TelegramApiException e) {
                    logger.error("Telegram bots api error: ", e);
                }
            } else if (ConfigManager.INPUT.contains("cmdline")) {
                Conversation c = createConversation(rosMainNode, analyzers, new Inference(), memory, "local");
                c.start();
                try {//Since this is roboy mode and only one conversation happens, we need to wait for it to finish so we don't clog the command line.
                    logger.info("Waiting for conversation to end.");
                    c.join();
                } catch (InterruptedException ie) {
                    logger.error("ConversationManager has been interrupted: " + ie.getMessage());
                }
            }
            logger.info("####################################################\n#                SYSTEM LOADED                     #\n####################################################\n");

            //wait for user commands
            Scanner scanner = new Scanner(System.in);

            while (true) {
                String command = scanner.next();
                switch (command) {
                    case "shutdown"://gracefully say bye
                        for (Conversation c : conversations.values()) c.endConversation();
                        System.exit(0);
                    default:
                        System.out.println("Command not found. Currently supported commands: shutdown");
                }
            }
        }
    }

    /**
     * Creates and spawns a conversation for a chatuser.
     * @param uuid should consist of "servicename-[uuid]", if input allows only a single user, set to "local"
     * @throws IOException If conversation could not created.
     */
    public static void spawnConversation(String uuid) throws IOException{
        Conversation conversation = createConversation(rosMainNode, analyzers, new Inference(), memory, uuid);
        conversations.put(uuid, conversation);
        conversation.start();
    }

    /**
     * Deregisters a conversation from the conversationmanager. Should only be called from a conversation when it ends.
     * @param conversation The conversation object to be deregistered
     */
    protected static void deregisterConversation(Conversation conversation){
        conversations.values().remove(conversation);
    }


    /**
     * Pauses conversation so it may be resumed via startConversation.
     * @param uuid should consist of "servicename-[uuid]", if input allows only a single user, set to "local"
     */
    public static void pauseConversation(String uuid){
        Conversation c = conversations.get(uuid);
        if (c != null) {
            c.pauseExecution();
        } else {
            logger.error("Conversation to be paused does not exist...");
        }
    }

    /**
     * Stops conversation thread for uuid.
     * @param uuid should consist of "servicename-[uuid]", if input allows only a single user, set to "local"
     */
    public static void stopConversation(String uuid){
        Conversation c = conversations.get(uuid);
        if (c != null) {
            c.endConversation();
        } else {
            logger.error("Conversation to be stopped does not exist...");
        }
    }

    /**
     * Starts a conversation that is paused or stopped.
     * NOT NECESSARY AFTER SPAWNCONVERSATION
     * @param uuid should consist of "servicename-[uuid]", if input allows only a single user, set to "local"
     */
    public static void startConversation(String uuid){
        Conversation c = conversations.get(uuid);
        if (c != null) {
            c.start();
        } else {
            logger.error("Conversation to be started does not exist...");
        }
    }


    /**
     * returns the threadID of the conversation with interlocutor uuid
     * @param uuid should consist of "servicename-[uuid]", if input allows only a single user, set to "local"
     * @return null if thread does not exist, threadID otherwise
     */
    public static Long getConversationThreadID(String uuid){
        Thread conv = conversations.get(uuid);

        return (conv == null) ? null : (Long)conv.getId();
    }


    /**
     * Creates and initializes a new conversation thread. Does not start the thread.
     * @param rosMainNode ROS node. Set null if ROS_ENABLED=false
     * @param analyzers   All analyzers necessary for analyzing the inputs from multiIn. Please provide these in correct order.
     * @param inference Inference engine. The better, the smarter roboy gets.
     * @param memory Roboy memory access. Without, we cannot remember anything and conversations stay shallow.
     * @return roboy.dialog.Conversation object. Fully intialized, ready to launch.
     * @throws IOException In case the IOdevices could not be correctly initialized.
     */

    private static Conversation createConversation(RosMainNode rosMainNode, List<Analyzer> analyzers, InferenceEngine inference, Neo4jMemoryInterface memory, String uuid) throws IOException{
        logger.info("Creating new conversation...");

        //Create IODevices.
        MultiInputDevice multiIn = IO.getInputs(rosMainNode, uuid);
        MultiOutputDevice multiOut = IO.getOutputs(rosMainNode, uuid);

        //Create this conversations context.
        Context context = new Context();
        //Initialize ROS node if present.
        if(rosMainNode == null) {
            if (ConfigManager.ROS_ENABLED) {
                logger.warn("ROS is enabled but this conversation did not recieve a ROS node. Ignore this warning if this was intended.");
            }
        } else {
            context.initializeROS(rosMainNode);
        }

        //Create this conversations statemachine.
        StateBasedPersonality personality = new StateBasedPersonality(inference, rosMainNode, memory, context, new Verbalizer());
        File personalityFile = new File(ConfigManager.PERSONALITY_FILE);

        //Set the interlocutor.
        if(memory == null){
            logger.error("Memory is null while starting a conversation");
        }
        Interlocutor person = new Interlocutor(memory);
        person.setProperty(Neo4jProperty.telegram_id, uuid);
        context.ACTIVE_INTERLOCUTOR_UPDATER.updateValue(person);


        return new Conversation(personality, personalityFile, multiIn, multiOut, analyzers);
    }
}
