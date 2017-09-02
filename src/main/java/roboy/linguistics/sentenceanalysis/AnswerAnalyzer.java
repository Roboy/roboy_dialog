package roboy.linguistics.sentenceanalysis;

import java.util.Map;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SEMANTIC_ROLE;

/**
 * Checks the predicate argument structures produced by the OpenNLPParser analyzer
 * and looks for possible answers to questions in them.
 * 
 * It creates the outputs Linguistics.OBJ_ANSWER for situations where the answer
 * to the question is in the object of the sentence (e.g. "Frank" in the sentence
 * "I am Frank" to the question "Who are you?") and Linguistics.PRED_ANSWER
 * if it is in the predicate or in the predicate and the object combined (e.g. 
 * "swimming" in the answer "I like swimming" to the question "What is your hobby?").
 */
public class AnswerAnalyzer implements Analyzer{
	
	@Override
	public Interpretation analyze(Interpretation interpretation) {
		
		// case pas object answer
		@SuppressWarnings("unchecked")
		Map<SEMANTIC_ROLE,Object> pas = (Map<SEMANTIC_ROLE,Object>) 
				interpretation.getFeatures().get(Linguistics.PAS);
//		System.out.println(interpretation.getFeatures());
		if(pas.containsKey(SEMANTIC_ROLE.PREDICATE)
				&& pas.containsKey(SEMANTIC_ROLE.LOCATION)){
			String pp = (String) pas.get(SEMANTIC_ROLE.LOCATION);
			String pred = (String) pas.get(SEMANTIC_ROLE.PREDICATE);
			if(Linguistics.tobe.contains(pred)){
				pred = "";
			} else {
				pred = pred + " ";
			}
			if(pp.contains(" ")){
				pp = pp.substring(pp.indexOf(" ")+1);
			}
			interpretation.getFeatures().put(Linguistics.OBJ_ANSWER, pp.toLowerCase());
			interpretation.getFeatures().put(Linguistics.PRED_ANSWER, pred+pp.toLowerCase());
			
			return interpretation;
		} else if(pas.containsKey(SEMANTIC_ROLE.PREDICATE)
				&& pas.containsKey(SEMANTIC_ROLE.PATIENT)){
			String pred = ((String) pas.get(SEMANTIC_ROLE.PREDICATE)).toLowerCase();
			if(Linguistics.tobe.contains(pred)){
				pred = "";
			} else {
				pred = pred + " ";
			}
			String pat = ((String)pas.get(SEMANTIC_ROLE.PATIENT)).toLowerCase();
			interpretation.getFeatures().put(Linguistics.OBJ_ANSWER, pat);
			interpretation.getFeatures().put(Linguistics.PRED_ANSWER, pred+pat);
			return interpretation;
		}
		
		// check for last verb
		int verbIndex = -1;
		String[] tokens = (String[]) interpretation.getFeatures().get(Linguistics.TOKENS);
		String[] pos = (String[]) interpretation.getFeatures().get(Linguistics.POSTAGS);
		
		for(int i=0; i<pos.length; i++){
			if(pos[i].startsWith("V")){
				verbIndex = i;
			}
		}
//		System.out.println("Verbindex="+verbIndex);
		
		// case one term answer
		if(verbIndex==-1){
			interpretation.getFeatures().put(Linguistics.OBJ_ANSWER, 
					((String)interpretation.getFeature(Linguistics.SENTENCE)).toLowerCase());
			interpretation.getFeatures().put(Linguistics.PRED_ANSWER, 
					((String)interpretation.getFeature(Linguistics.SENTENCE)).toLowerCase());
			return interpretation;
		}
		
		// case pas failed object answer
		StringBuilder answer = new StringBuilder("");
		StringBuilder answerPred = new StringBuilder("");
		for(int i=verbIndex; i<tokens.length; i++){
			if(i!=verbIndex){
				if(!"me".equals(tokens[i].toLowerCase())){
					if(answer.length()>0) answer.append(' ');
					if(Character.isLetterOrDigit(tokens[i].charAt(0))) answer.append(tokens[i].toLowerCase());
					if(answerPred.length()>0) answerPred.append(' ');
					if(Character.isLetterOrDigit(tokens[i].charAt(0))) answerPred.append(tokens[i].toLowerCase());
				}
			} else if(!Linguistics.tobe.contains(tokens[i])){
				answerPred.append(tokens[i].toLowerCase());
			}
		}
		interpretation.getFeatures().put(Linguistics.OBJ_ANSWER, answer.toString());
		interpretation.getFeatures().put(Linguistics.PRED_ANSWER, answerPred.toString());
		return interpretation;
	}

}
