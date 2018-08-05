package roboy.talk;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Maps;
import roboy.util.RandomList;

/**
 * Turns interpretations to actual utterances. This should in the future lead to diversifying
 * the ways Roboy is expressing information.
 */
public class Verbalizer {
	
	/**
	 * Currently contains utterance diversification for greetings, farewells, segue and 
	 * introductions to anecdotes. In all other cases the state machine provides a literal
	 * sentence that is just passed through. In the future, this should be extended to 
	 * diversify everything Roboy says.
	 * 
	 * @param interpretation the abstraction of what Roboy intends to say
	 * @return the actual action that is performed
	 */
	public Action verbalize(Interpretation interpretation){
		interpretation = verbalizeDates(interpretation);
		switch(interpretation.getSentenceType()){
		case GREETING: return greet(interpretation);
		case SEGUE:    return segue(interpretation);
		case ANECDOTE: return anecdote(interpretation);
		default:       return literalSentence(interpretation);
		}
	}

	// possible names for Roboy as parsed by Bing speech recognition
	public static final RandomList<String> roboyNames =
			new RandomList<>("roboi", "robot", "boy", "roboboy", "robot", "roboy");

	public static final RandomList<String> consent =
            new RandomList<>("yes", "I do", "sure", "of course", " go ahead");

	public static final RandomList<String> denial =
            new RandomList<>("no", "nope", "later", "other time", "not");

	// triggers that will start the conversation
	public static final RandomList<String> triggers =
            new RandomList<>("talk", "fun", "conversation", "new", "chat");

	public static final RandomList<String> greetings =
			new RandomList<>("hello","hi","greetings",
                    // "good morning", // not for the final demo ;)
                    "howdy",
                    // "good day", // not for the final demo ;)
                    "hey", "good evening",
					"what's up", "greeting to everyone here","hi there people",
					"hello world","gruse gott","wazup wazup wazup","howdy humans",
                    // "good day ladies and gentlemen",  // not for the final demo ;)
                    "good evening ladies and gentlemen",
                    "hey hey hey you there");
	
	private SpeechAction greet(Interpretation interpretation){
		return new SpeechAction(StatementBuilder.random(greetings));
	}
	
	public static final RandomList<String> farewells = new RandomList<>(
			"ciao", "goodbye", "cheerio", "bye",
            "see you", "farewell", "bye-bye");
	
	private static final RandomList<String> segues =
            new RandomList<>("talking about ","since you mentioned ","on the topic of ");
	
	private SpeechAction segue(Interpretation interpretation){
		return new SpeechAction(StatementBuilder.random(segues)
				+ interpretation.getAssociation());
	}

	private static final RandomList<String> preAnecdotes =
            new RandomList<>("here is an interesting bit of trivia. ", "how about this? ");
	private static final RandomList<String> anecdotes =
            new RandomList<>("did you know ","did you know that ","i read that ",
					"i heard that ", "have you heard this: ");
	
	private SpeechAction anecdote(Interpretation interpretation){
		String prefix = Math.random()<0.3 ? StatementBuilder.random(preAnecdotes) : "";
		return new SpeechAction(prefix+StatementBuilder.random(anecdotes)
				+ interpretation.getSentence());
	}

	public static final RandomList<String> startSomething =
			new RandomList<>("Let's go. ", "Can't wait to start. ", "Nice, let's start. ", "Yeah, let's go. "
			);

	public static final RandomList<String> userIsSure =
			new RandomList<>("You seem to be pretty sure about that. ", "You said yes. ", "I heard a yes from you. ", "A clear yes. "
			);

	public static final RandomList<String> userProbablyYes =
			new RandomList<>("The chance is quite high. ", "Might be true. ", "You're not sure but probably yes. "
			);

	public static final RandomList<String> userIsUncertain =
			new RandomList<>("Maybe you have never thought of that before. ", "Maybe, maybe not. ", "You don't know, no problem. ", "I don't know either. "
			);

	public static final RandomList<String> userProbablyNo =
			new RandomList<>("The chance is quite low. ", "Might be false. ", "You're not sure but probably no. ", "Probably not. "
			);

	public static final RandomList<String> userSaysNo =
			new RandomList<>("Not at all. ", "A clear no. ", "That was clear. No. ", "A no from you. ", "Nope. "
			);

	public static final RandomList<String> roboyNotUnderstand =
			new RandomList<>("Oh no, I didn't get what you said. ", "I didn't understand you correctly. ", "Sorry? What did you say? "
			);

	public static final RandomList<String> rosDisconnect =
			new RandomList<>("Oh no, where is my ROS connection? I need it. ", "I was looking for my ROS master everywhere but I can find it. ", "I think I have no ROS connection. ", "Hello? Hello? Any ROS master out there? Hmm, I can't hear anybody. "
			);

	private Interpretation verbalizeDates(Interpretation interpretation){
		StringBuilder sb = new StringBuilder();
		String sentence = interpretation.getSentence();
		if(sentence == null) return interpretation;
		Matcher matcher = Pattern.compile( "\\d\\d\\d\\d-\\d\\d?-\\d\\d" ).matcher( sentence );
		int lastEnd = 0;
		while ( matcher.find() ){
			sb.append(sentence.substring(lastEnd, matcher.start()));
			sb.append(dateToText(matcher.group()));
			lastEnd = matcher.end();
		}
		if(lastEnd>0){
			sb.append(sentence.substring(lastEnd,sentence.length()));
			interpretation.setSentence(sb.toString());
		}
		return interpretation;
	}
	
	private String dateToText(String date){
		// 1977-08-16
		StringBuilder sb = new StringBuilder();
		String[] parts = date.split("-");
		sb.append(monthNumberMap.get(parts[1]));
		sb.append(" ");
		sb.append(dayNumberMap.get(parts[2]));
		sb.append(" ");
		int thousands = Integer.parseInt(parts[0].substring(0,1));
		int hundreds = Integer.parseInt(parts[0].substring(1, 2));
		int tens = Integer.parseInt(parts[0].substring(2, 3));
		int ones = Integer.parseInt(parts[0].substring(3, 4));
		if(thousands>1 || hundreds==0){
			sb.append(lowNumberMap.get(thousands));
			sb.append(" thousand ");
			if(hundreds>0){
				sb.append(lowNumberMap.get(hundreds));
				sb.append(" hundred ");
			}
		} else {
			sb.append(lowNumberMap.get(thousands*10+hundreds));
			sb.append(" hundred ");
		}
		if(tens>0){
			sb.append(tenthNumberMap.get(tens));
			sb.append(" ");
		}
		if(ones>0){
			sb.append(lowNumberMap.get(ones));
		}
		return sb.toString().trim();
	}
	
	private static final Map<String,String> dayNumberMap = Maps.stringMap(
			"01","first",
			"02","second",
			"03","third",
			"04","fourth",
			"05","fifth",
			"06","sixth",
			"07","seventh",
			"08","eighth",
			"09","ninth",
			"10","tenth",
			"11","eleventh",
			"12","twelfth",
			"13","thirteenth",
			"14","fourteenth",
			"16","sixteenth",
			"17","seventeenth",
			"18","eighteenth",
			"19","nineteenth",
			"20","twentieth",
			"21","twenty first",
			"22","twenty second",
			"23","twenty third",
			"24","twenty fourth",
			"25","twenty fifth",
			"26","twenty sixth",
			"27","twenty seventh",
			"28","twenty eighth",
			"29","twenty ninth",
			"30","thirtieth",
			"31","thirty first"
			);
	
	private static final Map<Integer,String> lowNumberMap = Maps.intStringMap(
			1,"one",
			2,"two",
			3,"three",
			4,"four",
			5,"five",
			6,"six",
			7,"seven",
			8,"eight",
			9,"nine",
			10,"ten",
			11,"eleven",
			12,"twelve",
			13,"thirteen",
			14,"fourteen",
			15,"fifteen",
			16,"sixteen",
			17,"seventeen",
			18,"eighteen",
			19,"nineteen"
			);
	
	private static final Map<String,String> monthNumberMap = Maps.stringMap(
			"1","January",
			"2","February",
			"3","March",
			"4","April",
			"5","May",
			"6","June",
			"7","July",
			"8","August",
			"9","September",
			"01","January",
			"02","February",
			"03","March",
			"04","April",
			"05","May",
			"06","June",
			"07","July",
			"08","August",
			"09","September",
			"10","October",
			"11","November",
			"12","December"
			);
	
	private static final Map<Integer,String> tenthNumberMap = Maps.intStringMap(
			1,"ten",
			2,"twenty",
			3,"thirty",
			4,"forty",
			5,"fifty",
			6,"sixty",
			7,"seventy",
			8,"eighty",
			9,"ninety"
			);
	
	private SpeechAction literalSentence(Interpretation interpretation){
		return new SpeechAction(interpretation.getSentence());
	}

}
