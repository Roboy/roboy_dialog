package roboy.dialog.personality.states;

import java.util.List;
import java.util.Map;

import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jRelations;
import roboy.memory.WorkingMemory;
import roboy.memory.nodes.Interlocutor;
import roboy.util.JsonUtils;
import roboy.util.Lists;

import static roboy.memory.Neo4jRelations.FROM;
import static roboy.memory.Neo4jRelations.HAS_HOBBY;

/**
 * Manages the questions that can be asked from a person.
 * Coupled with Neo4j information about the person to prevent duplicates.
 */
public class QuestionRandomizerState implements State{
	// Profession is currently not represented in memory.
	//private static final String PROFESSION = "does";
	// Favorite movie currently not represented in memory.
	//private static final String MOVIE = "likes the movie";
	// TODO ask memory to add relations.
	// TODO These are new predicates from memory, matching questions needed.
	
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
		String questionsFile = "questions/questions.json";
		Map<String, List<String>> questions = JsonUtils.getQuestionFromJsonFile(questionsFile);

		locationQuestion = new PersonalQAState(
				questions.get(FROM.type),
				Lists.stringList("Oh, I have never heard of that."),
				Lists.strArray(new String[]{"Oh, I should visit ",""}),
				FROM.type, person);
		LocationDBpedia locationDBpedia = new LocationDBpedia();
		locationDBpedia.setSuccess(this);
		locationDBpedia.setFailure(this);
		locationQuestion.setSuccess(locationDBpedia);
		questionStates = new PersonalQAState[]{
			locationQuestion,
//			new PersonalQAState(
//						questions.get("occupation"),
//						Lists.stringList("Oh well, whatever"),
//						Lists.strArray(new String[]{"You are probably very poor doing ",""}),
//						PROFESSION),
			 new PersonalQAState(
					questions.get(HAS_HOBBY.type),
					Lists.stringList("Don't know what that is, but good luck!"),
					Lists.strArray(new String[]{"Just like me, I love "," too"}),
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
