package de.roboy.dialog.personality.states;

import java.util.List;

import de.roboy.linguistics.Linguistics.SENTENCE_TYPE;
import de.roboy.linguistics.sentenceanalysis.Interpretation;
import de.roboy.util.Lists;

public class FarewellState implements State{

	@Override
	public List<Interpretation> act() {
		return Lists.interpretationList(new Interpretation(SENTENCE_TYPE.FAREWELL)); //(new ShutDownAction(Lists.actionList()));
	}

	@Override
	public Reaction react(Interpretation input) {
		return new Reaction(this);
	}

}
