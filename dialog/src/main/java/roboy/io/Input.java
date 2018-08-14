package roboy.io;

import roboy.linguistics.sentenceanalysis.Interpretation;

import java.util.HashMap;

/**
 * The result of an input device consists of a sentence, if it is an audio device, and
 * an arbitrary map of lists.
 */
public class Input {
	
	private String sentence;
	private Interpretation attributes;

	//TODO we might not need this constructor anymore
	public Input(String sentence){
		this.sentence = sentence;
		attributes = null;
	}
	
	public Input(String sentence, Interpretation attributes){
		this.sentence = sentence;
		this.attributes = attributes;
	}


	public Input(String sentence, SpeakerInfo speakerInfo) {
		this.sentence = sentence;
		this.attributes.setSpeakerInfo(speakerInfo);
	}

	public String getSentence() {
	    return sentence;
    }

    public Interpretation getAttributes(){
	    return attributes;
    }

    public void setAttributes(Interpretation interpretation) {
	    attributes = interpretation;
    }
}
