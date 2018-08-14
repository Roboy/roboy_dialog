package roboy.dialog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;
import roboy.dialog.personality.StateBasedPersonality;
import roboy.io.Input;
import roboy.io.MultiInputDevice;
import roboy.io.MultiOutputDevice;
import roboy.linguistics.sentenceanalysis.Analyzer;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.nodes.Interlocutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


/**
 * A Conversation is in charge of leading conversation with an interlocutor.
 * Its behaviour is defined through the StateBasedPersonality.
 * It communicates with the interlocutor via a MultiInputDevice and a MultiOutputDevice.
 * The List of analyzers is used to make the input string machine understandable.
 */
public class Conversation extends Thread {

    private final Logger logger = LogManager.getLogger("Conversation" + this.getId());

    private final MultiInputDevice multiIn;
    private final MultiOutputDevice multiOut;
    private final List<Analyzer> analyzers;
    private final File personalityFile;
    private final StateBasedPersonality personality;
    /* isRunning specifies that the thread is running, paused specifies if the conversation has been terminated or paused:
    isRunning: Thread (therefore conversation) is running
    !isRunning && paused: Conversation paused, thread not running
    !isRunning && !paused: Conversation and thread ended
     */
    private volatile boolean isRunning = true;
    private volatile boolean paused = false;
    private List<Action> actions;

    /**
     *
     * @param personality roboy.dialog.personality.StateBasedPersonality object.
     * @param personalityFile File that the personality shall be initialized from.
     * @param multiIn Inputs for this conversation to act on.
     * @param multiOut Outputs for this conversation to act to.
     * @param analyzers All analyzers necessary for analyzing the inputs from multiIn. Please provide these in correct order.
     */
    public Conversation( StateBasedPersonality personality, File personalityFile, MultiInputDevice multiIn, MultiOutputDevice multiOut, List<Analyzer> analyzers){
        super("roboy-conversation");
        this.multiIn = multiIn;
        this.multiOut = multiOut;
        this.analyzers = analyzers;
        this.personalityFile = personalityFile;
        this.personality = personality;
        try {
            this.personality.loadFromFile(this.personalityFile);
        }catch(FileNotFoundException fnfe){
            logger.error("Personality file not found: " + fnfe.getMessage());
        }
    }

    /**
     * Ends conversation and resets state to initial. Does not reset gathered information.
     */
    synchronized void endConversation(){//Ends conversation including
        isRunning = false;
        personality.reset();
        this.interrupt();//to wake conversations that wait for input

        //Say bye
        List<Action> l = new ArrayList<>();
        l.add(new SpeechAction("Sorry. It seems I have to stop playing now. See you soon. Bye!"));
        multiOut.act(l);

        logger.info("############# Conversation forcibly ended ############");
    }


    @Override
    public void run(){

        //start conversation
        if(paused){//if this is a restart
            paused = false;
            logger.info("############# Conversation restarted ############");
        } else {//if this is an initial start
            actions = personality.startConversation();
            logger.info("############# Conversation started ############");
        }

        while (isRunning) {
            // do all actions defined in startConversation() or answer()
            multiOut.act(actions);

            // now stop if conversation ended
            if (personality.conversationEnded()) {
                isRunning = false;
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

            // analyze
            Interpretation interpretation = new Interpretation(raw.getSentence(), raw.getAttributes());
            for (Analyzer a : analyzers) {
                try {
                    interpretation = a.analyze(interpretation);
                } catch (Exception e) {
                    logger.error("Exception in analyzer " + a.getClass().getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            logger.debug(interpretation.toString());

            // answer
            try {
                actions = personality.answer(interpretation);
            } catch (Exception e) {
                logger.error("Error in personality.answer: " + e.getMessage());
                e.printStackTrace();
            }
        }
        if(!paused){//if this thread is about to die
            multiIn.cleanup();
            multiOut.cleanup();
            ConversationManager.deregisterConversation(this);
        }
    }
}
