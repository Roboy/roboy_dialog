package de.roboy.dialog.personality;

import java.util.Arrays;
import java.util.List;

import de.roboy.dialog.action.Action;
import de.roboy.dialog.personality.states.FarewellState;
import de.roboy.dialog.personality.states.GreetingState;
import de.roboy.dialog.personality.states.InquiryState;
import de.roboy.dialog.personality.states.IntroductionState;
import de.roboy.dialog.personality.states.Reaction;
import de.roboy.dialog.personality.states.State;
import de.roboy.linguistics.sentenceanalysis.Sentence;

public class SmallTalkPersonality implements Personality{

	private static final List<String> positive = 
			Arrays.asList("enthusiastic","awesome","great","very good",
					"dope","smashing","happy","cheerful","good","phantastic");
	private State state;
	
	public SmallTalkPersonality() {
		// build state machine
		GreetingState greetings = new GreetingState();
		IntroductionState intro = new IntroductionState();
		InquiryState inquiry = new InquiryState("How are you?", positive, "That's not good enough. Again: ");
		FarewellState farewell = new FarewellState();
		
		greetings.setSuccess(intro);
		greetings.setFailure(greetings);
		intro.setSuccess(inquiry);
		intro.setFailure(intro);
		inquiry.setSuccess(farewell);
		inquiry.setFailure(inquiry);
		
		state = greetings;
	}
	
	@Override
	public List<Action> answer(Sentence input) {
		Reaction reaction = state.react(input);
		List<Action> talk = reaction.getReactions();
		state = reaction.getState();
		talk.addAll(state.act());
		return talk;
	}

}
