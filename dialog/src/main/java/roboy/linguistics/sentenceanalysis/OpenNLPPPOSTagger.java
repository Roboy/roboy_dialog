package roboy.linguistics.sentenceanalysis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
		}catch (IOException e) {
		  // Model loading failed, handle the error
		  e.printStackTrace();
		}
		finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }catch (IOException e) {
				e.printStackTrace();
		    }
		  }
		}
	}
	
	@Override
	public Interpretation analyze(Interpretation interpretation) {
		List<String> tokens = interpretation.getTokens();
		String[] posTags = extractPosTag(tokens);
		interpretation.setPosTags(posTags);
		return interpretation;
	}

	private String[] extractPosTag(List<String> tokens){
		  String[] posTags = tagger.tag((String[]) tokens.toArray());
		  return posTags;
	}
}
