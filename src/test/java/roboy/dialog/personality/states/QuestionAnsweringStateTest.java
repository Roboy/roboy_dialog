package roboy.dialog.personality.states;

import static org.junit.Assert.*;

import org.junit.Test;

import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.linguistics.sentenceanalysis.OpenNLPParser;

public class QuestionAnsweringStateTest {

	private static final OpenNLPParser parser = new OpenNLPParser();
	private static final QuestionAnsweringState state = new QuestionAnsweringState(new IdleState());
	
	@Test
	public void test() {
		Interpretation interpretation = new Interpretation("What is the area code of Germany");
		interpretation = parser.analyze(interpretation);
		Reaction reaction = state.react(interpretation);
		assertEquals(1, reaction.getReactions().size());
		assertEquals("06241,",reaction.getReactions().get(0).getFeature("sentence"));
	}
	
	@Test
	public void testWhenWas() {
		Interpretation interpretation = new Interpretation("When was Putin born ?");
		interpretation = parser.analyze(interpretation);
		Reaction reaction = state.react(interpretation);
		assertEquals(1, reaction.getReactions().size());
		assertEquals("1953-03-30^^http://www.w3.org/2001/XMLSchema#date",reaction.getReactions().get(0).getFeature("sentence"));
	}

	@Test
	public void testWhereWas() {
		Interpretation interpretation = new Interpretation("Where was Putin born ?");
		interpretation = parser.analyze(interpretation);
		Reaction reaction = state.react(interpretation);
		assertEquals(1, reaction.getReactions().size());
		assertEquals("Ryazan",reaction.getReactions().get(0).getFeature("sentence"));
	}

	@Test
	public void testWhereDid() {
		Interpretation interpretation = new Interpretation("Where did Elvis die ?");
		interpretation = parser.analyze(interpretation);
		Reaction reaction = state.react(interpretation);
		assertEquals(1, reaction.getReactions().size());
		assertEquals("Memphis, Tennessee",reaction.getReactions().get(0).getFeature("sentence"));
	}

	@Test
	public void testWhenDid() {
		Interpretation interpretation = new Interpretation("When did Elvis die ?");
		interpretation = parser.analyze(interpretation);
		Reaction reaction = state.react(interpretation);
		assertEquals(1, reaction.getReactions().size());
		assertEquals("1977-08-16^^http://www.w3.org/2001/XMLSchema#date",reaction.getReactions().get(0).getFeature("sentence"));
	}

	@Test
	public void testHowAdjective() {
		Interpretation interpretation = new Interpretation("How high is Mount Everest ?");
		interpretation = parser.analyze(interpretation);
		Reaction reaction = state.react(interpretation);
		assertEquals(1, reaction.getReactions().size());
		assertEquals("8848.0^^http://www.w3.org/2001/XMLSchema#double",reaction.getReactions().get(0).getFeature("sentence"));
	}

}
