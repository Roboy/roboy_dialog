package roboy.logic;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SEMANTIC_ROLE;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.linguistics.sentenceanalysis.OpenNLPParser;
import roboy.util.Relation;

public class PASInterpreterTest {
	
	private OpenNLPParser parser;
	
	@Before
	public void init(){
		parser = new OpenNLPParser();
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
		
		Relation relation = PASInterpreter.pas2DBpediaRelation(pas);
		assertNotNull(relation);
		assertEquals("Putin",relation.subject.getAttribute(Linguistics.NAME));
		assertEquals("birthDate",relation.predicate);
		assertNull(relation.object);
	}

}
