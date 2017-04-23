package roboy.dialog.personality.states;

import java.util.List;

import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;

public class GenerativeCommunicationState extends AbstractBooleanState{
	
	private boolean first = true;

	@Override
	public List<Interpretation> act() {
		List<Interpretation> action = first ?
				Lists.interpretationList(new Interpretation("Do you want to ask me any questions?")) 
			  : Lists.interpretationList();
		first = false;
		return action;
	}

	@Override
	protected boolean determineSuccess(Interpretation input) {
		return false;
	}

}
