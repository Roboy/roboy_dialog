package de.roboy.dialog.personality;

import java.util.List;

import de.roboy.dialog.action.Action;
import de.roboy.linguistics.sentenceanalysis.Interpretation;

public interface Personality {

	public List<Action> answer(Interpretation input);
}
