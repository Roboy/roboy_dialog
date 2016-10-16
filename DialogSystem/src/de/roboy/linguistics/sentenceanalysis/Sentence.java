package de.roboy.linguistics.sentenceanalysis;

import de.roboy.linguistics.Triple;

public class Sentence {

	public enum SENTENCE_TYPE { WHO, HOW_IS, WHY, WHEN, WHERE, WHAT, IS_IT, DOES_IT, STATEMENT, NONE}

	public Triple triple;
	public SENTENCE_TYPE sentenceType;
	
	public Sentence(Triple triple, SENTENCE_TYPE sentenceType){
		this.triple = triple;
		this.sentenceType = sentenceType;
	}
}
