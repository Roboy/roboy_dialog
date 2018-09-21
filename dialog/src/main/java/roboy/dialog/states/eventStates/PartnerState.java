package roboy.dialog.states.eventStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.talk.Verbalizer;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.util.Set;

/**
 * Personal Information Asking State
 *
 * Sugar for Wacker
 */
public class PartnerState extends State {
    private QAJsonParser qaValues;
    private RandomList<String> intents;
    private String selectedPredicate;
    private State nextState;

    private final String NEXT_STATE = "nextState";
    private final String QA_FILE_PARAMETER_ID = "qaFile";
    final Logger LOGGER = LogManager.getLogger();

    public final static String INTENTS_HISTORY_ID = "WK";

    // we have to track question's index of the predicate OTHER, since the answer's order matters
    private int currentIdx = 0;

    public PartnerState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        String qaListPath = params.getParameter(QA_FILE_PARAMETER_ID);
        LOGGER.info(" -> The QAList path: " + qaListPath);
        qaValues = new QAJsonParser(qaListPath);
        intents = qaValues.getIntents();
    }

    @Override
    public Output act() {

        String question;
        selectedPredicate = intents.getRandomElement();
        RandomList<String> questions = qaValues.getQuestions(selectedPredicate);

        if (questions != null && !questions.isEmpty()) {
            question = questions.getRandomElement();
            currentIdx = questions.indexOf(question);
        } else {
            LOGGER.error(" -> The list of " + selectedPredicate + " questions is empty or null");
            return Output.sayNothing();
        }

        return Output.say(question);

    }

    @Override
    public Output react(Interpretation input) {
        if(StatementInterpreter.isFromList(input.getSentence(),Verbalizer.consent)) {
            return Output.say(qaValues.getSuccessAnswers(selectedPredicate).getRandomElement());
        }
        if(StatementInterpreter.isFromList(input.getSentence(),Verbalizer.denial)) {
            return Output.say(qaValues.getFailureAnswers(selectedPredicate).getRandomElement());
        }
        return Output.useFallback();
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        // optional: define all required transitions here:
        return newSet(NEXT_STATE);
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet(QA_FILE_PARAMETER_ID);
    }

}
