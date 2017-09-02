package roboy.dialog.personality.states;

import java.util.List;
import java.util.Map;

import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.nodes.Interlocutor;
import roboy.util.JsonUtils;

import static roboy.memory.Neo4jRelations.FROM;
import static roboy.memory.Neo4jRelations.HAS_HOBBY;
import static roboy.memory.Neo4jRelations.OCCUPATION;

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
	private boolean[] alreadyAsked;
	private State inner;
	private State chosenState;
	private Interlocutor person;
	
	public QuestionRandomizerState(State inner, Interlocutor person) {
		this.inner = inner;
		this.person = person;
		//Fetch the map: question type ("name", "occupation") -> list of phrasings for the question.
		String questionsFile = "sentences/questions.json";
		String successAnswersFile = "sentences/successAnswers.json";
		String failureAnswersFile = "sentences/failureAnswers.json";
		Map<String, List<String>> questions = JsonUtils.getSentencesFromJsonFile(questionsFile);
		Map<String, List<String[]>> successAnswers = JsonUtils.getSentenceArraysFromJsonFile(successAnswersFile);
		Map<String, List<String>> failureAnswers = JsonUtils.getSentencesFromJsonFile(failureAnswersFile);

		locationQuestion = new PersonalQAState(
				questions.get(FROM.type),
				failureAnswers.get(FROM.type),
				successAnswers.get(FROM.type),
				FROM.type, person);
		LocationDBpedia locationDBpedia = new LocationDBpedia();
		locationDBpedia.setSuccess(this);
		locationDBpedia.setFailure(this);
		locationQuestion.setSuccess(locationDBpedia);
		questionStates = new PersonalQAState[]{
			locationQuestion,
// TODO request support for Occupation data in the database.
//			new PersonalQAState(
//					questions.get(OCCUPATION.type),
//					failureAnswers.get(OCCUPATION.type),
//					successAnswers.get(OCCUPATION.type),
//					OCCUPATION.type, person),
			 new PersonalQAState(
					 questions.get(HAS_HOBBY.type),
					 failureAnswers.get(HAS_HOBBY.type),
					 successAnswers.get(HAS_HOBBY.type),
					 HAS_HOBBY.type, person),
//			new PersonalQAState(
//					questions.get("movies"),
//					Lists.stringList("Haven't heard of it. Probably it's shit"),
//					Lists.strArray(new String[]{"Oh, I would watch ",". If those vision guys finally fixed my perception."}),
//					MOVIE)
		};
		alreadyAsked = new boolean[questionStates.length];
	}

	@Override
	public List<Interpretation> act() {
		// Check if the question has already been answered.
		// TODO Make parsing independent of the exact definition of questionStates.
		if(person.hasRelation(FROM)) alreadyAsked[0] = true;
		if(person.hasRelation(HAS_HOBBY)) alreadyAsked[1] = true;
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
			if(!alreadyAsked[index]){
				alreadyAsked[index] = true;
				chosenState = questionStates[index];
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
	
}
