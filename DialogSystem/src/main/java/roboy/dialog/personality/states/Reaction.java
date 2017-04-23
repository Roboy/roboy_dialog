package roboy.dialog.personality.states;

import java.util.ArrayList;
import java.util.List;

import roboy.linguistics.sentenceanalysis.Interpretation;

/**
 * The reaction to what the other person said and did, consists of a list of interpretations,
 * which is an abstraction of an utterance (the verbalizer later formulates the utterance),
 * and a state into which the state machine moves.
 */
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
