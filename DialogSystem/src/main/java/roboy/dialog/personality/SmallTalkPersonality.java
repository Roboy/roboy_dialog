package roboy.dialog.personality;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import roboy.dialog.action.Action;
import roboy.dialog.personality.states.CelebrityState;
import roboy.dialog.personality.states.FarewellState;
import roboy.dialog.personality.states.GenerativeCommunicationState;
import roboy.dialog.personality.states.GreetingState;
import roboy.dialog.personality.states.InquiryState;
import roboy.dialog.personality.states.IntroductionState;
import roboy.dialog.personality.states.QuestionAnsweringState;
import roboy.dialog.personality.states.QuestionAskingState;
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

		List<String> nameQuestions = Arrays.asList(
			"How can I call you?",
			"What is your name?",
			"Who are you?");
		List<String> occupationQuestions = Arrays.asList(
			"What do you do?",
			"What is you your profession?");
		List<String> originQuestions = Arrays.asList(
			"Where are you from?",
			"Where do you live?",
			"Where were you born?",
			"I have hard time guessing your home country. What is it?",
			"Which town do you call your home?");
		List<String> hobbyQuestions = Arrays.asList(
			"What is your favorive thing to do?",
			"What is your hobby?",
			"What do you do in your free time?",
			"How do you spend your free time?",
			"What is your favorive pasttime activity?");
		List<String> moviesQuestions = Arrays.asList(
			"What is your favourite movie?",
			"What was the last movie you saw in the cinema?",
			"What is your favorite TV show?",
			"Which comedy do you like the most?");
		
		Map<String, List<String>> questions = new HashMap();
		questions.put("name", nameQuestions);
		questions.put("origin", originQuestions);
		questions.put("hobby", hobbyQuestions);
		questions.put("movies", moviesQuestions);
		questions.put("occupation", occupationQuestions);
		
		InquiryState inquiry = new InquiryState("How are you?", positive, "That's not good enough. Again: ");
//		InquiryState inquiry2 = new InquiryState("So, anything else you want to talk about?", Lists.stringList(), "");
		GenerativeCommunicationState generative = new GenerativeCommunicationState();
		QuestionAnsweringState answer = new QuestionAnsweringState(generative);
		SegueState segue = new SegueState(answer);
		CelebrityState celeb = new CelebrityState(segue);
		QuestionAskingState ask = new QuestionAskingState(answer, questions);
		FarewellState farewell = new FarewellState();
		
		ask.setTop(celeb);
		answer.setTop(celeb);
		segue.setTop(celeb);
		
		greetings.setNextState(intro);
		intro.setNextState(ask);
		ask.setSuccess(answer);
		ask.setFailure(ask);
		inquiry.setNextState(celeb);
		generative.setSuccess(farewell);
		generative.setFailure(celeb);

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
