package roboy.linguistics.sentenceanalysis;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SEMANTIC_ROLE;
import roboy.linguistics.Linguistics.SENTENCE_TYPE;

public class OpenNLPParserTest {
	
	private OpenNLPParser parser = new OpenNLPParser();

	@SuppressWarnings("unchecked")
	@Test
	public void testWhatIs() {
		Interpretation interpretation = new Interpretation("What is area code of Germany");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("areaCode",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("Germany",pas.get(SEMANTIC_ROLE.PATIENT));
//		interpretation = new Interpretation("Hey Roboy, what is area code of Germany?");
//		interpretation = parser.analyze(interpretation);
//		assertTrue(interpretation.getSentenceType()==SENTENCE_TYPE.WHAT);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWhenWas() {
		Interpretation interpretation = new Interpretation("When was Putin born ?");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("born",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("Putin",pas.get(SEMANTIC_ROLE.PATIENT));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWhereWas() {
		Interpretation interpretation = new Interpretation("Where was Putin born ?");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("born",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("Putin",pas.get(SEMANTIC_ROLE.PATIENT));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWhereDid() {
		Interpretation interpretation = new Interpretation("Where did Elvis die ?");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("die",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("Elvis",pas.get(SEMANTIC_ROLE.AGENT));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWhenDid() {
		Interpretation interpretation = new Interpretation("When did Elvis die ?");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("die",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("Elvis",pas.get(SEMANTIC_ROLE.AGENT));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testHowAdjective() {
		Interpretation interpretation = new Interpretation("How high is Mount Everest ?");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
//		assertEquals("high",pas.get(SEMANTIC_ROLE.PREDICATE));
//		assertEquals("Mount Everest",pas.get(SEMANTIC_ROLE.PATIENT));
		interpretation = new Interpretation("How many people live in Berlin ?");
		interpretation = parser.analyze(interpretation);
		pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("live",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("in Berlin",pas.get(SEMANTIC_ROLE.LOCATION));
	}
}
