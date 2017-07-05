package roboy.talk;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import roboy.dialog.action.Action;
import roboy.dialog.action.ShutDownAction;
import roboy.dialog.action.SpeechAction;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Maps;

/**
 * Turns interpretations to actual utterances. This should in the future lead to diversify
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
	
	private Interpretation verbalizeDates(Interpretation interpretation){
		StringBuilder sb = new StringBuilder();
		String sentence = (String)interpretation.getFeatures().get(Linguistics.SENTENCE);
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
			interpretation.getFeatures().put(Linguistics.SENTENCE, sb.toString());
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
			"12","twelveth",
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
			"30","thirtiest",
			"31","thiry first"
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
			4,"fourty",
			5,"fifty",
			6,"sixty",
			7,"seventy",
			8,"eighty",
			9,"ninety"
			);
	
	private SpeechAction literalSentence(Interpretation interpretation){
		return new SpeechAction((String)interpretation.getFeatures().get(Linguistics.SENTENCE));
	}

}
