package roboy.dialog.personality.states;

import java.util.List;

import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.util.Lists;

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
	public List<Interpretation> act() {
		return Lists.interpretationList(new Interpretation(inquiry));
	}

	@Override
	protected boolean determineSuccess(Interpretation input) {
		String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
		return StatementInterpreter.isFromList(sentence, successTerms);
	}
	

}
