package roboy.dialog.personality.states;

import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import org.json.JSONObject;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Ros;
import roboy.util.Lists;
import roboy.util.RosMainNode;

/**
 * Abstract super class for states that fork between two possible subsequent states.
 * The determineSuccess method needs to be implemented by subclass to determine if
 * the success or failure state should be moved into next.
 */
public abstract class AbstractBooleanState implements State {

    protected State success;
    protected State failure;

    public State getSuccess() {
        return success;
    }

    /**
     * Sets the state Roboy moves into if the determineSuccess method
     * returns true.
     * 
     * @param success The following state
     */
    public void setSuccess(State success) {
        this.success = success;
    }

    public State getFailure() {
        return failure;
    }

    /**
     * Sets the state Roboy moves into if the determineSuccess method
     * returns false.
     * 
     * @param failure The following state
     */
    public void setFailure(State failure) {
        this.failure = failure;
    }

    public void setNextState(State state) {
        this.success = this.failure = state;
    }

    @Override
    public Reaction react(Interpretation input)
    {
        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
        boolean successful = determineSuccess(input);
        if (successful) {
            return new Reaction(success);
        } else {
            return new Reaction(failure, Lists.interpretationList(new Interpretation(callGenerativeModel(sentence))));
        }
    }

    /**
     * Needs to be implemented by subclasses. If the method returns true the
     * state machine moves to the success state, if it returns false it moves
     * to the failure state.
     * 
     * @param input The interpretation of all inputs
     * @return true or false depending on the examined condition of the method
     */
    abstract protected boolean determineSuccess(Interpretation input);


    protected String callGenerativeModel(String sentence) {
        String text = RosMainNode.getInstance().GenerateAnswer(sentence);
        return text;
    }
}
