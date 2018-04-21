package roboy.dialog.states.searchStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jProperty;
import roboy.memory.nodes.Interlocutor;
import roboy.dialog.Segue;
import roboy.util.RandomList;

import java.util.*;

/**
 * This state will:
 * - ask the interlocutor for his name
 * - query memory if the person is already known
 * - create and update the interlocutor in the context
 * - take one of two transitions: knownPerson or newPerson
 *
 * IntroductionState interface:
 * 1) Fallback is not required.
 * 2) Outgoing transitions that have to be defined:
 *    - knownPerson:    following state if the person is already known
 *    - newPerson:      following state if the person is NOT known
 * 3) No parameters are used.
 */
public class SearchIntroductionState extends State {
    private final String TRANSITION_INTRO_DONE = "questionAnswering";
    private final Logger LOGGER = LogManager.getLogger();
    private final RandomList<String> introPhrases = new RandomList<>("I am Roboy 2.0! What's your name?");
    private final RandomList<String> successResponsePhrases = new RandomList<>("Hey, I know you, %s!");
    private final RandomList<String> failureResponsePhrases = new RandomList<>("Nice to meet you, %s!");

    private State nextState;

    public SearchIntroductionState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.say(getIntroPhrase());
    }

    @Override
    public Output react(Interpretation input) {
        String name = getNameFromInput(input);

        if (name == null) {
            name = "random citizen";
        }

        nextState = getTransition(TRANSITION_INTRO_DONE);
        Segue s = new Segue(Segue.SegueType.DISTRACT, 0.2);
        return Output.say(String.format(successResponsePhrases.getRandomElement(), name)).setSegue(s);
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    private String getNameFromInput(Interpretation input) {
        return getInference().inferProperty(Neo4jProperty.name, input);
    }

    private void updateInterlocutorInContext(Interlocutor interlocutor) {
        Context.getInstance().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(interlocutor);
    }

    private String getIntroPhrase() {
        return introPhrases.getRandomElement();
    }

    private String getResponsePhrase(String name, boolean familiar) {
        if (familiar) {
            return String.format(successResponsePhrases.getRandomElement(), name);
        } else {
            return String.format(failureResponsePhrases.getRandomElement(), name);
        }
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANSITION_INTRO_DONE);
    }
}
