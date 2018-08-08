package roboy.dialog.states.multiPartyDialogStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.dialog.states.definitions.State.Output;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jProperty;
import roboy.memory.Neo4jRelationship;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.Interlocutor.RelationshipAvailability;
import roboy.memory.nodes.Roboy;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.dialog.Segue;
import roboy.util.Agedater;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;

import java.util.*;

import static roboy.memory.Neo4jRelationship.*;
import static roboy.memory.Neo4jProperty.*;
import static roboy.memory.nodes.Interlocutor.RelationshipAvailability.*;

public class IntroductionStateMPD extends State{
	
	private final RandomList<String> introPhrases = new RandomList<>("What's your name?");
	private final Logger LOGGER = LogManager.getLogger();
	
	private State nextState;

	public IntroductionStateMPD(String stateIdentifier, StateParameters params) {
		super(stateIdentifier, params);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Output act() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Output react(Interpretation input) {
		// TODO Check form of interpretation input -> might need another format 
		// expecting something like "My name is NAME"

        // TODO 1. Check number of speakers
		// TODO 2. two possibilities: one speaker or two 
		
		// 3. get name -> case: one speaker 
		//TODO: Input might be different -> changes how we get the name... 
        String name = getNameFromInput(input);
        
        if (name == null) {
            // input couldn't be parsed properly
            // TODO: do something intelligent if the parser fails
            nextState = this;
            LOGGER.warn("IntroductionState couldn't get name! Staying in the same state.");
            return Output.say("Sorry, my parser is out of service.");
            // alternatively: Output.useFallback() or Output.sayNothing()
        }
        
		return Output.say(getIntroPhrase());
	}

	@Override
	public State getNextState() {
		// TODO Auto-generated method stub
		return null;
	}
	
    private String getIntroPhrase() {
        return introPhrases.getRandomElement();
    }
    
    // TODO 
    private String getNameFromInput(Interpretation input) {
        String result = null;
        if (input.getSentenceType().compareTo(Linguistics.SENTENCE_TYPE.STATEMENT) == 0) {
            String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
            if (tokens.length == 1) {
                result =  tokens[0].replace("[", "").replace("]","").toLowerCase();
                LOGGER.info(" -> Retrieved only one token: " + result);
                return result;
            } else {
                if (input.getFeatures().get(Linguistics.PARSER_RESULT).toString().equals("SUCCESS") &&
                        ((List<Triple>) input.getFeatures().get(Linguistics.SEM_TRIPLE)).size() != 0) {
                    LOGGER.info(" -> Semantic parsing is successful and semantic triple exists");
                    List<Triple> triple = (List<Triple>) input.getFeatures().get(Linguistics.SEM_TRIPLE);
                    result = triple.get(0).object.toLowerCase();
                    LOGGER.info(" -> Retrieved object " + result);
                } else {
                    LOGGER.warn(" -> Semantic parsing failed or semantic triple does not exist");
                    if (input.getFeatures().get(Linguistics.OBJ_ANSWER) != null) {
                        LOGGER.info(" -> OBJ_ANSWER exits");
                        String name = input.getFeatures().get(Linguistics.OBJ_ANSWER).toString().toLowerCase();
                        if (!name.equals("")) {
                            result = name;
                            LOGGER.info(" -> Retrieved OBJ_ANSWER result " + result);
                        } else {
                            LOGGER.warn(" -> OBJ_ANSWER is empty");
                        }
                    } else {
                        LOGGER.warn(" -> OBJ_ANSWER does not exit");
                    }
                }
            }
        }
        return result;
    }
	
}
