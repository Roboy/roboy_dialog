package roboy.linguistics.sentenceanalysis;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SEMANTIC_ROLE;

public class OpenNLPParserTest {
	
	private OpenNLPParser parser;
	
	@Before
	public void init(){
		parser = new OpenNLPParser();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWhatIs() {
		Interpretation interpretation = new Interpretation("What is the area code of Germany");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("is",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("the area code of Germany",pas.get(SEMANTIC_ROLE.PATIENT));
		assertEquals("What", pas.get(SEMANTIC_ROLE.AGENT));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testDiscourseMarker() {
		Interpretation interpretation = new Interpretation("Hey Roboy, what is the area code of Germany");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("is",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("the area code of Germany",pas.get(SEMANTIC_ROLE.PATIENT));
		assertEquals("what", pas.get(SEMANTIC_ROLE.AGENT));
	}
	

	@SuppressWarnings("unchecked")
	@Test
	public void testWhenWas() {
		Interpretation interpretation = new Interpretation("When was Putin born ?");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("born",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("Putin",pas.get(SEMANTIC_ROLE.PATIENT));
		assertEquals("When",pas.get(SEMANTIC_ROLE.TIME));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWhereWas() {
		Interpretation interpretation = new Interpretation("Where was Putin born ?");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("born",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("Putin",pas.get(SEMANTIC_ROLE.PATIENT));
		assertEquals("Where", pas.get(SEMANTIC_ROLE.LOCATION));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWhereDid() {
		Interpretation interpretation = new Interpretation("Where did Elvis die ?");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("die",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("Elvis",pas.get(SEMANTIC_ROLE.AGENT));
		assertEquals("Where", pas.get(SEMANTIC_ROLE.LOCATION));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWhenDid() {
		Interpretation interpretation = new Interpretation("When did Elvis die ?");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("die",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("Elvis",pas.get(SEMANTIC_ROLE.AGENT));
		assertEquals("When", pas.get(SEMANTIC_ROLE.TIME));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testHowAdjective() {
		Interpretation interpretation = new Interpretation("How high is Mount Everest ?");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("is",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("Mount Everest",pas.get(SEMANTIC_ROLE.PATIENT));
		assertEquals("How", pas.get(SEMANTIC_ROLE.MANNER));
		assertEquals("high", pas.get(SEMANTIC_ROLE.AGENT)); // bad parser
		
		interpretation = new Interpretation("How many people live in Berlin ?");
		interpretation = parser.analyze(interpretation);
		pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("live",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("in Berlin",pas.get(SEMANTIC_ROLE.LOCATION));
		assertEquals("How many people", pas.get(SEMANTIC_ROLE.AGENT)); // bad parser
	}
}
