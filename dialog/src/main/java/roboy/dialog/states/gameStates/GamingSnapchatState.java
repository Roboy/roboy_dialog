package roboy.dialog.states.gameStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.Inference;
import roboy.talk.PhraseCollection;
import roboy.util.RandomList;

import java.util.*;

import static roboy.util.FileLineReader.readFile;


public class GamingSnapchatState extends State {

    private final static String TRANSITION_GAME_ENDED = "gameEnded";
    private final static String EXISTING_FILTERS_ID = "filterFile";

    private Map<String,List<String>> EXISTING_FILTERS;

    private RandomList<String> filters;

    private final Logger LOGGER = LogManager.getLogger();
    private final Inference localInference = new Inference();

    private boolean filterApplied = false;
    private List<String> desiredFilters = new ArrayList<>();
    private boolean stopGame = false;

    private String suggestedFilter = "";

    public GamingSnapchatState(String stateIdentifier, StateParameters params) {

        super(stateIdentifier, params);
        String filterListPath = params.getParameter(EXISTING_FILTERS_ID);
        LOGGER.info(" -> The filterList path: " + filterListPath);
        filters = readFile(filterListPath);
        EXISTING_FILTERS = buildSynonymMap(filters);
    }

    @Override
    public Output act() {
        suggestedFilter = filters.getRandomElement();
        return Output.say(String.format(PhraseCollection.OFFER_FILTER_PHRASES.getRandomElement(), suggestedFilter));
    }

    @Override
    public Output react(Interpretation input) {

        Linguistics.UtteranceSentiment inputSentiment = getInference().inferSentiment(input);
        List<String> inputFilters = localInference.inferSnapchatFilter(input, EXISTING_FILTERS);

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

    private Map<String, List<String>> buildSynonymMap(List<String> filters){

        Map<String, List<String>> filterMap = new HashMap<>();

        for(String filter : filters){
            switch(filter){
                case " roboy ":
                    filterMap.put(filter, new ArrayList<> (Arrays.asList("roboy", "robot", "you", "your face")));
                    break;
                case " mustache ":
                    filterMap.put(filter, new ArrayList<> (Arrays.asList("mustache", "beard", "barb")));
                    break;
                case " sunglasses ":
                    filterMap.put(filter, new ArrayList<> (Arrays.asList("sunglasses", "gasses", "shades")));
                    break;
                case " hat ":
                    filterMap.put(filter, Arrays.asList("hat", "cap", "headpiece"));
                    break;
                case " flies ":
                    filterMap.put(filter, Arrays.asList("flies", "fly", "bugs", "bug", "insects", "insect", "vermin"));
                    break;
                case " crown ":
                    filterMap.put(filter, Arrays.asList("crown", "king", "queen", "royal", "tiara"));
                    break;
            }
        }
        return filterMap;
    }

}
