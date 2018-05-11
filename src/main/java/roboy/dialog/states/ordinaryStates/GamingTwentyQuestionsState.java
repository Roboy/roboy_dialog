package roboy.dialog.states.ordinaryStates;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.dialog.states.definitions.State.Output;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;

public class GamingTwentyQuestionsState extends State{

	public GamingTwentyQuestionsState(String stateIdentifier, StateParameters params) {
		super(stateIdentifier, params);
	}

	private State next;
	
	@Override
	public Output act() {
		
		return Output.say("Asking a question...");
	}

	@Override
	public Output react(Interpretation input) {
		
		Linguistics.UtteranceSen

		if () {
			next = getTransition("positiveAnswer");
			
		}
		else if (((String) input.getFeature(Linguistics.SENTENCE)).contains("no")) {
			next = getTransition("negativeAnswer");
			
		}
		return Output.sayNothing();

	}

	@Override
	public State getNextState() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
