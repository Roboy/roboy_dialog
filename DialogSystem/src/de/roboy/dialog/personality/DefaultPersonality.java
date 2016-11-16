package de.roboy.dialog.personality;

import java.util.Arrays;
import java.util.List;

import de.roboy.dialog.action.Action;
import de.roboy.dialog.action.ShutDownAction;
import de.roboy.dialog.action.SpeechAction;
import de.roboy.linguistics.sentenceanalysis.Sentence;

public class DefaultPersonality implements Personality{

	private static final List<String> greetings =
			Arrays.asList("hello","hi","greetings","good morning",
					"howdy","good day","hey");
	private static final List<String> farewells = 
			Arrays.asList("ciao","goodbye","cheerio","bye","see you",
					"farewell","bye-bye");
	private static final List<String> positive = 
			Arrays.asList("enthusiastic","awesome","great","very good",
					"dope","smashing","happy","cheerful","good","phantastic");
	private static final List<String> agreement = 
			Arrays.asList("yes","yeah","indeed","i am");
	private static final List<String> disagreement = 
			Arrays.asList("no","never","not sure");
	private static final List<String> introduction =
			Arrays.asList("i am","i'm","my name is","call me");
	
	public enum CONVERSATIONAL_STATE {GREETING, INTRODUCTION, SMALL_TALK, FAREWELL}
	
	private CONVERSATIONAL_STATE state = CONVERSATIONAL_STATE.GREETING;
	
	@Override
	public List<Action> answer(Sentence inputSentence) {
		String input = inputSentence.sentence;
		switch (state) {
		case GREETING:
			if(checkForTerm(input, greetings)){
				state = CONVERSATIONAL_STATE.INTRODUCTION;
				return Arrays.asList(new SpeechAction(random(greetings)+"! who are you?"));
			} else {
				return Arrays.asList(new SpeechAction("..."));
			}
		case INTRODUCTION:
			input = stripFromFront(input, introduction);
			if(input.split(" ").length>2){
				return Arrays.asList(new SpeechAction("\""+input+"\" is a really stupid name. pick another."));
			} else {
				state = CONVERSATIONAL_STATE.SMALL_TALK;
				return Arrays.asList(new SpeechAction("Nice to meet you "+input+". How are you today?"));
			}
		case SMALL_TALK:
			if(checkForTerm(input,farewells)){
				state = CONVERSATIONAL_STATE.FAREWELL;
				return Arrays.asList(new SpeechAction("Oh, you wanna leave already? Are you sure?"));
			} else if(checkForTerm(input,positive)){
				return Arrays.asList(new SpeechAction("Awesome! "+input+" is the best! i feel "+input+", too. how were you again?"));
			} else {
				String feeling = random(positive);
				return Arrays.asList(new SpeechAction(input+" isn't good enough. you should feel "+feeling+". i feel "+feeling+". so how do you feel now?"));
			}
		case FAREWELL:
			if(checkForTerm(input,agreement)){
				return Arrays.asList(new ShutDownAction(Arrays.asList(new SpeechAction("ok. "+random(farewells)+"!"))));
			} else if(checkForTerm(input, disagreement)){
				state = CONVERSATIONAL_STATE.SMALL_TALK;
				return Arrays.asList(new SpeechAction("great then we can talk more about how you feel!"));
			} else{
				state = CONVERSATIONAL_STATE.SMALL_TALK;
				return Arrays.asList(new SpeechAction("i will take that for a no. so how were you again?"));
			}
		}
		
		return Arrays.asList(new SpeechAction("i am wasted!! wheee!!"));
	}
	
	private String stripFromFront(String input, List<String> list){
		for(String l: list){
			if(input.toLowerCase().startsWith(l)) return input.substring(l.length()).trim();
		}
		return input;
	}
	
	private String random(List<String> list){
		return list.get((int)(Math.random()*list.size()));
	}
	
	private boolean checkForTerm(String input, List<String> list){
		for(String l: list){
			if(input.toLowerCase().contains(l)) return true;
		}
		return false;
	}

}
