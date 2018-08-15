package roboy.dialog.states.botboy;

import org.apache.jena.atlas.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.talk.Verbalizer;

import java.util.HashMap;
import java.util.Map;

/**
 * Start a conversation with telegram.
 * Try to detect a greeting or roboy names or some key word that initialize the conversation.
 */
public class BotBoyState extends State {

    private final Logger logger = LogManager.getLogger();
    private final String TRANSITION_INITIALIZED = "initialized";
    private State next;

    public BotBoyState(String stateIdentifiers, StateParameters params){
        super(stateIdentifiers, params);
    }

    // first we wait for conversation to start
    @Override
    public Output act() {
        return Output.sayNothing();
    }


    @Override
    public Output react(Interpretation input) {
        String sentence = input.getSentence();

        boolean inputOK = StatementInterpreter.isFromList(sentence, Verbalizer.greetings) ||
                StatementInterpreter.isFromList(sentence, Verbalizer.roboyNames) ||
                StatementInterpreter.isFromList(sentence, Verbalizer.triggers);

        if(inputOK){
            next = getTransition(TRANSITION_INITIALIZED);
            return Output.say(Verbalizer.greetings.getRandomElement());
        }

        return Output.sayNothing();
    }

    @Override
    public State getNextState() {
        return next;
    }


}