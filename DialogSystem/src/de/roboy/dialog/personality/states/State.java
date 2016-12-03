package de.roboy.dialog.personality.states;

import java.util.List;

import de.roboy.dialog.action.Action;
import de.roboy.linguistics.sentenceanalysis.Interpretation;

public interface State {
	
	public List<Action> act();

	public Reaction react(Interpretation input);
}
