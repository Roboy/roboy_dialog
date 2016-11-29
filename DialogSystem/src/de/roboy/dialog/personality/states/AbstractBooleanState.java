package de.roboy.dialog.personality.states;

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
	
}
