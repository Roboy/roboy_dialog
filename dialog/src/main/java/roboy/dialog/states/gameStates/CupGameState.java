package roboy.dialog.states.gameStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yecht.Data;
import org.yecht.ruby.Out;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.emotions.RoboyEmotion;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.Inference;
import roboy.logic.StatementInterpreter;
import roboy.talk.PhraseCollection;
import roboy.talk.Verbalizer;
import roboy.util.RandomList;

import java.util.*;

import static roboy.util.FileLineReader.readFile;


public class CupGameState extends State {

    private final static List<String> EXIT_REQUEST = Arrays.asList("bored", "stop", "something else", "different", "boring");
    private final static List<String> READY = Arrays.asList("done", "ready", "let's go", "play");
    private final static String TRANSITION_EXIT = "exit";
    private final Logger LOGGER = LogManager.getLogger();

    private enum CupGamePhase {
        SHUFFLE,
        SCAN,
        GUESS,
        OFFER_REPEAT,
        EXIT
    }


    private final static String FIND_CUPS = "find_cups";
    private final static String GOTOCUP1 = "cup_1";
    private final static String GOTOCUP2 = "cup_2";
    private final static String FINISHED = "finished";

    private CupGamePhase phase = CupGamePhase.SHUFFLE;

    public CupGameState(String stateIdentifier, StateParameters params) {

        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        switch (phase){
            case SHUFFLE:
                return Output.say("you shall shuffle the cups, my friend. let me know when you are ready");
            case SCAN:
                // TODO get response from the state machine
//                if (getRosMainNode().StartCupGameSmach()) {
                    // if smach start is successful
                    // comment on actions while not finished exploring
                    String prevState = "";
                    String currentSmachState = getContext().CUP_GAME_SMACH_STATE_MSG.getValue();
                    while(!currentSmachState.equals(FINISHED)) {
                        currentSmachState = getContext().CUP_GAME_SMACH_STATE_MSG.getValue();
                        if (!currentSmachState.equals(prevState)) {
                            switch (getContext().CUP_GAME_SMACH_STATE_MSG.getValue()) {
                                case FIND_CUPS:
                                    getRosMainNode().SynthesizeSpeech("i am looking for cups");
                                    break;
                                case GOTOCUP1:
                                    getRosMainNode().ShowEmotion(RoboyEmotion.HYPNO_COLOUR_EYES);
                                    getRosMainNode().SynthesizeSpeech("wubba lubba dub dub, doing magic for this cup");
                                    break;
                            }

                            prevState = currentSmachState;
                        }
//                    }
                }
                return Output.say("so are you ready to hear the oracle's prophecy?");
            case GUESS:
                return Output.say("I think I found it there. So do I win now?");
            case OFFER_REPEAT:
                return Output.say("wanna play again?");
            default:
                return Output.sayNothing();
        }

    }

    @Override
    public Output react(Interpretation input) {
        exitOnRequest(input.getSentence());

        switch (phase) {
            case SHUFFLE:
                if (StatementInterpreter.isFromList(input.getSentence(),READY)) {
                    phase = CupGamePhase.SCAN;
                    return Output.say("Alright. Let me check with my superpower soli where did you hide the ball");
                }
                else {
                    return Output.say("Hm, seems you are not done yet. I give you another try");
                }
            case SCAN:
                phase = CupGamePhase.GUESS;
                return Output.useFallback();
            case GUESS:
                phase = CupGamePhase.OFFER_REPEAT;
                return Output.say("ha-ha.");
            case OFFER_REPEAT:
                if (StatementInterpreter.isFromList(input.getSentence(),Verbalizer.consent)) {
                    phase = CupGamePhase.SHUFFLE;
                    return Output.say("Here we go again then!");
                }
                else
                    phase = CupGamePhase.EXIT;
                    return Output.say("I believe you've had enough of my magic");
            case EXIT:
                return Output.say("Wanna stop playing? Sure, if you say so.");
            default:
                return Output.useFallback();
        }

    }

    private void exitOnRequest(String input) {
        if(StatementInterpreter.isFromList(input, EXIT_REQUEST)) {
            phase = CupGamePhase.EXIT;
        }
    }

    @Override
    public State getNextState() {
        if (phase == CupGamePhase.EXIT) {
            return getTransition(TRANSITION_EXIT);
        }
        else {
            return this;
        }
    }

    @Override
    protected Set<String> getRequiredTransitionNames() {
        return newSet(TRANSITION_EXIT);
    }


}
