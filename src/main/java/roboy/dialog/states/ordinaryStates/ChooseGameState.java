package roboy.dialog.states.ordinaryStates;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;

public class ChooseGameState extends State {

    private final static String TRANSITION_CHOSE_SNAPCHAT = "choseSnapchat";
    private final static String TRANSITION_CHOSE_20_Q = "chose20questions";

    State nextState = null;
    String game = null;


    public ChooseGameState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {

        return Output.say("What game do you want to play?");
    }

    @Override
    public Output react(Interpretation input) {

        return Output.say("Let's play " + getGame(input));
    }

    @Override
    public State getNextState() {

        switch (game){
            case "20questions":
                return getTransition(TRANSITION_CHOSE_20_Q);
            case "snapchat":
                return getTransition(TRANSITION_CHOSE_SNAPCHAT);
            default:
                return this;
        }

    }


    private String getGame(Interpretation input){

        String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);

        for(String token : tokens) {

            if (token.equals("20questions")) {
                game = "20questions";
            } else if (token.equals("snapchat")) {
                game = "snapchat";
            }
        }
        return game;
    }
}
