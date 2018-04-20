package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics.UtteranceSentiment;
import roboy.linguistics.sentenceanalysis.Interpretation;


public class RoboyInfoState extends State {
    private final String SELECTED_SKILLS = "skills";
    private final String SELECTED_ABILITIES = "abilities";
    private final String SELECTED_ROBOY_QA = "roboy";
    private final String LEARN_ABOUT_PERSON = "newPerson";
    private final Logger LOGGER = LogManager.getLogger();

    private State nextState;

    public RoboyInfoState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.say("");
    }

    @Override
    public Output react(Interpretation input) {
        UtteranceSentiment inputSentiment = getInference().inferSentiment(input);
        if (inputSentiment.toBoolean == Boolean.TRUE) {
            nextState = null;
            return Output.say("");
        } else {
            nextState = getTransition(LEARN_ABOUT_PERSON);
            return Output.say("");
        }
    }

    @Override
    public State getNextState() {
        return nextState;
    }
}
