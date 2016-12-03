package de.roboy.dialog.personality.states;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.roboy.dialog.action.Action;
import de.roboy.dialog.action.SpeechAction;
import de.roboy.linguistics.sentenceanalysis.Interpretation;
import de.roboy.logic.StatementInterpreter;
import de.roboy.talk.StatementBuilder;
import de.roboy.util.Lists;

public class GreetingState extends AbstractBooleanState{

	private static final List<String> greetings = 
			Arrays.asList("hello","hi","greetings","good morning","howdy","good day","hey");
	
	@Override
	public List<Action> act() {
		return Lists.actionList(new SpeechAction(StatementBuilder.random(greetings)));
	}

	@Override
	public Reaction react(Interpretation input) {
		if("".equals(input.sentence)) return new Reaction(this,new ArrayList<Action>());
		boolean successful = StatementInterpreter.isFromList(input.sentence, greetings);
		if(successful){
			return new Reaction(success);
		} else {
			return new Reaction(failure,Lists.actionList(new SpeechAction("Is that a way to greet somebody? Let's try that again.")));
		}
	}

}
