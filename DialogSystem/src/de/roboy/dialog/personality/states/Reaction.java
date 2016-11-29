package de.roboy.dialog.personality.states;

import java.util.ArrayList;
import java.util.List;

import de.roboy.dialog.action.Action;

public class Reaction {

	private List<Action> reactions;
	private State state;
	
	public Reaction(State state, List<Action> reactions){
		this.state = state;
		this.reactions = reactions;
	}
	
	public Reaction(State state){
		this.state=state;
		reactions = new ArrayList<Action>();
	}

	public List<Action> getReactions() {
		return reactions;
	}

	public State getState() {
		return state;
	}
	
}
