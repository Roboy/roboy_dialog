package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.nodes.Interlocutor;
import roboy.dialog.Segue;
import roboy.util.RandomList;

import java.util.*;

import static roboy.memory.Neo4jProperty.*;



/**
 * This state will:
 * - ask the interlocutor for his name
 * - create and update the interlocutor in the context
 * - take one transition: roboyInfo
 *
 * ExpoIntroductionState interface:
 * 1) Fallback is not required.
 * 2) Outgoing transitions that have to be defined:
 *    - roboyInfo:    following state if the name was given
 * 3) No parameters are used.
 */
public class ExpoIntroductionState extends State {
    private final String INTERLOCUTOR_NAME_OBTAINED = "roboyInfo";
    private final Logger LOGGER = LogManager.getLogger();
    private final RandomList<String> introPhrases = new RandomList<>("What's your name?", "Could you tell me your name?");
    private final RandomList<String> responsePhrases = new RandomList<>("Nice to meet you, %s!", "I am glad to meet you, %s!");

    private State nextState;

    public ExpoIntroductionState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.say(getIntroPhrase());
    }

    @Override
    public Output react(Interpretation input) {
        // 1. get name
        String name = getNameFromInput(input);

        if (name == null) {
            nextState = this;
            LOGGER.warn("IntroductionState couldn't get name! Staying in the same state.");
            return Output.say("Sorry, my parser is out of service.");
        }

        // 2. get interlocutor object from context
        // this also should query memory and do other magic
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();
        person.addName(name);

        // 3. update interlocutor in context
        updateInterlocutorInContext(person);
        Double segueProbability = 0.25;

        nextState = getTransition(INTERLOCUTOR_NAME_OBTAINED);
        Segue s = new Segue(Segue.SegueType.DISTRACT, segueProbability);
        return Output.say(getResponsePhrase(name)).setSegue(s);
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    private String getNameFromInput(Interpretation input) {
        return getInference().inferProperty(name, input);
    }

    private void updateInterlocutorInContext(Interlocutor interlocutor) {
        Context.getInstance().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(interlocutor);
    }

    private String getIntroPhrase() {
        return introPhrases.getRandomElement();
    }

    private String getResponsePhrase(String name) {
        return String.format(responsePhrases.getRandomElement(), name);
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(INTERLOCUTOR_NAME_OBTAINED);
    }
}
