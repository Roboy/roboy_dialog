package roboy.dialog.states.gameStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.talk.PhraseCollection;
import roboy.talk.Verbalizer;
import roboy.util.Maps;
import roboy.util.Pair;
import roboy.util.RandomList;

import java.util.*;
import java.util.stream.Collectors;

public class ChooseGameState extends State {


    /*
    Hey, if you want to add a new GameState, you will need to add your GameState into the HashMap EXISTING_GAME_MAP. That should be it. Don't forget to increment the initialCapacity of the HashMap
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
            addElement("Akinator", (GamingTwentyQuestionsState          .transitionName));
            addElement("Snapchat", (GamingSnapchatState                 .transitionName));
        }
    }

    private void addElement(String key, String transitionName){
        if(getAllTransitions().containsKey(transitionName)){
            EXISTING_GAME_MAP.put(key, (GameState) getTransition(transitionName));
        }
        else{
            LOGGER.error("Cannot add "+transitionName+" to EXISTING_GAME_MAP because transition does not exist");
        }
    }

    @Override
    public Output act() {
        fillExistingGameMap();

        //Pick a game that is playable
        RandomList<String> randomList = new RandomList<>();
        randomList.addAll(
                //Streams to Filter, because why have Java 1.8 if we can't make use of it
                //Basically: Get all keys, turn that list into a stream, filter the stream by whether the key's value (the game) is capable of starting. Then put all those who can start into a random list.
                EXISTING_GAME_MAP.keySet().stream().filter(s -> EXISTING_GAME_MAP.get(s).canStartGame())
                        .collect(Collectors.toCollection(ArrayList::new)));

        //If no games are playable
        if(randomList.isEmpty()){
            LOGGER.info("No games available");

            suggestedGame = "exit";
            return Output.say("I do not think I know any games that are playable in this environment. Should I try again?");
        }
        //Choose a random game that is playable
        else{
            LOGGER.debug(""+randomList.size()+" games are available to play");
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

        //If no games can be played, should we try again?
        if(suggestedGame.equals("exit")){
            if(inputSentiment == Linguistics.UtteranceSentiment.NEGATIVE){
                game = suggestedGame;
            }
        }
        //If a game can be played...
        else {
            //If you AGREE with the selected game, try to play the game
            if (inputSentiment == Linguistics.UtteranceSentiment.POSITIVE) {
                return attemptGameLaunch(suggestedGame);
            }
            //If you suggested your OWN game, try to play the game
            if (!inputGame.isEmpty()) {
                return attemptGameLaunch(inputGame);
            }
            //If you DISAGREE with the selected game, exit...
            if (inputSentiment == Linguistics.UtteranceSentiment.NEGATIVE) {
                game = "exit";
            }
        }

        return Output.sayNothing();
    }

    private Output attemptGameLaunch(String inputGame) {
        //Check if Game is playable
        if(EXISTING_GAME_MAP.get(inputGame).canStartGame()) {
            //If so, lets go start it
            game = inputGame;
            return Output.say(Verbalizer.startSomething.getRandomElement());
        }
        else{
            //Else, quit and issue a warning
            LOGGER.warn("Detected that the given game cannot be played for some reason");

            game = "exit";
            return EXISTING_GAME_MAP.get(inputGame).cannotStartWarning();
        }
    }

    @Override
    public State getNextState() {
        //CASE: EXIT
        if(game.equals("exit")) {
            return getTransition(TRANSITION_EXIT);
        }
        else{
            //Does game exist in Map --> Basically, should we launch a game...
            if(EXISTING_GAME_MAP.containsKey(game)) {
                String transition = Maps.value2Key( getAllTransitions(), EXISTING_GAME_MAP.get(game)).get();
                return getTransition(transition);
            }
            //Or repeat this state again, because we need to check something
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
    //VERY IMPORTANT: If your tags conflict, the one which comes first in the Hash Map shall be taken, the other shall be ignored. If for some reason you have conflicting Tags, rewrite this method
        List<String> tokens = input.getTokens();
        String inferredGame = "";
        //If Tokens Exist
        if(tokens != null && !tokens.isEmpty()) {
            //Get All Keys
            for (String key : EXISTING_GAME_MAP.keySet()) {
                //Get all Tags from each Keys Value
                for (String tag : EXISTING_GAME_MAP.get(key).getTags()) {
                    //If Tag is found in tokens
                    if (tokens.contains(tag)) {
                        //Return the key/game
                        return key;
                    }
                }
            }
        }
        return inferredGame;
    }


}
