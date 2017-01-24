package roboy.dialog.personality.states;

import java.util.List;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SENTENCE_TYPE;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.talk.Verbalizer;
import roboy.util.Lists;

public class GreetingState extends AbstractBooleanState{

	
	@Override
	public List<Interpretation> act() {
		return Lists.interpretationList(new Interpretation(SENTENCE_TYPE.GREETING));
	}

	@Override
	public Reaction react(Interpretation input) {
		String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
		if("".equals(sentence)){
			return new Reaction(this,Lists.interpretationList()); // new Interpretation(SENTENCE_TYPE.GREETING)
		}
		return super.react(input);
	}

	@Override
	protected boolean determineSuccess(Interpretation input) {
		String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
		return StatementInterpreter.isFromList(sentence, Verbalizer.greetings);
	}

}
