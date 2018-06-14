package roboy.linguistics.sentenceanalysis;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import roboy.linguistics.Linguistics.SemanticRole;

public class OpenNLPParserTest {
	
	private static final OpenNLPParser parser = new OpenNLPParser();

	@SuppressWarnings("unchecked")
	@Test
	public void testWhatIs() {
		Interpretation interpretation = new Interpretation("What is the area code of Germany");
		interpretation = parser.analyze(interpretation);
		Map<SemanticRole, String> pas = interpretation.getPas();
		assertEquals("is", pas.get(SemanticRole.PREDICATE));
		assertEquals("the area code of Germany", pas.get(SemanticRole.PATIENT));
		assertEquals("What", pas.get(SemanticRole.AGENT));
	}
	
//	@SuppressWarnings("unchecked")
//	@Test
//	public void testDiscourseMarker() {
//		Interpretation interpretation = new Interpretation("Hey Roboy, what is the area code of Germany");
//		interpretation = parser.analyze(interpretation);
//		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
//		assertEquals("is",pas.get(SEMANTIC_ROLE.PREDICATE));
//		assertEquals("the area code of Germany",pas.get(SEMANTIC_ROLE.PATIENT));
//		assertEquals("what", pas.get(SEMANTIC_ROLE.AGENT));
//	}
	

	@SuppressWarnings("unchecked")
	@Test
	public void testWhenWas() {
		Interpretation interpretation = new Interpretation("When was Putin born ?");
		interpretation = parser.analyze(interpretation);
		Map<SemanticRole, String> pas = interpretation.getPas();
		assertEquals("born", pas.get(SemanticRole.PREDICATE));
		assertEquals("Putin", pas.get(SemanticRole.PATIENT));
		assertEquals("When", pas.get(SemanticRole.TIME));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWhereWas() {
		Interpretation interpretation = new Interpretation("Where was Putin born ?");
		interpretation = parser.analyze(interpretation);
		Map<SemanticRole, String> pas = interpretation.getPas();
		assertEquals("born", pas.get(SemanticRole.PREDICATE));
		assertEquals("Putin", pas.get(SemanticRole.PATIENT));
		assertEquals("Where", pas.get(SemanticRole.LOCATION));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWhereDid() {
		Interpretation interpretation = new Interpretation("Where did Elvis die ?");
		interpretation = parser.analyze(interpretation);
		Map<SemanticRole, String> pas = interpretation.getPas();
		assertEquals("die", pas.get(SemanticRole.PREDICATE));
		assertEquals("Elvis",pas.get(SemanticRole.AGENT));
		assertEquals("Where", pas.get(SemanticRole.LOCATION));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWhenDid() {
		Interpretation interpretation = new Interpretation("When did Elvis die ?");
		interpretation = parser.analyze(interpretation);
		Map<SemanticRole, String> pas = interpretation.getPas();
		assertEquals("die", pas.get(SemanticRole.PREDICATE));
		assertEquals("Elvis", pas.get(SemanticRole.AGENT));
		assertEquals("When", pas.get(SemanticRole.TIME));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testHowAdjective() {
		Interpretation interpretation = new Interpretation("How high is Mount Everest ?");
		interpretation = parser.analyze(interpretation);
		Map<SemanticRole, String> pas = interpretation.getPas();
		assertEquals("is", pas.get(SemanticRole.PREDICATE));
		assertEquals("Mount Everest", pas.get(SemanticRole.PATIENT));
		assertEquals("How", pas.get(SemanticRole.MANNER));
		assertEquals("high", pas.get(SemanticRole.AGENT)); // bad parser
		
		interpretation = new Interpretation("How many people live in Berlin ?");
		interpretation = parser.analyze(interpretation);
		pas = interpretation.getPas();
		assertEquals("live", pas.get(SemanticRole.PREDICATE));
		assertEquals("in Berlin", pas.get(SemanticRole.LOCATION));
		assertEquals("How many people", pas.get(SemanticRole.AGENT)); // bad parser
	}
}
