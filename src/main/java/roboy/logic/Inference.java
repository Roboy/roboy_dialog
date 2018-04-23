package roboy.logic;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jLabel;
import roboy.memory.Neo4jProperty;
import roboy.memory.Neo4jRelationship;

import java.util.*;

public class Inference implements InferenceEngine {
    final static List<String> positiveTokens = Arrays.asList("yes", "yep", "sure", "yeah", "ok");
    final static List<String> negativeTokens = Arrays.asList("no", "nope");

    private String inferName(Interpretation input) {
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
    public HashMap<Neo4jProperty, String> inferProperties(ArrayList<Neo4jProperty> keys, Interpretation input) {

            HashMap<Neo4jProperty, String> inferenceResult = new HashMap<>();
            for (Neo4jProperty key : keys) {
                inferenceResult.put(key, null);
            }

            if (keys.contains(Neo4jProperty.name)) {
                inferenceResult.put(Neo4jProperty.name, inferProperty(Neo4jProperty.name, input));
            }

            return inferenceResult;
    }

    @Override
    public String inferProperty(Neo4jProperty key, Interpretation input) {
        switch (key) {
            case name:
                return inferName(input);
            default:
                return null;
        }
    }

    @Override
    public HashMap<Neo4jRelationship, String> inferRelationships(ArrayList<Neo4jRelationship> keys, Interpretation input) {
        return null;
    }

    @Override
    public String inferRelationship(Neo4jRelationship key, Interpretation input) {
        return null;
    }

    @Override
    public HashMap<Neo4jLabel, String> inferLabels(ArrayList<Neo4jLabel> keys, Interpretation input) {
        return null;
    }

    @Override
    public String inferLabel(Neo4jLabel key, Interpretation input) {
        return null;
    }

    @Override
    public Linguistics.UtteranceSentiment inferSentiment(Interpretation input) {
        // Brute-force implementation
        boolean positive = false;
        boolean negative = false;
        for (String token : positiveTokens) {
            if (input.getFeatures().get(Linguistics.SENTENCE).toString().contains(token)) {
                positive = true;
                break;
            }
        }
        for (String token : negativeTokens) {
            if (input.getFeatures().get(Linguistics.SENTENCE).toString().contains(token)) {
                negative = true;
                break;
            }
        }
        if (positive && !negative) {
            return Linguistics.UtteranceSentiment.POSITIVE;
        } else if (!positive && negative) {
            return Linguistics.UtteranceSentiment.NEGATIVE;
        } else {
            return Linguistics.UtteranceSentiment.NEUTRAL;
        }
    }
}
