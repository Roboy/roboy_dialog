package de.roboy.dialog.personality.states;

import java.util.List;

import de.roboy.linguistics.Linguistics;
import de.roboy.linguistics.sentenceanalysis.Interpretation;
import de.roboy.util.Lists;

public class IntroductionState extends AbstractBooleanState{

	@Override
	public List<Interpretation> act() {
		return Lists.interpretationList(new Interpretation("Who are you?"));
	}

	@Override
	public Reaction react(Interpretation input) {
		String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
		if(sentence.split(" ").length>2){
			return new Reaction(failure, Lists.interpretationList(new Interpretation("I did not ask for your life story, just your name. So again: ")));
		} else {
			return new Reaction(success);
		}
	}

}
