package roboy.dialog.states.gameStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.talk.PhraseCollection;
import roboy.talk.Verbalizer;
import roboy.util.SynonymFileParser;

import java.util.List;

public class ChooseGameState extends State {

    private final static String TRANSITION_CHOSE_SNAPCHAT = "choseSnapchat";
    private final static String TRANSITION_CHOSE_20_Q = "chose20questions";
    private final static String TRANSITION_EXIT = "exitGame";
    private final static String EXISTING_GAMES_ID = "gameFile";

    private final Logger LOGGER = LogManager.getLogger();
    private SynonymFileParser gameParser;

    private String game = null;
    private String suggestedGame = null;


    public ChooseGameState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        String gameListPath = params.getParameter(EXISTING_GAMES_ID);
        LOGGER.info(" -> The gameList path: " + gameListPath);
        gameParser = new SynonymFileParser(gameListPath);
    }

    @Override
    public Output act() {
        suggestedGame = gameParser.getRandomKey();
        return Output.say(String.format(PhraseCollection.GAME_ASKING_PHRASES.getRandomElement(), suggestedGame));
    }

    @Override
    public Output react(Interpretation input) {

        Linguistics.UtteranceSentiment inputSentiment = getInference().inferSentiment(input);
        String inputGame = getInference().inferGame(input, gameParser.getSynonyms());

        if (inputSentiment == Linguistics.UtteranceSentiment.POSITIVE){
            game = suggestedGame;
            return Output.say(Verbalizer.startSomething.getRandomElement());
        } else if (inputGame != null){
            game = inputGame;
            return Output.say(Verbalizer.startSomething.getRandomElement());
        } else if (inputSentiment == Linguistics.UtteranceSentiment.NEGATIVE){
            game = "exit";
        }
        return Output.sayNothing();
    }

    @Override
    public State getNextState() {

        switch (game){
            case "20questions":
                return getTransition(TRANSITION_CHOSE_20_Q);
            case "snapchat":
                return getTransition(TRANSITION_CHOSE_SNAPCHAT);
            case "exit":
                return getTransition(TRANSITION_EXIT);
            default:
                return this;
        }

    }
}
