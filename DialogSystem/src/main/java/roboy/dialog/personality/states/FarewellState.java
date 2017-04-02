package roboy.dialog.personality.states;

import java.util.List;

import roboy.linguistics.Linguistics.SENTENCE_TYPE;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;

public class FarewellState implements State{

	@Override
	public List<Interpretation> act() {
		return Lists.interpretationList(new Interpretation(SENTENCE_TYPE.FAREWELL));
	}

	@Override
	public Reaction react(Interpretation input) {
		return new Reaction(this);
	}

}
