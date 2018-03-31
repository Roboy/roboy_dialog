package roboy.logic;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jLabels;
import roboy.memory.Neo4jProperties;
import roboy.memory.Neo4jRelationships;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Inference implements InferenceEngine {

    public String inferName(Interpretation input) {
        if (input.getSentenceType().compareTo(Linguistics.SENTENCE_TYPE.STATEMENT) == 0) {
            String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
            if (tokens.length == 1) {
                return tokens[0].replace("[", "").replace("]","").toLowerCase();
            } else {
                if (input.getFeatures().get(Linguistics.PARSER_RESULT).toString().equals("SUCCESS") &&
                        input.getFeatures().get(Linguistics.SEM_TRIPLE) != null) {
                    List<Triple> result = (List<Triple>) input.getFeatures().get(Linguistics.SEM_TRIPLE);
                    if (result.size() != 0) {
                        return result.get(0).object.toLowerCase();
                    } else {
                        if (input.getFeatures().get(Linguistics.OBJ_ANSWER) != null) {
                            String name = input.getFeatures().get(Linguistics.OBJ_ANSWER).toString().toLowerCase();
                            return !name.equals("") ? name : null;
                        }
                    }
                } else {
                    if (input.getFeatures().get(Linguistics.OBJ_ANSWER) != null) {
                        String name = input.getFeatures().get(Linguistics.OBJ_ANSWER).toString().toLowerCase();
                        return !name.equals("") ? name : null;
                    }
                }
            }
        }
        return null;
    }


    @Override
    public HashMap<Neo4jProperties, String> inferProperties(ArrayList<Neo4jProperties> keys, Interpretation input) {

            HashMap<Neo4jProperties, String> inferenceResult = new HashMap<>();
            for (Neo4jProperties key : keys) {
                inferenceResult.put(key, null);
            }

            if (keys.contains(Neo4jProperties.name)) {
                inferenceResult.put(Neo4jProperties.name, inferName(input));
            }

            return inferenceResult;
    }

    @Override
    public HashMap<Neo4jRelationships, String> inferRelationships(ArrayList<Neo4jRelationships> keys, Interpretation input) {
        return null;
    }

    @Override
    public HashMap<Neo4jLabels, String> inferLabel(ArrayList<Neo4jLabels> keys, Interpretation input) {
        return null;
    }
}
