package de.roboy.linguistics.sentenceanalysis;

import java.util.HashMap;
import java.util.Map;

import de.roboy.linguistics.Linguistics;
import de.roboy.linguistics.Linguistics.SENTENCE_TYPE;

public class Interpretation {

	private Map<String,Object> features;
	private SENTENCE_TYPE sentenceType;
	
	public Interpretation(String sentence){
		features = new HashMap<>();
		features.put(Linguistics.SENTENCE,sentence);
		sentenceType = SENTENCE_TYPE.STATEMENT;
	}
	
	public Interpretation(SENTENCE_TYPE sentenceType){
		this.sentenceType = sentenceType;
	}
	
	public Interpretation(SENTENCE_TYPE sentenceType, Map<String,Object> features){
		this.sentenceType = sentenceType;
		this.features = features;
	}

	public Map<String, Object> getFeatures() {
		return features;
	}

	public void setFeatures(Map<String, Object> features) {
		this.features = features;
	}

	public SENTENCE_TYPE getSentenceType() {
		return sentenceType;
	}

	public void setSentenceType(SENTENCE_TYPE sentenceType) {
		this.sentenceType = sentenceType;
	}
}
