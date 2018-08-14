package roboy.dialog.states.fairShowStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.ros.RosMainNode;
import roboy.talk.PhraseCollection;

/**
 * State where Roboy can calculate mathematical expressions
 */
public class MathState extends State {

    private final static String TRANSITION_FINISHED = "finished";

    private final Logger LOGGER = LogManager.getLogger();

    private State nextState = this;

    public MathState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {

        return Output.say(PhraseCollection.OFFER_MATH_PHRASES.getRandomElement());
    }

    @Override
    public Output react(Interpretation input){

        nextState = getTransition(TRANSITION_FINISHED);

        return Output.say(getAnswerFromSemanticParser(input, getContext().ACTIVE_INTERLOCUTOR.getValue().getName(), getRosMainNode()));
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    private String getAnswerFromSemanticParser(Interpretation input, String name, RosMainNode rmn) {

        Linguistics.ParsingOutcome parserOutcome = input.getParsingOutcome();
        if (parserOutcome == Linguistics.ParsingOutcome.SUCCESS) {
            if (input.getAnswer() != null) {
                String result = input.getAnswer();
                LOGGER.info("Parsing was successful! The result is " + result);
                return String.format(PhraseCollection.CONNECTING_PHRASES.getRandomElement(), name) + result;
            } else {
                LOGGER.error("Parsing failed! Answer is null!");
            }
        }

        LOGGER.error("Parsing failed! Invalid parser outcome!");
        String generativeAnswer = rmn.GenerateAnswer(input.getSentence());
        return generativeAnswer != null ? generativeAnswer : PhraseCollection.PARSER_ERROR.getRandomElement();
    }

}
