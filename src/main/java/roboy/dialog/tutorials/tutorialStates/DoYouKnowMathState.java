package roboy.dialog.tutorials.tutorialStates;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;

public class DoYouKnowMathState extends State {

    private State next;

    public DoYouKnowMathState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        return Output.say("What is 2 plus 2?");
    }

    @Override
    public Output react(Interpretation input) {

        // get tokens (= single words of the input)
        String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);

        // check if the answer is correct (simplest version)
        if (tokens.length > 0 && tokens[0].equals("four")) {
            // answer correct
            next = getTransition("personKnowsMath");
            return Output.say("You are good at math!");

        } else {
            // answer incorrect
            next = getTransition("personDoesNotKnowMath");
            return Output.say("Well, 2 plus 2 is 4!");
        }
    }

    @Override
    public State getNextState() {
        return next;
    }
}
