package roboy.linguistics.sentenceanalysis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import roboy.linguistics.Linguistics;

public class AnswerAnalyzerTest {

	@Test
	public void testName() {
		assertEquals("frank", analyze("Frank"));
		assertEquals("carl", analyze("I am carl"));
		assertEquals("horst", analyze("My name is Horst"));
		assertEquals("bill gates", analyze("I am Bill Gates"));
		assertEquals("jack", analyze("My friends call me Jack"));
		assertEquals("bill", analyze("You can call me Bill"));
//		assertEquals("bob", analyze("Call me Bob")); // Doesn't work due to POS tagger error
	}
	
	@Test
	public void testOccupation() {
		assertEquals("nothing really", analyzePred("nothing really"));
		assertEquals("a math student", analyzePred("I am a math student"));
		assertEquals("a professor", analyzePred("I am a professor"));
		assertEquals("studying robotics", analyzePred("I am studying robotics"));
		assertEquals("study biology", analyzePred("I study Biology"));
	}
	
	@Test
	public void testOrigin() {
		assertEquals("netherlands", analyze("Netherlands"));
		assertEquals("india", analyze("I am from India"));
		assertEquals("germany", analyze("I live in Germany"));
		assertEquals("sweden", analyze("My home country is Sweden"));
		assertEquals("garching", analyze("My home town is Garching"));
		assertEquals("austria", analyze("I was born in Austria"));
	}
	
	@Test
	public void testHobby() {
		assertEquals("robots", analyzePred("Robots"));
		assertEquals("football", analyzePred("My hobby is football"));
		assertEquals("fishing and swimming", analyzePred("My hobbies are fishing and swimming"));
		assertEquals("read a lot", analyzePred("I read a lot"));
		assertEquals("playing cards", analyzePred("I enjoy playing cards"));
		assertEquals("program", analyzePred("I like to program"));
		assertEquals("playing computer games", analyzePred("My favorite thing to do is playing computer games"));
//		assertEquals("basketball", analyzePred("I guess I like basketball")); // Doesn't yet work due to nesting
	}
	
	@Test
	public void testMovie() {
		assertEquals("matrix", analyze("I love matrix"));
		assertEquals("forest gump", analyze("I enjoyed forest gump"));
		assertEquals("wheel of fortune", analyze("I always watch wheel of fortune"));
		assertEquals("anything from quentin tarantino", analyze("anything from quentin tarantino"));
		assertEquals("psycho", analyze("psycho"));
//		assertEquals("gone with the wind", analyze("My favorite movie is gone with the wind")); // Doesn't work due to verb in title
	}

	private static final SimpleTokenizer tokenizer = new SimpleTokenizer();
	private static final OpenNLPPPOSTagger pos = new OpenNLPPPOSTagger();
	private static final OpenNLPParser parser = new OpenNLPParser();
	private static final AnswerAnalyzer answer = new AnswerAnalyzer();
	
	private String analyze(String sentence){
		List<Analyzer> analyzers = new ArrayList<Analyzer>();
		analyzers.add(tokenizer);
		analyzers.add(pos);
		analyzers.add(parser);
		analyzers.add(answer);
        Interpretation interpretation = new Interpretation(sentence);
        for (Analyzer a : analyzers) interpretation = a.analyze(interpretation);
        return interpretation.getObjAnswer();
	}
	
	private String analyzePred(String sentence){
		List<Analyzer> analyzers = new ArrayList<Analyzer>();
		analyzers.add(tokenizer);
		analyzers.add(pos);
		analyzers.add(parser);
		analyzers.add(answer);
        Interpretation interpretation = new Interpretation(sentence);
        for (Analyzer a : analyzers) interpretation = a.analyze(interpretation);
        return interpretation.getPredAnswer();
	}
	
}
