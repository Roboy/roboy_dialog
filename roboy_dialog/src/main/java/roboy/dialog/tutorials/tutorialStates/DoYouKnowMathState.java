package roboy.dialog.tutorials.tutorialStates;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;

import java.util.List;

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
        List<String> tokens = input.getTokens();

        // check if the answer is correct (simplest version)
        if (tokens != null && ((List) tokens).size() > 0 && tokens.get(0).equals("four")) {
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
