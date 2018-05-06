package roboy.dialog.states.ordinaryStates;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;

public class DoYouKnowMath extends State {

    private State next;

    @Override
    public Output act() {
        return Output.say("What is 2 plus 2?");
    }

    @Override
    public Output react(Interpretation input) {

        String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
        if(tokens.length > 0 && tokens[0].equals("four")){
            next = getTransition("personKnowsMath");
            return Output.say("You are good at math");
        }else{
            next = getTransition("personDoesNotKnowMath");
            return Output.say("Well, 2 plus 2 is 4!");
        }
    }

    @Override
    public State getNextState() {
        return next;
    }

    public DoYouKnowMath(String stateIdentifier, StateParameters params){
        super(stateIdentifier, params);
    }
}
