package roboy.dialog.states.ordinaryStates;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.entities.Question;
import com.markozajc.akiwrapper.Akiwrapper.Answer;
import com.markozajc.akiwrapper.core.entities.Guess;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.IntentAnalyzer;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.ros.RosMainNode;

import java.util.ArrayList;
import java.util.List;

public class GamingTwentyQuestionsState extends State {


	private static final double PROBABILITY_THRESHOLD = 0.85;
	private final static String TRANSITION_GAME_ENDED = "gameEnded";

	private Akiwrapper aw = new AkiwrapperBuilder().setFilterProfanity(true).build();
	private Question nextQuestion = null;
	private Guess currentGuess = null;

	private List<Long> declined = new ArrayList<>();
	private boolean userReady = false;
	private boolean guessesAvailable = false;
	private boolean gameFinished = false;
	private int numberGuesses = 0;

	private boolean filterApplied = false;
	private String winner = "roboy";

	public GamingTwentyQuestionsState(String stateIdentifier, StateParameters params) {
		super(stateIdentifier, params);
	}

	@Override
	public Output act() {

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


		String intent = getIntent (input);

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

		if(gameFinished){
			applyFilter(winner);
			resetGame();
			return getTransition(TRANSITION_GAME_ENDED);

		} else {
			return this;
		}
	}

	private String getIntent(Interpretation input) {


		String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);





		String intent = null;

		for(String token : tokens) {
			if(token.equals("yes")) {
				intent = "yes";
			} else if (token.equals("no")) {
				intent = "no";
			} else if(token.equals("ready")) {
				intent = "ready";
			} else if(token.equals("back")) {
				intent = "back";
			} else if(token.equals("dont")) {
				intent = "dont know";
			} else if(token.equals("not")) {
				intent = "probably not";
			} else if(token.equals("probably")) {
				intent = "probably";
			}
		}

		return intent;
	}

	private Output askNextQuestion() throws NullPointerException{

		String nextQuestionString = null;
		try {
			nextQuestion = aw.getCurrentQuestion();
			nextQuestionString = nextQuestion.getQuestion();
			++numberGuesses;
		}
		catch(NullPointerException e){
			gameFinished = true;
			return Output.say("I throw in the towel, I think I have no idea what to ask next. You win!");
		}
		return Output.say(nextQuestionString);
	}

	private Output saveUsersEstimate(String intent) throws Exception {

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

	private Output doAGuess () throws Exception{


		String roboyAnswer = "";

		for(Guess guess : aw.getGuessesAboveProbability(PROBABILITY_THRESHOLD)) {
			if(!declined.contains(Long.valueOf(guess.getIdLong()))) {

				roboyAnswer = "Let me guess... I think it is " + guess.getName() + "?";
				currentGuess = guess;
				++numberGuesses;
				break;
			}
		}

		if(roboyAnswer.isEmpty()){
			gameFinished = true;
			winner = "interlocutor";
			roboyAnswer = "Congratulations, you defeated me. I have no more guesses.";
		}

		return Output.say(roboyAnswer);
	}

	private Output processUserGuessAnswer(String intent){

		if (intent.equals("yes")){
			gameFinished = true;
			return Output.say("I won at my " + numberGuesses + ". guess.");
		} else {
			declined.add(Long.valueOf(currentGuess.getIdLong()));
			return Output.sayNothing();
		}
	}

	private void resetGame(){

		aw = new AkiwrapperBuilder().setFilterProfanity(true).build();
		declined.clear();
		userReady = false;
		guessesAvailable = false;
		gameFinished = false;
		numberGuesses = 0;
		winner = "roboy";
	}

	private boolean isUserReady(String intent){

		if(intent.equals("ready")) {
			userReady = true;
		}

		return userReady;
	}

	private void applyFilter(String winner){
		RosMainNode rmn = getRosMainNode();
		if(winner.equals("roboy")){
			filterApplied = rmn.ApplyFilter("flies");
		} else {
			filterApplied = rmn.ApplyFilter("crown");
		}
	}

}
