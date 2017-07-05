package roboy.dialog.personality.states;

import java.util.List;
import java.util.Map;

import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.WorkingMemory;
import roboy.util.JsonUtils;
import roboy.util.Lists;

public class QuestionRandomizerState implements State{
	
	private static final String PROFESSION = "does";
	private static final String ORIGIN = "is from";
	private static final String HOBBY = "enjoys";
	private static final String MOVIE = "likes the movie";
	
	private PersonalQAState[] questionStates;
	private PersonalQAState locationQuestion;
	private boolean[] alreadyAsked;
	private State inner;
	private State chosenState;
	
	public QuestionRandomizerState(State inner) {
		this.inner = inner;
		//Fetch the map: question type ("name", "occupation") -> list of phrasings for the question.
		String questionsFile = "questions/questions.json";
		Map<String, List<String>> questions = JsonUtils.getQuestionFromJsonFile(questionsFile);
		locationQuestion = new PersonalQAState(
				questions.get("origin"),
				Lists.stringList("Oh, I have never heard of that."),
				Lists.strArray(new String[]{"Oh, I should visit ",""}),
				ORIGIN);
		LocationDBpedia locationDBpedia = new LocationDBpedia();
		locationDBpedia.setNextState(this);
		locationQuestion.setSuccess(locationDBpedia);
		questionStates = new PersonalQAState[]{
			locationQuestion,
			new PersonalQAState(
						questions.get("occupation"),
						Lists.stringList("Oh well, whatever"),
						Lists.strArray(new String[]{"You are probably very poor doing ",""}),
						PROFESSION),
			 new PersonalQAState(
					questions.get("hobby"),
					Lists.stringList("Don't know what that is, but good luck!"),
					Lists.strArray(new String[]{"Just like me, I love "," too"}),
					HOBBY),
			new PersonalQAState(
					questions.get("movies"),
					Lists.stringList("Haven't heard of it. Probably it's shit"),
					Lists.strArray(new String[]{"Oh, I would watch ",". If those vision guys finally fixed my perception."}),
					MOVIE)
		};
		alreadyAsked = new boolean[questionStates.length];
	}

	@Override
	public List<Interpretation> act() {
		WorkingMemory memory = WorkingMemory.getInstance();
		chosenState = null;
		List<Triple> nameTriples = memory.retrieve(new Triple("is","name",null));
		if(nameTriples.isEmpty()) return inner.act();
		String name = nameTriples.get(0).patiens;
//		List<Triple> infos = memory.retrieve(new Triple(null,name,null));
//		infos.addAll(memory.retrieve(new Triple(null,null,name)));
		if(Math.random()<0.5){
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
