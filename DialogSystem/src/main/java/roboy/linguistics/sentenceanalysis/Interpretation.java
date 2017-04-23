package roboy.linguistics.sentenceanalysis;

import java.util.HashMap;
import java.util.Map;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SENTENCE_TYPE;

/**
 * An interpretation of all inputs to Roboy consists of the sentence type and an
 * arbitrary map of features. Feature names are listed in the Linguistics class.
 */
public class Interpretation {

	private Map<String,Object> features;
	private SENTENCE_TYPE sentenceType; //TODO: turn this into a feature
	
	public Interpretation(String sentence){
		features = new HashMap<>();
		features.put(Linguistics.SENTENCE,sentence);
		sentenceType = SENTENCE_TYPE.STATEMENT;
	}
	
	public Interpretation(String sentence, Map<String,Object> features){
		this.features = features;
		this.features.put(Linguistics.SENTENCE,sentence);
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
	
	public Object getFeature(String featureName){
		return features.get(featureName);
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

	@Override
	public String toString(){
		return sentenceType+" "+features;
	}
}
