package roboy.dialog.personality.states;

import java.util.List;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SENTENCE_TYPE;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;
import roboy.util.Maps;

/**
 * Utters the given text and moves to the given state. Used for telling anecdotes.
 */
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
