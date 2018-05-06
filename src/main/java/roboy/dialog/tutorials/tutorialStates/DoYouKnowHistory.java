package roboy.dialog.tutorials.tutorialStates;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;

public class DoYouKnowHistory extends State {

    private State next;

    public DoYouKnowHistory(String stateIdentifiers, StateParameters params) {
        super(stateIdentifiers, params);

    }

    @Override
    public Output act() {
        return Output.say("What year is the united nations was established?");
    }

    @Override
    public Output react(Interpretation input) {
        String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
        if(tokens.length > 0 && tokens[0].equals("1945")){
            next = getTransition("personKnowsHistory");
            return Output.say("You are good at history!");
        }
        else{
            next = getTransition("personDoesNotKnowHistory");
            return Output.say("Well, the UN founded on October 24, 1945");

        }
    }

    @Override
    public State getNextState() {
        return next;
    }
}
