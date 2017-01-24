package roboy.dialog.personality.states;

import java.util.List;

import roboy.linguistics.sentenceanalysis.Interpretation;

public interface State {
	
	public List<Interpretation> act();

	public Reaction react(Interpretation input);
}
