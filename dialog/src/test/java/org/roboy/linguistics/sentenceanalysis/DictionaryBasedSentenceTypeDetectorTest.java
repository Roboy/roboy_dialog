package org.roboy.linguistics.sentenceanalysis;

import static org.junit.Assert.*;

import org.junit.Test;

import roboy.linguistics.Linguistics.SentenceType;

public class DictionaryBasedSentenceTypeDetectorTest {
	
	private DictionaryBasedSentenceTypeDetector detector = new DictionaryBasedSentenceTypeDetector();
	private SimpleTokenizer tokenizer = new SimpleTokenizer();
	
	@Test
	public void testWhatIs() {
		Interpretation interpretation = new Interpretation("What is area code of Germany?");
		interpretation = tokenizer.analyze(interpretation);
		interpretation = detector.analyze(interpretation);
		assertTrue(interpretation.getSentenceType() == SentenceType.WHAT);
		
//		interpretation = new Interpretation("Hey RoboyModel, what is area code of Germany?");
//		interpretation = tokenizer.analyze(interpretation);
//		interpretation = detector.analyze(interpretation);
//		assertTrue(interpretation.getSentenceType()==SENTENCE_TYPE.WHAT);
	}

	
}
