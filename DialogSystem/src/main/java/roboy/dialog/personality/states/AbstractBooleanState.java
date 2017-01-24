package roboy.dialog.personality.states;

import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;

public abstract class AbstractBooleanState implements State{

	protected State success;
	protected State failure;
	
	public State getSuccess() {
		return success;
	}
	public void setSuccess(State success) {
		this.success = success;
	}
	public State getFailure() {
		return failure;
	}
	public void setFailure(State failure) {
		this.failure = failure;
	}
	
	@Override
	public Reaction react(Interpretation input) {
		String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
		boolean successful = determineSuccess(input);
		if(successful){
			return new Reaction(success);
		} else {
			return new Reaction(failure,Lists.interpretationList(new Interpretation(callGenerativeModel(sentence))));
		}
	}
	
	abstract protected boolean determineSuccess(Interpretation input);
	
	protected String callGenerativeModel(String sentence){
		return "Generated text";
	}
}
