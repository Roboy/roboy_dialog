package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelationship;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.memory.nodes.Roboy;
import roboy.talk.PhraseCollection;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.util.List;

import static roboy.memory.Neo4jProperty.*;

public class RoboyQAState extends State {
    private final String SELECTED_SKILLS = "skills";
    private final String SELECTED_ABILITIES = "abilities";
    private final String LEARN_ABOUT_PERSON = "newPerson";

    private final RandomList<String> connectingPhrases = PhraseCollection.CONNECTING_PHRASES;
    private final RandomList<String> roboyIntentPhrases = PhraseCollection.INFO_ROBOY_INTENT_PHRASES;
    private final RandomList<String> parserError = PhraseCollection.PARSER_ERROR;
    private final String INFO_FILE_PARAMETER_ID = "infoFile";

    private final Logger LOGGER = LogManager.getLogger();

    private QAJsonParser infoValues;
    private State nextState;

    public RoboyQAState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        String infoListPath = params.getParameter(INFO_FILE_PARAMETER_ID);
        LOGGER.info(" -> The infoList path: " + infoListPath);
        infoValues = new QAJsonParser(infoListPath);
    }

    @Override
    public Output act() {
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();
        return Output.say(String.format(connectingPhrases.getRandomElement(), person.getName()) +
                roboyIntentPhrases.getRandomElement());
    }

    @Override
    public Output react(Interpretation input) {
        Roboy roboy = new Roboy(getMemory());
        String answer = inferMemoryAnswer(input, roboy);
        if (answer.equals("")) {
            return Output.say(parserError.getRandomElement());
        }
        nextState = getRandomTransition();
        return Output.say(answer);
    }

    private State getRandomTransition() {
        return this;
    }

    private String inferMemoryAnswer(Interpretation input, Roboy roboy) {
        String answer = "";
        if (input.semParserTriples != null) {
            List<Triple> triples = input.semParserTriples;
            for (Triple result : triples) {
                if (result.predicate != null) {
                    for (Neo4jRelationship predicate : Roboy.VALID_NEO4J_RELATIONSHIPS) {
                        if (result.predicate.contains(predicate.type)) {
                            String nodeName = extractNodeNameForPredicate(predicate, roboy);
                            if (nodeName != null) {
                                answer = String.format(infoValues.getSuccessAnswers(predicate).getRandomElement(), nodeName);
                                break;
                            }
                        }
                    }
                    if (!answer.equals("")){
                        break;
                    }
                }
            }
        }
        return answer;
    }

    private String extractNodeNameForPredicate(Neo4jRelationship predicate, Roboy roboy) {
        MemoryNodeModel node = getMemNodesByIds(roboy.getRelationships(predicate)).getRandomElement();
        if (node != null) {
            if (node.getProperties().containsKey(full_name.type) && !node.getProperties().get(full_name.type).equals("")) {
                return node.getProperties().get(full_name.type).toString();
            } else {
                return node.getProperties().get(name.type).toString();
            }
        }
        return null;
    }

    @Override
    public State getNextState() {
        return nextState;
    }
}
