package de.roboy.talk;

import java.util.Arrays;
import java.util.List;

import de.roboy.dialog.action.SpeechAction;
import de.roboy.linguistics.Linguistics;
import de.roboy.linguistics.sentenceanalysis.Interpretation;

public class Verbalizer {
	
	public SpeechAction verbalize(Interpretation interpretation){
		switch(interpretation.getSentenceType()){
		case GREETING: return greet(interpretation);
		case FAREWELL: return farewell(interpretation);
		default:       return literalSentence(interpretation);
		}
	}
	
	public static final List<String> greetings = 
			Arrays.asList("hello","hi","greetings","good morning","howdy","good day","hey");
	private SpeechAction greet(Interpretation interpretation){
		return new SpeechAction(StatementBuilder.random(greetings));
	}
	
	private static final List<String> farewells = 
			Arrays.asList("ciao","goodbye","cheerio","bye","see you",
					"farewell","bye-bye");
	private SpeechAction farewell(Interpretation interpretation){
		return new SpeechAction(StatementBuilder.random(farewells));
	}
	
	private SpeechAction literalSentence(Interpretation interpretation){
		return new SpeechAction((String)interpretation.getFeatures().get(Linguistics.SENTENCE));
	}

}
