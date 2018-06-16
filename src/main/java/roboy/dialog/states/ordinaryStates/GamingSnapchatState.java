package roboy.dialog.states.ordinaryStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;


import roboy.ros.RosMainNode;
import roboy.talk.PhraseCollection;
import roboy.util.SynonymFileParser;

import java.util.ArrayList;
import java.util.List;


public class GamingSnapchatState extends State {

    private final static String TRANSITION_GAME_ENDED = "gameEnded";
    private final static String EXISTING_FILTERS_ID = "filterFile";

    private final Logger LOGGER = LogManager.getLogger();
    private SynonymFileParser scParser;

    private boolean filterApplied = false;
    private List<String> desiredFilters = new ArrayList<>();
    private boolean stopGame = false;

    private String suggestedFilter = "";

    public GamingSnapchatState(String stateIdentifier, StateParameters params) {

        super(stateIdentifier, params);
        String filterListPath = params.getParameter(EXISTING_FILTERS_ID);
        LOGGER.info(" -> The filterList path: " + filterListPath);
        scParser = new SynonymFileParser(filterListPath);
    }

    @Override
    public Output act() {
        suggestedFilter = scParser.getRandomKey();
        return Output.say(String.format(PhraseCollection.OFFER_FILTER_PHRASES.getRandomElement(), suggestedFilter));
    }

    @Override
    public Output react(Interpretation input) {

        Linguistics.UtteranceSentiment inputSentiment = getInference().inferSentiment(input);
        List<String> inputFilters = getInference().inferSnapchatFilter(input, scParser.getSynonyms());

        if(!checkUserSaidStop(input)) {
            if (inputSentiment == Linguistics.UtteranceSentiment.POSITIVE) {
                desiredFilters.add(suggestedFilter);
            } else if (inputFilters != null) {
                desiredFilters = inputFilters;
            }
            for(String filter : desiredFilters){
                filterApplied = getRosMainNode().ApplyFilter(filter);
            }
            desiredFilters.clear();
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
}
