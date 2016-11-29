package de.roboy.dialog.personality.states;

import java.util.List;

import de.roboy.dialog.action.Action;
import de.roboy.dialog.action.SpeechAction;
import de.roboy.linguistics.sentenceanalysis.Sentence;
import de.roboy.logic.StatementInterpreter;
import de.roboy.util.Lists;

public class InquiryState extends AbstractBooleanState{

	private String inquiry;
	private List<String> successTerms;
	private String failureText;
	
	public InquiryState(String inquiry, List<String> successTerms, String failureText){
		this.inquiry = inquiry;
		this.successTerms = successTerms;
		this.failureText = failureText;
	}
	
	@Override
	public List<Action> act() {
		return Lists.actionList(new SpeechAction(inquiry));
	}

	@Override
	public Reaction react(Sentence input) {
		boolean successful = StatementInterpreter.isFromList(input.sentence, successTerms);
		if(successful){
			return new Reaction(success);
		} else {
			return new Reaction(failure,Lists.actionList(new SpeechAction(failureText)));
		}
	}
	

}
