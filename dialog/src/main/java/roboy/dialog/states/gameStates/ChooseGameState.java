package roboy.dialog.states.gameStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.talk.PhraseCollection;
import roboy.talk.Verbalizer;
import roboy.util.RandomList;

import java.util.*;
import java.util.stream.Collectors;

public class ChooseGameState extends State {


    /*
    Hey, if you want to add a new GameState, you will need to add your GameState into the HashMap EXISTING_GAME_MAP.
    If you implemented it correctly, all should work here without problems :)
     */

    private final static String TRANSITION_EXIT = "exitGame";

    private final static HashMap<String, GameState> EXISTING_GAME_MAP = new HashMap<>(2); //CHANGE THE VALUE OF THIS TO THE AMOUNT OF GAMES YOU SHALL ADD (EFFICIENCY)

    private final Logger LOGGER = LogManager.getLogger();

    private String game = null;
    private String suggestedGame = null;


    public ChooseGameState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }
    //Can't fill this at constructor or static constructor level, so it is done in act()
    private void fillExistingGameMap(){
        if(EXISTING_GAME_MAP.isEmpty()){
            //TODO find a better solution for the value, can we somehow get the transitions from the method cleanly?
            EXISTING_GAME_MAP.put("Akinator", (GameState) getTransition("chose20questions"));
            EXISTING_GAME_MAP.put("Snapchat", (GameState) getTransition("choseSnapchat"));
        }
    }

    @Override
    public Output act() {
        fillExistingGameMap();

        //Pick a game that is playable
        RandomList<String> randomList = new RandomList<String>();
        randomList.addAll( //Streams to Filter, because why have Java 1.8 if we can't make use of it
                EXISTING_GAME_MAP.keySet().stream().filter(s -> EXISTING_GAME_MAP.get(s).canStartGame())
                        .collect(Collectors.toCollection(RandomList::new)));

        //If no games are playable
        if(randomList.isEmpty()){
            LOGGER.info("No games available");
            suggestedGame = "exit";
            return Output.say("I do not think I know any games that are playable in this environment. Should I try again?");
        }
        //Choose a random game that is playable
        else{
            suggestedGame = randomList.getRandomElement();
            return Output.say(String.format(PhraseCollection.GAME_ASKING_PHRASES.getRandomElement(), suggestedGame));
        }
    }

    @Override
    public Output react(Interpretation input) {
        //Reset the Value of Game
        game = "";

        Linguistics.UtteranceSentiment inputSentiment = getInference().inferSentiment(input);
        String inputGame = inferGame(input);

        //Should we try again?
        if(suggestedGame.equals("exit")){
            if(inputSentiment == Linguistics.UtteranceSentiment.NEGATIVE){
                game = suggestedGame;
            }
        }
        //If a game can be played...
        else {
            //If you AGREE with the selected game, do this
            if (inputSentiment == Linguistics.UtteranceSentiment.POSITIVE) {
                //Can we run the game?
                if(EXISTING_GAME_MAP.get(suggestedGame).canStartGame()) {
                    game = suggestedGame;
                    return Output.say(Verbalizer.startSomething.getRandomElement());
                }
                else{
                    game = "exit";
                    return EXISTING_GAME_MAP.get(suggestedGame).cannotStartWarning();
                }
            }
            //If you suggested your OWN game, do this
            if (!inputGame.isEmpty()) {
                //Can we run the game?
                if(EXISTING_GAME_MAP.get(inputGame).canStartGame()) {
                    game = inputGame;
                    return Output.say(Verbalizer.startSomething.getRandomElement());
                }
                else{
                    game = "exit";
                    return EXISTING_GAME_MAP.get(inputGame).cannotStartWarning();
                }
            }
            //If you DISAGREE with the game, quit...
            if (inputSentiment == Linguistics.UtteranceSentiment.NEGATIVE) {
                game = "exit";
            }
        }

        return Output.sayNothing();
    }

    @Override
    public State getNextState() {
        if(game.equals("exit")) {
            return getTransition(TRANSITION_EXIT);
        }
        else{
            if(EXISTING_GAME_MAP.containsKey(game)) {
                return getTransition(EXISTING_GAME_MAP.get(game).getTransitionName());
            }
            else{
                return this;
            }
        }
//        switch (game){
//            case "Akinator":
//                return getTransition(TRANSITION_CHOSE_20_Q);
//            case "Snapchat":
//                return getTransition(TRANSITION_CHOSE_SNAPCHAT);
//            case "exit":
//                return getTransition(TRANSITION_EXIT);
//            default:
//                return this;
//        }

    }
    
    private String inferGame(Interpretation input){
    //VERY IMPORTANT: If your tags conflict, the one with higher priority in the hashmap shall be taken
        List<String> tokens = input.getTokens();
        String game = "";
        if(tokens != null && !tokens.isEmpty()) {
            for (String key : EXISTING_GAME_MAP.keySet()) {
                for (String tag : EXISTING_GAME_MAP.get(key).getTags()) {
                    if (tokens.contains(tag)) {
                        game = key;
                        return game;
                    }
                }
            }
        }
        return game;
    }


}
