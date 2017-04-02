package roboy.dialog.personality.states;

import java.util.List;

import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;

public class CelebrityState implements State{
	
	private static final List<String> triggerSentences =
			Lists.stringList(
					"whom do i look like",
					"who do i look like",
					"which star do i look like",
					"which celebrity do i look like",
					"how do i look",
					"whom do i resemble",
					"who do i resemble",
					"which star do i resemble",
					"which celebrity do i resemble",
					"whom do i remind you of",
					"who do i remind you of",
					"who am i"
					);
	
	private static final List<String> formulations =
			Lists.stringList(
					"You look one hundred percent like ",
					"You totally look like ",
					"You bear a very striking resemblance to ",
					"You must be the identical twin of ",
					"You look a lot like ",
					"You seem to be the doubleganger of ",
					"The look, the attitude. Yeah, you are totally a ",
					"You look like ",
					"That is easy. You are a ",
					"You resemble ",
					"You could easily pass for ",
					"You are the very picture of ",
					"You remind me a lot of ",
					"You take after ",
					"You were clearly created in the image of ",
					"You are like a poor version of ",
					"What is up with your face? You look like if Picasso tried to draw ",
					"Looks like they finally managed to clone ",
					"You are like an impersinator of ",
					"I am unable to distinguish between you and ",
					"You have quite some of the features of ",
					"Is that you? "
					);
	
	private State inner;
	private State top;
	
	public CelebrityState(State inner) {
		this.inner = inner;
		this.top = this;
	}
	
	public void setTop(State top){
		this.top = top;
	}

	@Override
	public List<Interpretation> act() {
		return inner.act();
	}

	@Override
	public Reaction react(Interpretation input) {
		String sentence = ((String)input.getFeature(Linguistics.SENTENCE)).trim().toLowerCase();
		if(triggerSentences.contains(sentence)){
			Object celebrity = input.getFeature(Linguistics.CELEBRITY);
//			System.out.println(input.getFeatures());
			if(celebrity==null){
				return new Reaction(top,Lists.interpretationList(
						new Interpretation("You really do not look like anything to me.")));
			} else {
				String prefix = formulations.get((int)(Math.random()*formulations.size()));
				return new Reaction(top,Lists.interpretationList(
						new Interpretation(prefix+celebrity)));
			}
		}
		return inner.react(input);
	}

}
