package roboy.dialog.personality.states;

import java.util.ArrayList;
import java.util.List;

import roboy.linguistics.sentenceanalysis.Interpretation;

public class Reaction {

	private List<Interpretation> reactions;
	private State state;
	
	public Reaction(State state, List<Interpretation> reactions){
		this.state = state;
		this.reactions = reactions;
	}
	
	public Reaction(State state){
		this.state=state;
		reactions = new ArrayList<Interpretation>();
	}

	public List<Interpretation> getReactions() {
		return reactions;
	}

	public State getState() {
		return state;
	}
	
	public void setState(State state){
		this.state = state;
	}
	
}
