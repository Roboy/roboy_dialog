package roboy.dialog.personality.states;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelationships;
import roboy.memory.nodes.Interlocutor;
import roboy.util.JsonUtils;

import static roboy.memory.Neo4jRelationships.*;

/**
 * Manages the questions that can be asked from a person.
 * Coupled with Neo4j information about the person to prevent duplicates.
 */
public class QuestionRandomizerState implements State{
	// Favorite movie currently not represented in memory.
	//private static final String MOVIE = "likes the movie";
	// TODO ask memory to add relation.
	// TODO There are new predicates from memory, need integrating.
	
	private PersonalQAState[] questionStates;
	private PersonalQAState locationQuestion;
	private PersonalFollowUpState[] followUpStates;
	private HashMap<Neo4jRelationships, Boolean> alreadyAsked;
	private State inner;
	private State chosenState;
	private Interlocutor person;
	boolean askFollowUp = true;

	// All spoken phrases for asking questions are stored in these JSON files.
	String questionsFile = "sentences/questions.json";
	String successAnswersFile = "sentences/successAnswers.json";
	String failureAnswersFile = "sentences/failureAnswers.json";
	String followUpFile = "sentences/followUp.json";
	String answersFollowUpFile = "sentences/answersFollowUp.json";
	Map<String, List<String>> questions;
	Map<String, List<String[]>> successAnswers;
	Map<String, List<String>> failureAnswers;
	Map<String, List<String>> followUp;
	Map<String, List<String[]>> answersFollowUp;
	
	public QuestionRandomizerState(State inner, Interlocutor person) {
		this.inner = inner;
		this.person = person;
		questions = JsonUtils.getSentencesFromJsonFile(questionsFile);
		successAnswers = JsonUtils.getSentenceArraysFromJsonFile(successAnswersFile);
		failureAnswers = JsonUtils.getSentencesFromJsonFile(failureAnswersFile);
		followUp = JsonUtils.getSentencesFromJsonFile(followUpFile);
		answersFollowUp = JsonUtils.getSentenceArraysFromJsonFile(answersFollowUpFile);
		// alreadyAsked is filled automatically by the initializeQuestion method,
		// then updated to match already existing information with checkForAskedQuestions()
		alreadyAsked = new HashMap<>();

		locationQuestion = initializeQuestion(FROM);
		LocationDBpedia locationDBpedia = new LocationDBpedia();
		locationDBpedia.setSuccess(this);
		locationDBpedia.setFailure(this);
		locationQuestion.setSuccess(locationDBpedia);
		questionStates = new PersonalQAState[]{
				locationQuestion,
//				initializeQuestion(HAS_HOBBY),
//				initializeQuestion(WORK_FOR),
//				initializeQuestion(STUDY_AT)
// 		TODO request support for Occupation and Movie data in the database.
			 initializeQuestion(OTHER),
//			 initializeQuestion(LIKES_MOVIE),
		};
		followUpStates = new PersonalFollowUpState[] {
				initializeFollowUpQuestion(FROM),
				initializeFollowUpQuestion(HAS_HOBBY),
				initializeFollowUpQuestion(WORK_FOR),
				initializeFollowUpQuestion(STUDY_AT)
		};
	}

	@Override
	public List<Interpretation> act() {
		checkForAskedQuestions();
		// TODO Remove old code after successfully switching to Neo4j memory
		//WorkingMemory memory = WorkingMemory.getInstance();

		// chosenState refers to the question to be asked.
		chosenState = null;
		// List<Triple> nameTriples = memory.retrieve(new Triple("is","name",null));
		// if(nameTriples.isEmpty()) return inner.act();
		// String name = nameTriples.get(0).patiens;
		//List<Triple> infos = memory.retrieve(new Triple(null,name,null));
		//infos.addAll(memory.retrieve(new Triple(null,null,name)));
		//TODO why this if statement?
		if(Math.random()<1){
			int index = (int) (Math.random()*questionStates.length);
			if(!alreadyAsked.get(questionStates[index].predicate)){
				alreadyAsked.put(questionStates[index].predicate, true);
				chosenState = questionStates[index];
                return chosenState.act();
			} else if (askFollowUp) {
				index = (int) (Math.random() * followUpStates.length);
				chosenState = followUpStates[index];
				askFollowUp = false;
				return chosenState.act();
			}
		}
		return inner.act();
	}

	@Override
	public Reaction react(Interpretation input) {
		if(chosenState==null){
			return inner.react(input);
		}
		return chosenState.react(input);
	}

	public void setTop(State top){
		for(int i=1; i<questionStates.length; i++){
			questionStates[i].setNextState(top);
		}
	}

	private PersonalQAState initializeQuestion(Neo4jRelationships relationship) {
		alreadyAsked.put(relationship, false);
		return new PersonalQAState(
				questions.get(relationship.type),
				failureAnswers.get(relationship.type),
				successAnswers.get(relationship.type),
				relationship, person);
	}

	private PersonalFollowUpState initializeFollowUpQuestion(Neo4jRelationships relationship) {
		return new PersonalFollowUpState(
				followUp.get(relationship.type),
				failureAnswers.get(relationship.type),
                answersFollowUp.get(relationship.type),
				relationship, this, person);
	}

	private void checkForAskedQuestions() {
	for(Neo4jRelationships relationship : alreadyAsked.keySet()) {
			if(person.hasRelationship(relationship)) {
				alreadyAsked.put(relationship, true);
			}
		}
	}
	
}
