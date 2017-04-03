package roboy.logic;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SEMANTIC_ROLE;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.linguistics.sentenceanalysis.OpenNLPParser;
import roboy.util.Relation;

public class PASInterpreterTest {
	
	private static final OpenNLPParser parser = new OpenNLPParser();
	
	@SuppressWarnings("unchecked")
	@Test
	public void testWhenWas() {
		Interpretation interpretation = new Interpretation("When was Putin born ?");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("born",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("Putin",pas.get(SEMANTIC_ROLE.PATIENT));
		assertEquals("When",pas.get(SEMANTIC_ROLE.TIME));
		
		Relation relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("Putin",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("birthDate",relation.predicate);
		assertNull(relation.object);
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
		
		Relation relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("Germany",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("areaCode",relation.predicate);
		assertNull(relation.object);
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
		
		Relation relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("Putin",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("birthPlace",relation.predicate);
		assertNull(relation.object);
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
		
		Relation relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("Elvis",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("deathPlace",relation.predicate);
		assertNull(relation.object);
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
		
		Relation relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("Elvis",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("deathDate",relation.predicate);
		assertNull(relation.object);
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
		
		Relation relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("Mount Everest",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("elevation",relation.predicate);
		assertNull(relation.object);
		
		interpretation = new Interpretation("How many people live in Berlin ?");
		interpretation = parser.analyze(interpretation);
		pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		assertEquals("live",pas.get(SEMANTIC_ROLE.PREDICATE));
		assertEquals("in Berlin",pas.get(SEMANTIC_ROLE.LOCATION));
		assertEquals("How many people", pas.get(SEMANTIC_ROLE.AGENT)); // bad parser
		
		relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("in Berlin",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("populationTotal",relation.predicate);
		assertNull(relation.object);
	}


	@SuppressWarnings("unchecked")
	@Test
	public void testWhatIsNewExamples() {
		Interpretation interpretation = new Interpretation("What is the capital of Germany");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		Relation relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("Germany",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("capital",relation.predicate);
		assertNull(relation.object);
		
		interpretation = new Interpretation("What is the official language of Germany");
		interpretation = parser.analyze(interpretation);
		pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("Germany",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("officialLanguage",relation.predicate);
		assertNull(relation.object);
		
		interpretation = new Interpretation("What is the national anthem of Germany");
		interpretation = parser.analyze(interpretation);
		pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("Germany",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("nationalAnthem",relation.predicate);
		assertNull(relation.object);
		
		interpretation = new Interpretation("What is the currency of Germany");
		interpretation = parser.analyze(interpretation);
		pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("Germany",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("currency",relation.predicate);
		assertNull(relation.object);
		
		interpretation = new Interpretation("What is the job of Putin");
		interpretation = parser.analyze(interpretation);
		pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("Putin",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("occupation",relation.predicate);
		assertNull(relation.object);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWhereDidNewExamples() {
		Interpretation interpretation = new Interpretation("Where did Putin study ?");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		Relation relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("Putin",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("almaMater",relation.predicate);
		assertNull(relation.object);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testWhoIsNewExamples() {
		Interpretation interpretation = new Interpretation("Who is the partner of Donald Trump");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		Relation relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("Donald Trump",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("spouse",relation.predicate);
		assertNull(relation.object);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testWhoLivesNewExamples() {
		Interpretation interpretation = new Interpretation("Who lives in Sweden");
		interpretation = parser.analyze(interpretation);
		Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		Relation relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("in Sweden",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("residence",relation.predicate);
		assertNull(relation.object);
		
		interpretation = new Interpretation("Who is living in Sweden");
		interpretation = parser.analyze(interpretation);
		pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
		relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("in Sweden",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("residence",relation.predicate);
		assertNull(relation.object);
	}
}
