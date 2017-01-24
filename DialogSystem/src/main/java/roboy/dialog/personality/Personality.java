package roboy.dialog.personality;

import java.util.List;

import roboy.dialog.action.Action;
import roboy.linguistics.sentenceanalysis.Interpretation;

public interface Personality {

	public List<Action> answer(Interpretation input);
}
