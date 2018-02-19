package roboy.dialog.personality.states;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import roboy.dialog.action.SpeechAction;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.linguistics.sentenceanalysis.OpenNLPParser;
import roboy.talk.Verbalizer;

public class QuestionAnsweringStateTest {

	private static final OpenNLPParser parser = new OpenNLPParser();
	private static final QuestionAnsweringState state = new QuestionAnsweringState(new IdleState());
	
	@Test
	public void test() {
		Interpretation interpretation = new Interpretation("What is the area code of Washington");
		interpretation = parser.analyze(interpretation);
		Reaction reaction = state.react(interpretation);
		assertEquals(1, reaction.getReactions().size());
		assertEquals("360",reaction.getReactions().get(0).getFeature("sentence"));
	}
	
	@Test
	public void testNotAnswerable() {
		Interpretation interpretation = new Interpretation("My name is Bob");
		interpretation = parser.analyze(interpretation);
		Reaction reaction = state.react(interpretation);
		assertEquals(1, reaction.getReactions().size());
		assertEquals("",reaction.getReactions().get(0).getFeature("sentence"));
	}
	
	@Test
	public void testWhenWas() {
		Interpretation interpretation = new Interpretation("When was Putin born ?");
		interpretation = parser.analyze(interpretation);
		Reaction reaction = state.react(interpretation);
		assertEquals(1, reaction.getReactions().size());
		assertEquals("1953-3-30",reaction.getReactions().get(0).getFeature("sentence"));
		Verbalizer verbalizer = new Verbalizer();
		SpeechAction action = (SpeechAction) verbalizer.verbalize(reaction.getReactions().get(0));
		assertEquals("March thirtieth nineteen hundred fifty three",action.getText());
	}

	@Test
	public void testWhereWas() {
		Interpretation interpretation = new Interpretation("Where was Putin born ?");
		interpretation = parser.analyze(interpretation);
		Reaction reaction = state.react(interpretation);
		assertEquals(1, reaction.getReactions().size());
		assertEquals("Russia",reaction.getReactions().get(0).getFeature("sentence"));
	}

	@Test
	@Ignore
	public void testWhereDid() {
		Interpretation interpretation = new Interpretation("Where did Elvis die ?");
		interpretation = parser.analyze(interpretation);
		Reaction reaction = state.react(interpretation);
		assertEquals(1, reaction.getReactions().size());
		assertEquals("Greenwich, Connecticut",reaction.getReactions().get(0).getFeature("sentence"));
	}

	@Test
	@Ignore
	public void testWhenDid() {
		Interpretation interpretation = new Interpretation("When did Elvis die ?");
		interpretation = parser.analyze(interpretation);
		Reaction reaction = state.react(interpretation);
		assertEquals(1, reaction.getReactions().size());
		assertEquals("1977-08-16",reaction.getReactions().get(0).getFeature("sentence"));
		Verbalizer verbalizer = new Verbalizer();
		SpeechAction action = (SpeechAction) verbalizer.verbalize(reaction.getReactions().get(0));
		assertEquals("August sixteenth nineteen hundred seventy seven",action.getText());
	}

	@Test
	@Ignore
	public void testHowAdjective() {
		Interpretation interpretation = new Interpretation("How high is Mount Everest ?");
		interpretation = parser.analyze(interpretation);
		Reaction reaction = state.react(interpretation);
		assertEquals(1, reaction.getReactions().size());
		assertEquals("8848.0",reaction.getReactions().get(0).getFeature("sentence"));
	}

}
