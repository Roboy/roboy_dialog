package de.roboy.dialog.personality.states;

import java.util.List;

import de.roboy.dialog.action.Action;
import de.roboy.dialog.action.SpeechAction;
import de.roboy.linguistics.sentenceanalysis.Interpretation;
import de.roboy.util.Lists;

public class IntroductionState extends AbstractBooleanState{

	@Override
	public List<Action> act() {
		return Lists.actionList(new SpeechAction("Who are you?"));
	}

	@Override
	public Reaction react(Interpretation input) {
		if(input.sentence.split(" ").length>2){
			return new Reaction(failure, Lists.actionList(new SpeechAction("I did not ask for your life story, just your name. So again: ")));
		} else {
			return new Reaction(success);
		}
	}

}
