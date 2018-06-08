package roboy.linguistics.sentenceanalysis;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SemanticRole;

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
public class AnswerAnalyzer implements Analyzer {

	private final static Logger logger = LogManager.getLogger();
	
	@Override
	public Interpretation analyze(Interpretation interpretation) {
		
		// case pas object answer
		Map<SemanticRole, String> pas = interpretation.getPas();
		if(pas.containsKey(SemanticRole.PREDICATE)
				&& pas.containsKey(SemanticRole.LOCATION)){
			String pp = pas.get(SemanticRole.LOCATION);
			String pred = pas.get(SemanticRole.PREDICATE);
			if(Linguistics.tobe.contains(pred)){
				pred = "";
			} else {
				pred = pred + " ";
			}
			if(pp.contains(" ")){
				pp = pp.substring(pp.indexOf(" ") + 1);
			}
			interpretation.setObjAnswer(pp.toLowerCase());
			interpretation.setPredAnswer(pred + pp.toLowerCase());
			return interpretation;

		} else if(pas.containsKey(SemanticRole.PREDICATE)
				&& pas.containsKey(SemanticRole.PATIENT)){
			String pred = pas.get(SemanticRole.PREDICATE).toLowerCase();
			if(Linguistics.tobe.contains(pred)){
				pred = "";
			} else {
				pred = pred + " ";
			}
			String pat = pas.get(SemanticRole.PATIENT).toLowerCase();
			interpretation.setObjAnswer(pat);
			interpretation.setPredAnswer(pred + pat);
			return interpretation;
		}
		
		// check for last verb
		int verbIndex = -1;
		List<String> tokens = interpretation.getTokens();
		String[] pos = interpretation.getPosTags();

		if (pos != null) {
			for(int i=0; i < pos.length; i++) {
				if(pos[i] != null && pos[i].startsWith("V")) {
					verbIndex = i;
				}
			}
		} else {
			logger.warn("POSTAGS missing but AnswerAnalyzer is used!");
		}

//		System.out.println("Verbindex="+verbIndex);
		
		// case one term answer
        if (tokens != null && !tokens.isEmpty()) {
            if(verbIndex == -1){
                if (tokens.size() == 1) {
                    interpretation.setObjAnswer(tokens.get(0));
                    interpretation.setPredAnswer(tokens.get(0));
                    return interpretation;
                }
            }
		
            // case pas failed object answer
            StringBuilder answer = new StringBuilder("");
            StringBuilder answerPred = new StringBuilder("");
            for(int i = verbIndex; i < tokens.size(); i++){
                if(i != verbIndex){
                    if(tokens.get(i) != null && !"me".equals(tokens.get(i).toLowerCase())){
                        if(answer.length() > 0) answer.append(' ');
                        if(Character.isLetterOrDigit(tokens.get(i).charAt(0))) answer.append(tokens.get(i).toLowerCase());
                        if(answerPred.length()>0) answerPred.append(' ');
                        if(Character.isLetterOrDigit(tokens.get(i).charAt(0))) answerPred.append(tokens.get(i).toLowerCase());
                    }
                } else if(verbIndex != -1 && !Linguistics.tobe.contains(tokens.get(i))){
                    answerPred.append(tokens.get(i).toLowerCase());
                }
            }
            interpretation.setObjAnswer(answer.toString());
            interpretation.setPredAnswer(answerPred.toString());
        } else {
		    logger.warn("TOKENS missing but AnswerAnalyzer is used!");
		}

		return interpretation;
	}
}
