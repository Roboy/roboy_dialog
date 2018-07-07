package roboy.dialog.states.gameStates;

import com.ibm.watson.developer_cloud.alchemy.v1.model.SAORelation;
import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.entities.Question;
import com.markozajc.akiwrapper.Akiwrapper.Answer;
import com.markozajc.akiwrapper.core.entities.Guess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.nodes.Interlocutor;
import roboy.ros.RosMainNode;
import roboy.talk.PhraseCollection;
import roboy.talk.Verbalizer;

import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.List;

public class GamingTwentyQuestionsState extends State {


	private static final double PROBABILITY_THRESHOLD = 0.85;
	private final static String TRANSITION_GAME_ENDED = "gameEnded";

	private final Logger LOGGER = LogManager.getLogger();

	private Akiwrapper aw = new AkiwrapperBuilder().setFilterProfanity(true).build();
	private Question nextQuestion = null;
	private Guess currentGuess = null;

	private List<Long> declined = new ArrayList<>();
	private boolean userReady = false;
	private boolean guessesAvailable = false;
	private boolean gameFinished = false;
	private boolean stopGame = false;

	private boolean filterApplied = false;
	private String winner = "";

	public GamingTwentyQuestionsState(String stateIdentifier, StateParameters params) {
		super(stateIdentifier, params);
	}

	@Override
	public Output act() {

		if(!userReady){
			return Output.say(PhraseCollection.AKINATOR_INTRO_PHRASES.getRandomElement());

		} else if(guessesAvailable) {

			try {
				return doAGuess();
			}
			catch (Exception e){
				LOGGER.error("-> Error on finding a guess:" + e.getMessage());
				gameFinished = true;
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
		Linguistics.UtteranceSentiment inputSentiment = getInference().inferSentiment(input);

		if(checkUserSaidStop(input)){
			gameFinished = true;
			winner = "interruption";
			return Output.sayNothing();

		} else if(!userReady && inputSentiment == Linguistics.UtteranceSentiment.POSITIVE){
			userReady = true;
			return Output.say(Verbalizer.startSomething.getRandomElement());

		} else if(guessesAvailable){

			return processUserGuessAnswer(intent);

		} else {

			try {

				return saveUsersEstimate(intent);
			}
			catch (Exception e){
				LOGGER.error("Error in processing input: " + e);

				return Output.say("I have a problem processing your input...");
			}
		}
	}

	@Override
	public State getNextState() {

		if(gameFinished){
			if(!winner.equals("interruption")) {
				applyFilter(winner);
			}
			resetGame();
			return getTransition(TRANSITION_GAME_ENDED);

		}
		else {
			return this;
		}
	}

	private String getIntent(Interpretation input) {

		String intent = null;
		Linguistics.UtteranceSentiment inputSentiment = getInference().inferSentiment(input);
		if(inputSentiment == Linguistics.UtteranceSentiment.POSITIVE){
			intent = "yes";
		} else if (inputSentiment == Linguistics.UtteranceSentiment.NEGATIVE) {
			intent = "no";
		}
		return intent;
	}

	private Output askNextQuestion(){

		String nextQuestionString;
		try {
			nextQuestion = aw.getCurrentQuestion();
			nextQuestionString = nextQuestion.getQuestion();
		}
		catch(NullPointerException e){
			winner = getContext().ACTIVE_INTERLOCUTOR.getValue().getName();
			gameFinished = true;
			LOGGER.error("No more questions available: " + e);
			return Output.say("I throw in the towel, I think I have no idea what to ask next.");
		}
		return Output.say(nextQuestionString);
	}

	private Output saveUsersEstimate(String intent) {

		try {

		//check if guesses above threshold are available
		if(aw.getGuessesAboveProbability(PROBABILITY_THRESHOLD).size() > 0){
			guessesAvailable = true;
		}

			if (intent != null) {
				switch (intent) {
					case "yes":
						aw.answerCurrentQuestion(Answer.YES);
						return Output.say(Verbalizer.userIsSure.getRandomElement());
					case "no":
						aw.answerCurrentQuestion(Answer.NO);
						return Output.say(Verbalizer.userSaysNo.getRandomElement());
					case "dont know":
						aw.answerCurrentQuestion(Answer.DONT_KNOW);
						return Output.say(Verbalizer.userIsUncertain.getRandomElement());
					case "probably":
						aw.answerCurrentQuestion(Answer.PROBABLY);
						return Output.say(Verbalizer.userProbablyYes.getRandomElement());
					case "probably not":
						aw.answerCurrentQuestion(Answer.PROBABLY_NOT);
						return Output.say(Verbalizer.userProbablyNo.getRandomElement());
					case "back":
						aw.undoAnswer();
						return Output.sayNothing();
					default:
						return Output.say("Please answer with either YES, NO, DONT KNOW, PROBABLY or PROBABLY NOT or go back one question with BACK.");
				}
			} else {

				return Output.say(Verbalizer.roboyNotUnderstand.getRandomElement());
			}
		} catch (IOException e){
			LOGGER.error("IO Exception: " + e.toString());
			return Output.sayNothing();
		}
	}

	private Output doAGuess (){


		String roboyAnswer = "";

		try {

			for (Guess guess : aw.getGuessesAboveProbability(PROBABILITY_THRESHOLD)) {
				if (!declined.contains(Long.valueOf(guess.getIdLong()))) {

					roboyAnswer = PhraseCollection.QUESTION_ANSWERING_START.getRandomElement() + guess.getName();
					currentGuess = guess;
					break;
				}
			}
		}catch (IOException e){
			LOGGER.error(e.toString());
		}

		if(roboyAnswer.isEmpty()){
			gameFinished = true;
			winner = getContext().ACTIVE_INTERLOCUTOR.getValue().getName();
			roboyAnswer = String.format(PhraseCollection.ROBOY_LOSER_PHRASES.getRandomElement(), winner);
		}

		return Output.say(roboyAnswer);
	}

	private Output processUserGuessAnswer(String intent){

		if (intent.equals("yes")){
			gameFinished = true;
			winner = "roboy";
			return Output.say(PhraseCollection.ROBOY_WINNER_PHRASES.getRandomElement());
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
		winner = "";
	}

	private boolean checkUserSaidStop(Interpretation input){

		stopGame = false;
		List<String> tokens = input.getTokens();
		if(tokens != null && !tokens.isEmpty()){
			if(tokens.contains("boring") || tokens.contains("stop") || tokens.contains("bored")){
				stopGame = true;

			}
		}
		return stopGame;
	}

	private void applyFilter(String winner){

		if(winner.equals("roboy")){
			filterApplied = getRosMainNode().ApplyFilter("flies");
			getRosMainNode().ShowEmotion("sunglasses");
			LOGGER.info("Snapchat-Filter Service Callback: " + filterApplied);

		} else {
			filterApplied = getRosMainNode().ApplyFilter("crown");
			LOGGER.info("Snapchat-Filter Service Callback: " + filterApplied);
			getRosMainNode().ShowEmotion("tears");
		}
	}

}
