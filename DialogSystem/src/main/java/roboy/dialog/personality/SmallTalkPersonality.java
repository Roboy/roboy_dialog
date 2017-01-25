package roboy.dialog.personality;

import java.util.Arrays;
import java.util.List;

import roboy.dialog.action.Action;
import roboy.dialog.personality.states.FarewellState;
import roboy.dialog.personality.states.GenerativeCommunicationState;
import roboy.dialog.personality.states.GreetingState;
import roboy.dialog.personality.states.InquiryState;
import roboy.dialog.personality.states.IntroductionState;
import roboy.dialog.personality.states.QuestionAnsweringState;
import roboy.dialog.personality.states.Reaction;
import roboy.dialog.personality.states.SegueState;
import roboy.dialog.personality.states.State;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.talk.Verbalizer;
import roboy.util.Lists;

public class SmallTalkPersonality implements Personality{

	private static final List<String> positive = 
			Arrays.asList("enthusiastic","awesome","great","very good",
					"dope","smashing","happy","cheerful","good","phantastic");
	private State state;
	private Verbalizer verbalizer;
	
	public SmallTalkPersonality(Verbalizer verbalizer) {
		this.verbalizer = verbalizer;
		
		// build state machine
		GreetingState greetings = new GreetingState();
		IntroductionState intro = new IntroductionState();
		InquiryState inquiry = new InquiryState("How are you?", positive, "That's not good enough. Again: ");
//		InquiryState inquiry2 = new InquiryState("So, anything else you want to talk about?", Lists.stringList(), "");
		GenerativeCommunicationState generative = new GenerativeCommunicationState();
		QuestionAnsweringState qa = new QuestionAnsweringState(generative);
		SegueState segue = new SegueState(qa);
		FarewellState farewell = new FarewellState();
		
		greetings.setSuccess(intro);
		greetings.setFailure(intro);
		intro.setSuccess(inquiry);
		intro.setFailure(inquiry);
		inquiry.setSuccess(segue);
		inquiry.setFailure(segue);
		generative.setSuccess(farewell);
		generative.setFailure(segue);
		
		state = greetings;
	}
	
	@Override
	public List<Action> answer(Interpretation input) {
		Reaction reaction = state.react(input);
		List<Action> talk = Lists.actionList();
		List<Interpretation> intentions = reaction.getReactions();
		for(Interpretation i: intentions){
			talk.add(verbalizer.verbalize(i));
		}
		state = reaction.getState();
		intentions = state.act();
		for(Interpretation i: intentions){
			talk.add(verbalizer.verbalize(i));
		}
		return talk;
	}

}
