package de.roboy.dialog.personality.states;

import java.util.List;

import de.roboy.linguistics.Linguistics;
import de.roboy.linguistics.Linguistics.SENTENCE_TYPE;
import de.roboy.linguistics.sentenceanalysis.Interpretation;
import de.roboy.logic.StatementInterpreter;
import de.roboy.talk.Verbalizer;
import de.roboy.util.Lists;

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
		boolean successful = StatementInterpreter.isFromList(sentence, Verbalizer.greetings);
		if(successful){
			return new Reaction(success);
		} else {
			return new Reaction(failure,Lists.interpretationList(new Interpretation("Is that a way to greet somebody? Let's try that again.")));
		}
	}

}
