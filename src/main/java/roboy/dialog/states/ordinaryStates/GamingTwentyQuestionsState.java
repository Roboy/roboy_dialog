package roboy.dialog.states.ordinaryStates;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.entities.Question;
import com.markozajc.akiwrapper.Akiwrapper.Answer;
import com.markozajc.akiwrapper.core.entities.Guess;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;

import java.util.ArrayList;
import java.util.List;

public class GamingTwentyQuestionsState extends State {



	private Akiwrapper aw = new AkiwrapperBuilder().setFilterProfanity(true).build();
	private Question nextQuestion = null;
	private List<Long> declined = new ArrayList<>();

	private static final double PROBABILITY_THRESHOLD = 0.85;

	private boolean userReady = false;
	private boolean guessesAvailable = false;
	private Guess currentGuess = null;

	public GamingTwentyQuestionsState(String stateIdentifier, StateParameters params) {
		super(stateIdentifier, params);
	}

	@Override
	public Output act() {

		System.out.println("--> act()");

		if(!userReady){
			return Output.say("Now think of a character and tell me when you're ready to start the game.");

		} else if(guessesAvailable) {

			try {
				return doAGuess();
			}
			catch (Exception e){
				return Output.say("I have a problem finding a guess...");
			}
		}
		else {

			return askNextQuestion();
		}


	}

	@Override
	public Output react(Interpretation input) {

		System.out.println("--> react()");

		String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
		String intent = getIntent (tokens);
		System.out.println("Intent: " + intent);

		if(!isUserReady(intent)){
			return Output.say("Nice, then let's start");

		} else if(guessesAvailable){

			return processUserGuessAnswer(intent);

		} else {

			try {

				return saveUsersEstimate(intent);
			}
			catch (Exception e){
				return Output.say("I have a problem processing your input...");
			}
		}
	}

	@Override
	public State getNextState() {
		return this;
	}


	//TODO: methods to implement
	/*
	- getToKnowUser
	- doTheGuess
	- finishGame
	- askNextQuestion
	 */

	private Output askNextQuestion(){
		System.out.println("--> askNextQuestion");
		nextQuestion = aw.getCurrentQuestion();
		return Output.say(nextQuestion.getQuestion());
	}

	private Output doAGuess () throws Exception{

		System.out.println("--> doAGuess");

		String roboyAnswer = "I have no guesses for you...";

		for(Guess guess : aw.getGuessesAboveProbability(PROBABILITY_THRESHOLD)) {
			if(!declined.contains(Long.valueOf(guess.getIdLong()))) {

				roboyAnswer = guess.getName();
				currentGuess = guess;
				break;
			}
		}

		return Output.say(roboyAnswer);
	}

	private Output processUserGuessAnswer(String intent){

		System.out.println("--> processUserGuessAnswer");

		if (intent.equals("yes")){
			//TODO: next state...
			guessesAvailable = false;
			return Output.say("I won.");
		} else {
			declined.add(Long.valueOf(currentGuess.getIdLong()));
			return Output.say("I have another guess in mind.");
		}
	}

	private Output saveUsersEstimate(String intent) throws Exception {

		System.out.println("--> saveUserEstimate");

		//check if guesses above threshold are available
		if(aw.getGuessesAboveProbability(PROBABILITY_THRESHOLD).size() > 0){
			guessesAvailable = true;
		}

		if (intent != null) {
			switch (intent) {
				case "yes":
					aw.answerCurrentQuestion(Answer.YES);
					return Output.sayNothing();
					//break;
				case "no":
					aw.answerCurrentQuestion(Answer.NO);
					return Output.sayNothing();
				//break;

				case "dont know":
					aw.answerCurrentQuestion(Answer.DONT_KNOW);
					return Output.sayNothing();
				//break;

				case "probably":
					aw.answerCurrentQuestion(Answer.PROBABLY);
					return Output.sayNothing();
				//break;

				case "probably not":
					aw.answerCurrentQuestion(Answer.PROBABLY_NOT);
					return Output.sayNothing();
				//break;

				case "back":
					aw.undoAnswer();
					return Output.sayNothing();
				//break;

				default:
					return Output.say("Please answer with either YES, NO, DONT KNOW, PROBABLY or PROBABLY NOT or go back one step with BACK.");
				//break;
			}
		}

		else {

			return Output.say("I didn't understand your answer correctly.");
		}
	}

	private String getIntent(String[] tokens) {

		System.out.println("--> getIntent");

		String intent = "";

		for(String token : tokens) {
			if(token.equals("yes")) {
				intent = "yes";
			} else if (token.equals("no")) {
				intent = "no";
			} else if(token.equals("ready")) {
				intent = "ready";
			} else if(token.equals("back")) {
				intent = "back";
			} else if(token.equals("don't")) {
				intent = "don't know";
			} else if(token.equals("not")) {
				intent = "probably now";
			} else if(token.equals("probably")) {
				intent = "probably";
			}
		}

		return intent;
	}

	private boolean isUserReady(String intent){

		System.out.println("--> isUserReady");

		if(intent.equals("ready")) {
			userReady = true;
		}

		return userReady;
	}
}
