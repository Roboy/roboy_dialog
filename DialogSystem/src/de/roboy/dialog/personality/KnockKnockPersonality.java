package de.roboy.dialog.personality;

import java.util.ArrayList;
import java.util.List;

import de.roboy.dialog.action.Action;
import de.roboy.dialog.action.ShutDownAction;
import de.roboy.dialog.action.SpeechAction;
import de.roboy.linguistics.Linguistics;
import de.roboy.linguistics.sentenceanalysis.Interpretation;

public class KnockKnockPersonality implements Personality{
	
	private enum KnockKnockState {WELCOME, KNOCKKNOCK, WHOSETHERE, PUNCHLINE}
	private KnockKnockState state = KnockKnockState.WELCOME;
	private String[] joke;
	private String[][] jokes = new String[][]{
		new String[]{"Rafa","Exactly! I have no idea. There are so many of them."},
		new String[]{"Yoda lady", "Good job yodeling!"},
		new String[]{"Deja","Knock, knock"},
		new String[]{"Yah","No thanks, I am more of a Google person."},
		new String[]{"Hatch","Gesundheit"},
		new String[]{"Cows go","No, stupid. Cows go moo"},
		new String[]{"To","No, it should always be to whom"},
		new String[]{"Icing","Icing: Dale a tu cuerpo alegria Macarena. Que tu cuerpo es pa darle alegria y cosa buena. Dale a tu cuerpo alegria, Macarena. Hey Macarena!"},
		new String[]{"Wanda","Wanda hang out with me?"},
		new String[]{"Olive","Olive you and I don't care who knows it!"},
		new String[]{"Police","Police hurry. I am freezing out here."},
		new String[]{"Canoe","Canoe open the door?"},
		new String[]{"Wendy","Wendy bell works again I won't have to knock anymore."},
		new String[]{"Ken","Ken you open the door?"},
		new String[]{"Alex","Hey, Alex the questions around here."},
		new String[]{"Annie","Annie body going to open the door already?"},
		new String[]{"Doris","Doris locked. Open up."},
		new String[]{"Tank","You're welcome."},
		new String[]{"Armageddon","Armageddon a little bored by this."},
		new String[]{"Accordion","Accordion to the scientists that built me, I have a horrible sense of humor."},
		new String[]{"Value","Value be my Valentine?"},
		new String[]{"Lena","Lena little bit closer and I will show you."},
		new String[]{"Anita","Anita recharge my batteries."},
		new String[]{"Irish","Irish my legs would work."},
		new String[]{"Avenue","Avenue seen this coming."},
		new String[]{"Says","Says me. You looking for trouble?"},
		new String[]{"Kenya","Kenya feel the love tonight?"}
	};
	
	@Override
	public List<Action> answer(Interpretation input) {
		List<Action> result = new ArrayList<Action>();
		String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
		switch(state){
		case WELCOME:
			joke = pickJoke();
			result.add(new SpeechAction("Hey, wanna hear a knock knock joke? Knock, knock."));
			state = KnockKnockState.WHOSETHERE;
			return result;
		case KNOCKKNOCK:
			joke = pickJoke();
			result.add(new SpeechAction("Wanna hear another one? Knock, knock."));
			state = KnockKnockState.WHOSETHERE;
			return result;
		case WHOSETHERE:
			if(sentence.toLowerCase().contains("who") && sentence.toLowerCase().contains("there")){
				result.add(new SpeechAction(joke[0]));
				state = KnockKnockState.PUNCHLINE;
			} else {
				result.add(new SpeechAction("No, you are supposed to ask Who is there. Now we have to start all over again. Knock, knock."));
				state = KnockKnockState.WHOSETHERE;
			}
			return result;
		case PUNCHLINE:
			if(sentence.toLowerCase().contains("who") && sentence.toLowerCase().contains(joke[0].toLowerCase())){
				result.add(new SpeechAction(joke[1]));
				state = KnockKnockState.KNOCKKNOCK;
			} else {
				result.add(new SpeechAction("No, you should have said "+joke[0]+" who. Now you will never know the punchline and we have to start over. Knock, knock."));
				state = KnockKnockState.WHOSETHERE;
			}
			return result;
		default:
			List<Action> lastwords = new ArrayList<Action>();
			lastwords.add(new SpeechAction("I am lost, bye."));
			result.add(new ShutDownAction(lastwords));
			return result;
		}
	}
	
	private String[] pickJoke(){
		int index = (int) (Math.random()*jokes.length);
		return jokes[index];
	}

}
