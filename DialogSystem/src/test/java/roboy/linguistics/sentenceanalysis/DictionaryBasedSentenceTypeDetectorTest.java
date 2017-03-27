package roboy.linguistics.sentenceanalysis;

import static org.junit.Assert.*;

import org.junit.Test;

import roboy.linguistics.Linguistics.SENTENCE_TYPE;

public class DictionaryBasedSentenceTypeDetectorTest {
	
	private DictionaryBasedSentenceTypeDetector detector = new DictionaryBasedSentenceTypeDetector();
	
	@Test
	public void testWhatIs() {
		Interpretation interpretation = new Interpretation("What is area code of Germany?");
		interpretation = detector.analyze(interpretation);
		assertTrue(interpretation.getSentenceType()==SENTENCE_TYPE.WHAT);
		interpretation = new Interpretation("Hey Roboy, what is area code of Germany?");
		interpretation = detector.analyze(interpretation);
		assertTrue(interpretation.getSentenceType()==SENTENCE_TYPE.WHAT);
	}

	
}
