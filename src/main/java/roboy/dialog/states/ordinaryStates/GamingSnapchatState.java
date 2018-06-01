package roboy.dialog.states.ordinaryStates;


import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.talk.PhraseCollection;

import roboy.ros.RosMainNode;

import java.util.ArrayList;

public class GamingSnapchatState extends State {


    private final static String TRANSITION_GAME_ENDED = "gameEnded";

    private final static ArrayList<String> possibleFilters = PhraseCollection.SNAPCHAT_FILTERS;
    private boolean filterApplied = false;
    private String desiredFilter = null;
    private boolean stopGame = false;

    public GamingSnapchatState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {

        return Output.say("What filter do you want? " + possibleFilters);
    }

    @Override
    public Output react(Interpretation input) {
        if(!checkUserSaidStop(input)) {
            desiredFilter = getFilterFromInput(input);
            RosMainNode rmn = getRosMainNode();
            filterApplied = rmn.ApplyFilter(desiredFilter);
        }
        return Output.sayNothing();
    }

    @Override
    public State getNextState() {

        if(stopGame){
            stopGame = false;
            return getTransition(TRANSITION_GAME_ENDED);
        } else {
            return this;
        }
    }


    private String getFilterFromInput(Interpretation input){

        String filter = null;
        String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
        for (String token : tokens){
            token = " " + token + " ";
            if(possibleFilters.contains(token)){
            filter = token.trim();
            System.out.println(filter);
            break;
            }
        }
        return filter;
    }

    private boolean checkUserSaidStop(Interpretation input){
        if(input.getSentenceType().compareTo(Linguistics.SENTENCE_TYPE.STOP) == 0){
            stopGame = true;
        }
        return stopGame;
    }
}
