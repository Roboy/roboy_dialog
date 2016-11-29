package de.roboy.dialog.personality.states;

import java.util.List;

import de.roboy.dialog.action.Action;
import de.roboy.linguistics.sentenceanalysis.Sentence;

public interface State {
	
	public List<Action> act();

	public Reaction react(Sentence input);
}
