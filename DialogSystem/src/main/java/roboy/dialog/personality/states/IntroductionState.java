package roboy.dialog.personality.states;

import java.util.List;

import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;

/**
 * Roboy introduces himself and asks "How are you?". Moves to success state if the answer
 * is at most 2 words.
 */
public class IntroductionState extends AbstractBooleanState{

	@Override
	public List<Interpretation> act() {
		return Lists.interpretationList(new Interpretation("I am Roboy. A humanoid robot. I'm pleased to meet you. How are you today?"));
	}

	@Override
	protected boolean determineSuccess(Interpretation input) {
		String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
		return sentence.split(" ").length<=2;
	}

}
