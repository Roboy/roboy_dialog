package de.roboy.dialog.personality.states;

import java.util.Arrays;
import java.util.List;

import de.roboy.dialog.action.Action;
import de.roboy.dialog.action.ShutDownAction;
import de.roboy.dialog.action.SpeechAction;
import de.roboy.linguistics.sentenceanalysis.Interpretation;
import de.roboy.talk.StatementBuilder;
import de.roboy.util.Lists;

public class FarewellState implements State{

	private static final List<String> farewells = 
			Arrays.asList("ciao","goodbye","cheerio","bye","see you",
					"farewell","bye-bye");
	
	@Override
	public List<Action> act() {
		return Lists.actionList(new ShutDownAction(Lists.actionList(new SpeechAction(StatementBuilder.random(farewells)))));
	}

	@Override
	public Reaction react(Interpretation input) {
		return new Reaction(this);
	}

}
