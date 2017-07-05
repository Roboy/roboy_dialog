package roboy.dialog.personality.states;

import java.util.List;

import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.WorkingMemory;
import roboy.util.Lists;

public class QuestionRandomizerState implements State{
	
	private static final String PROFESSION = "does";
	private static final String ORIGIN = "is from";
	private static final String HOBBY = "enjoys";
	private static final String MOVIE = "likes the movie";
	
	private PersonalQAState[] questionStates;
	private boolean[] alreadyAsked;
	private State inner;
	private State chosenState;
	
	public QuestionRandomizerState(State inner) {
		this.inner = inner;
		questionStates = new PersonalQAState[]{
			new PersonalQAState(
					Lists.stringList("What do you do for a living?"), 
					Lists.stringList("Can you explain what you do there?"), 
					Lists.strArray(new String[]{"And do you enjoy ",""}), 
					PROFESSION),
			new PersonalQAState(
					Lists.stringList("Where are you from?"), 
					Lists.stringList("Oh, I have never heard of that."), 
					Lists.strArray(new String[]{"Oh, I should visit ",""}), 
					ORIGIN),
			new PersonalQAState(
					Lists.stringList("How do you spend your free time?"), 
					Lists.stringList("Tell me more about that. Is that fun?"), 
					Lists.strArray(new String[]{""," Do you think a robot can do that as well?"}), 
					HOBBY),
			new PersonalQAState(
					Lists.stringList("What is your favourite movie?"), 
					Lists.stringList("Haven't heard of it. Who stars in it?"), 
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
		if(nameTriples.isEmpty()) inner.act();
		String name = nameTriples.get(0).patiens;
//		List<Triple> infos = memory.retrieve(new Triple(null,name,null));
//		infos.addAll(memory.retrieve(new Triple(null,null,name)));
		if(Math.random()<0.2){
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
		for(int i=0; i<questionStates.length; i++){
			questionStates[i].setNextState(top);
		}
	}
	
}
