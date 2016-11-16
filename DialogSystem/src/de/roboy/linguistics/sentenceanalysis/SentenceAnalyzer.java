package de.roboy.linguistics.sentenceanalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.roboy.linguistics.Term;
import de.roboy.linguistics.Triple;
import de.roboy.linguistics.sentenceanalysis.Sentence.SENTENCE_TYPE;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class SentenceAnalyzer implements Analyzer{
	
	private Map<String,String> meanings;
	POSTaggerME tagger;
	
	public SentenceAnalyzer () throws JsonSyntaxException, JsonIOException, FileNotFoundException{
		// load POS tagger
		InputStream modelIn = null;
		try {
		  modelIn = new FileInputStream("resources/en-pos-maxent.bin");
		  POSModel model = new POSModel(modelIn);
		  tagger = new POSTaggerME(model);
		}
		catch (IOException e) {
		  // Model loading failed, handle the error
		  e.printStackTrace();
		}
		finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}

		// find meaning
		meanings = new HashMap<>();
		ClassLoader cl = this.getClass().getClassLoader();
		URL url = cl.getResource("ontology/terms");
		Gson gson = new Gson();
	    File dir=null;
		try {
			dir = new File(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	    for (File nextFile : dir.listFiles()) {
	    	Term t = gson.fromJson(new FileReader(nextFile), Term.class);
	    	meanings.put(nextFile.getName().substring(0,nextFile.getName().length()-5), t.concept);
	    }
	}
	
	@Override
	public Sentence analyze(String sentence){
		// NLP pipeline
		String[] tokens = tokenize(sentence);
		String[] posTags = posTag(tokens);
		SENTENCE_TYPE sentenceType = determineSentenceType(tokens, posTags);
		Sentence result = extractPAS(sentence, tokens, posTags, sentenceType);
		return result;
	}
	
	private String[] tokenize(String sentence){
		return sentence.split("\\s+");
	}
	
	private String[] posTag(String[] tokens){
		  String[] posTags =  tagger.tag(tokens);
//		  for(int i=0; i<posTags.length; i++){
//			  System.out.println("  "+tokens[i]+":"+posTags[i]);
//		  }
		  return posTags;
	}

	private SENTENCE_TYPE determineSentenceType(String[] tokens, String[] posTags){
		if(tokens.length==0) return SENTENCE_TYPE.NONE;
		String first = tokens[0].toLowerCase();
		if("who".equals(first)) return SENTENCE_TYPE.WHO;
		if("where".equals(first)) return SENTENCE_TYPE.WHERE;
		if("what".equals(first)) return SENTENCE_TYPE.WHAT;
		if("when".equals(first)) return SENTENCE_TYPE.WHEN;
		if("why".equals(first)) return SENTENCE_TYPE.WHY;
		if("do".equals(first)) return SENTENCE_TYPE.DOES_IT;
		if("does".equals(first)) return SENTENCE_TYPE.DOES_IT;
		if("did".equals(first)) return SENTENCE_TYPE.DOES_IT;
		if("is".equals(first)) return SENTENCE_TYPE.IS_IT;
		if("are".equals(first)) return SENTENCE_TYPE.IS_IT;
		if("am".equals(first)) return SENTENCE_TYPE.IS_IT;
		if(tokens.length==1) return SENTENCE_TYPE.STATEMENT;
		String second = tokens[1].toLowerCase();
		if("how".equals(first) && 
				("is".equals(second)||"are".equals("second")||"am".equals(second))){
			return SENTENCE_TYPE.HOW_IS;
		}
		return SENTENCE_TYPE.STATEMENT;
	}
	
	private Sentence extractPAS(String sentence, String[] tokens, String[] posTags, SENTENCE_TYPE sentenceType){
//		System.out.println("  "+sentenceType);
		switch(sentenceType){
			case STATEMENT: return new Sentence(sentence,analyzeStatement(tokens, posTags),sentenceType);
			case IS_IT:     return new Sentence(sentence,analyzeIsIt(tokens, posTags),sentenceType);
			case WHO:       return new Sentence(sentence,analyzeWho(tokens, posTags),sentenceType);
			case HOW_IS:       return new Sentence(sentence,analyzeHow(tokens, posTags),sentenceType);
			default:        return new Sentence(sentence,new Triple(null,"",""),sentenceType);
		}
	}
	
	private Triple analyzeStatement(String[] tokens, String[] posTags){
		String predicate = null;
		String agens = "";
		String patiens = "";
		for(int i=0; i<tokens.length; i++){
			if(posTags[i].startsWith("V")){
				for(int j=0; j<i; j++){
					if(posTags[j].startsWith("N")||posTags[j].startsWith("J")){
						if(j>0) agens+=" ";
						agens+=tokens[j];
					}
				}
				predicate = tokens[i];
				for(int j=i+1; j<tokens.length; j++){
					if(posTags[j].startsWith("N")||posTags[j].startsWith("J")){
						if(j>i+1) patiens+=" ";
						patiens+=tokens[j];
					}
				}
			}
		}
		return new Triple(predicate,agens,patiens);
	}
	
	private Triple analyzeIsIt(String[] tokens, String[] posTags){
		String predicate = null;
		String agens = "";
		String patiens = "";
		if(tokens.length>2){
			predicate = tokens[0];
			agens = tokens[1];
			for(int j=2; j<tokens.length; j++){
				if(j>2) patiens+=" ";
				patiens+=tokens[j];
			}
		}
		return new Triple(predicate,agens,patiens);
	}
	
	private Triple analyzeWho(String[] tokens, String[] posTags){
		String predicate = null;
		String agens = "";
		String patiens = "";
		if(tokens.length>2 && posTags[1].startsWith("V")){
			agens = null;
			predicate = tokens[1];
			for(int j=2; j<tokens.length; j++){
				if(j>2) patiens+=" ";
				patiens+=tokens[j];
			}
		}
		return new Triple(predicate,agens,patiens);
	}
	
	private Triple analyzeHow(String[] tokens, String[] posTags){
		String predicate = null;
		String agens = "";
		String patiens = "";
		if(tokens.length>2){
			patiens = null;
			predicate = tokens[1];
			for(int j=2; j<tokens.length; j++){
				if(j>2) agens+=" ";
				agens+=tokens[j];
			}
		}
		return new Triple(predicate,agens,patiens);
	}
	
//	private int detectNP(String[] posTags){
//		
//	}
	
//	private int detectHeadVerb(String[] posTags){
//		
//	}
}
