package roboy.linguistics.sentenceanalysis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import roboy.linguistics.Linguistics;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

/**
 * Perform part-of-speech tagging (detecting nouns, verbs etc.) using the Open NLP POS tagger and 
 * stores the results in the Linguistics.POSTAGS attribute of the interpretation.
 */
public class OpenNLPPPOSTagger implements Analyzer{

	private POSTaggerME tagger;
	
	public OpenNLPPPOSTagger() {
		// load POS tagger https://opennlp.apache.org/documentation/manual/opennlp.html#tools.postagger
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
	}
	
	@Override
	public Interpretation analyze(Interpretation interpretation) {
		String[] tokens = (String[]) interpretation.getFeatures().get(Linguistics.TOKENS);
		String[] posTags = posTag(tokens);
		interpretation.getFeatures().put(Linguistics.POSTAGS,posTags);
		return interpretation;
	}

	private String[] posTag(String[] tokens){
		  String[] posTags =  tagger.tag(tokens);
		  return posTags;
	}
}
