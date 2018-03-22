package roboy.newDialog.states;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelationships;
import roboy.memory.nodes.Interlocutor;
import roboy.newDialog.Segue;
import roboy.util.QAJsonParser;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static roboy.memory.Neo4jRelationships.*;

/**
 * Personal Information Asking State
 */
public class PIAState extends State {
    private QAJsonParser qaValues;
    private Neo4jRelationships[] predicates = { FROM, HAS_HOBBY, WORK_FOR, STUDY_AT };
    private Neo4jRelationships selectedPredicate;
    private State nextState;

    private final String TRANSITION_INFO_OBTAINED = "questionAnswering";
    final Logger LOGGER = LogManager.getLogger();

    public PIAState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        qaValues = new QAJsonParser(params.getParameter("qaFile"));
    }

    @Override
    public State.Output act() {
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();

        for (Neo4jRelationships predicate : predicates) {
            if (!person.hasRelationship(predicate)) {
                selectedPredicate = predicate;
                break;
            }
        }
        List<String> questions = qaValues.getQuestions(selectedPredicate);
        String question = questions.get((int)(Math.random()*questions.size()));
        HashMap<String, String> INTENT_VALUE = new HashMap<>();
        INTENT_VALUE.put("PIA", selectedPredicate.type);
        Context.getInstance().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue("PIA", selectedPredicate));
        return State.Output.say(question);
    }

    @Override
    public State.Output react(Interpretation input) {
        // TODO: Get relevant data from the input
        // TODO: Update the Interlocutor
        // TODO: Update the Memory
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();
        List<String> answers = null;
        String answer = "I have no words";
        String result = "";
        // TODO: What is the condition?
        if (input.getSentenceType().compareTo(Linguistics.SENTENCE_TYPE.STATEMENT) == 0) {
            String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
            if (tokens.length == 1) {
                result = tokens[0].replace("[", "").replace("]","").toLowerCase();
                person.addInformation(selectedPredicate.type, result);
                Context.getInstance().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(person);
                answers = qaValues.getSuccessAnswers(selectedPredicate);
            } else {
                if (input.getFeatures().get(Linguistics.PARSER_RESULT).toString().equals("SUCCESS")) {
                    List<Triple> sem_triple = (List<Triple>) input.getFeatures().get(Linguistics.SEM_TRIPLE);
                    if (sem_triple.size() != 0) {
                        if (sem_triple.get(0).predicate.contains(selectedPredicate.type)) {
                            result = sem_triple.get(0).object.toLowerCase();
                            answers = qaValues.getSuccessAnswers(selectedPredicate);
                        } else {
                            answers = qaValues.getFailureAnswers(selectedPredicate);
                        }
                    } else {
                        if (input.getFeatures().get(Linguistics.OBJ_ANSWER) != null) {
                            result = input.getFeatures().get(Linguistics.OBJ_ANSWER).toString().toLowerCase();
                            if (!result.equals("")) {
                                answers = qaValues.getSuccessAnswers(selectedPredicate);
                            } else {
                                answers = qaValues.getFailureAnswers(selectedPredicate);
                            }
                        } else {
                            answers = qaValues.getFailureAnswers(selectedPredicate);
                        }
                    }
                } else {
                    if (input.getFeatures().get(Linguistics.OBJ_ANSWER) != null) {
                        result = input.getFeatures().get(Linguistics.OBJ_ANSWER).toString().toLowerCase();
                        if (!result.equals("")) {
                            answers = qaValues.getSuccessAnswers(selectedPredicate);
                        } else {
                            answers = qaValues.getFailureAnswers(selectedPredicate);
                        }
                    } else {
                        answers = qaValues.getFailureAnswers(selectedPredicate);
                    }
                }
            }
        } else {
            answers = qaValues.getFailureAnswers(selectedPredicate);
        }
        if (!result.equals("")) {
            person.addInformation(selectedPredicate.type, result);
            Context.getInstance().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(person);
        }
        if (answers != null && !answers.isEmpty()) {
            answer = String.format(answers.get((int) (Math.random() * answers.size())), result);
        }
        nextState = getTransition(TRANSITION_INFO_OBTAINED);
        return State.Output.say(answer).setSegue(new Segue(Segue.SegueType.CONNECTING_PHRASE, 0.5));
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        // optional: define all required transitions here:
        return newSet(TRANSITION_INFO_OBTAINED);
    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet("qaFile");
    }
}
