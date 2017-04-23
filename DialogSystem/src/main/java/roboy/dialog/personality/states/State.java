package roboy.dialog.personality.states;

import java.util.List;

import roboy.linguistics.sentenceanalysis.Interpretation;

/**
 * The central interface of the state machine. A state always acts when its enters and
 * reacts when its left. Both, the reaction of the last and the action of the next state,
 * are combined to give the answer of Roboy.
 */
public interface State {
	
	public List<Interpretation> act();

	public Reaction react(Interpretation input);
}
