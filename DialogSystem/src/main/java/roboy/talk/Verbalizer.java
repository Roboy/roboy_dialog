package roboy.talk;

import java.util.Arrays;
import java.util.List;

import roboy.dialog.action.Action;
import roboy.dialog.action.ShutDownAction;
import roboy.dialog.action.SpeechAction;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;

public class Verbalizer {
	
	public Action verbalize(Interpretation interpretation){
		switch(interpretation.getSentenceType()){
		case GREETING: return greet(interpretation);
		case FAREWELL: return farewell(interpretation);
		case SEGUE:    return segue(interpretation);
		case ANECDOTE: return anecdote(interpretation);
		default:       return literalSentence(interpretation);
		}
	}
	
	public static final List<String> greetings = 
			Arrays.asList("hello","hi","greetings","good morning","howdy","good day","hey");
	
	private SpeechAction greet(Interpretation interpretation){
		return new SpeechAction(StatementBuilder.random(greetings));
	}
	
	public static final List<String> farewells =
			Arrays.asList("ciao","goodbye","cheerio","bye","see you",
					"farewell","bye-bye");
	
	private ShutDownAction farewell(Interpretation interpretation){
		return new ShutDownAction(Arrays.<Action>asList(new SpeechAction(StatementBuilder.random(farewells))));
	}
	
	private static final List<String> segues = 
			Arrays.asList("talking about ","since you mentioned ","on the topic of ");
	
	private SpeechAction segue(Interpretation interpretation){
		return new SpeechAction(StatementBuilder.random(segues)
				+ interpretation.getFeatures().get(Linguistics.ASSOCIATION));
	}
	
	
	private static final List<String> preAnecdotes = 
			Arrays.asList("here is an interesting bit of trivia. ", "how about this? ");
	private static final List<String> anecdotes = 
			Arrays.asList("did you know ","did you know that ","i read that ",
					"i heard that ", "have you heard this: ");
	
	private SpeechAction anecdote(Interpretation interpretation){
		String prefix = Math.random()<0.3 ? StatementBuilder.random(preAnecdotes) : "";
		return new SpeechAction(prefix+StatementBuilder.random(anecdotes)
				+ interpretation.getFeatures().get(Linguistics.SENTENCE));
	}
	
	private SpeechAction literalSentence(Interpretation interpretation){
		return new SpeechAction((String)interpretation.getFeatures().get(Linguistics.SENTENCE));
	}

}
