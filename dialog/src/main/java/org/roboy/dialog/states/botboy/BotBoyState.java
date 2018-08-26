package org.roboy.dialog.states.botboy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.roboy.linguistics.sentenceanalysis.Interpretation;
import org.roboy.dialog.states.definitions.State;
import org.roboy.dialog.states.definitions.StateParameters;
import org.roboy.logic.StatementInterpreter;
import org.roboy.talk.Verbalizer;


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