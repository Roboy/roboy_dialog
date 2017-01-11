package de.roboy.dialog.personality.states;

import java.util.List;

import de.roboy.linguistics.Linguistics;
import de.roboy.linguistics.Linguistics.SENTENCE_TYPE;
import de.roboy.linguistics.sentenceanalysis.Interpretation;
import de.roboy.util.Lists;
import de.roboy.util.Maps;

public class AnecdoteState implements State{
	
	private State nextState;
	private String anecdote; // later this should be more complex
	
	public AnecdoteState(State nextState, String anecdote){
		this.nextState = nextState;
		this.anecdote = anecdote;
	}
	
	@Override
	public List<Interpretation> act() {
		return Lists.interpretationList(
				new Interpretation(SENTENCE_TYPE.ANECDOTE, 
						Maps.stringObjectMap(Linguistics.SENTENCE,anecdote)));
	}

	@Override
	public Reaction react(Interpretation input) {
		return new Reaction(nextState);
	}

}
