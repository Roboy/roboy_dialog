package de.roboy.dialog.personality;

import java.util.List;

import de.roboy.dialog.action.Action;
import de.roboy.linguistics.sentenceanalysis.Sentence;

public interface Personality {

	public List<Action> answer(Sentence input);
}
