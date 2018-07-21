package roboy.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacpp.presets.opencv_core;
import org.json.JSONObject;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jLabel;
import roboy.memory.Neo4jProperty;
import roboy.memory.Neo4jRelationship;
import roboy.util.RandomList;

import java.util.*;

public class Inference implements InferenceEngine {
    final Logger LOGGER = LogManager.getLogger();

    final static List<String> positiveTokens = Arrays.asList("yes", "yep", "yeah", "ok", "sure", "course", "go ahead", "okay", "totally", "surely", "positive", "ready");
    final static List<String> negativeTokens = Arrays.asList("no", "nope", "later", "not", "dont", "negative");
    final static List<String> uncertaintyTokens = Arrays.asList("guess", "probably", "could", "likely", "know", "not sure", "idea", "perhaps", "depends", "maybe", "think", "might");


    private String inferName(Interpretation input) {
        if (input.getSentenceType().compareTo(Linguistics.SentenceType.STATEMENT) == 0) {
            List<String> tokens = input.getTokens();
            if (tokens != null && !tokens.isEmpty()) {
                if (tokens.size() == 1) {
                    return tokens.get(0).toLowerCase();
                } else {
                    if (input.getParsingOutcome() == Linguistics.ParsingOutcome.SUCCESS &&
                            input.getSemTriples() != null) {
                        List<Triple> result = input.getSemTriples();
                        if (result.size() > 0) {
                            return result.get(0).object.toLowerCase();
                        } else {
                            if (input.getObjAnswer() != null) {
                                String name = input.getObjAnswer();
                                return !name.equals("") ? name : null;
                            }
                        }
                    } else {
                        if (input.getObjAnswer() != null) {
                            String name = input.getObjAnswer();
                            return !name.equals("") ? name : null;
                        }
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
        // TODO we may need to do different inference
        String result = null;
        if (input.getSentenceType() != null && input.getSentenceType().equals(Linguistics.SentenceType.STATEMENT)) {
            List<String> tokens = input.getTokens();
            if (tokens != null && tokens.size() == 1) {
                result = tokens.get(0).toLowerCase();
                LOGGER.info("Retrieved only one token: " + result);
            } else {
                if (input.getParsingOutcome() == Linguistics.ParsingOutcome.SUCCESS &&
                        input.getSemTriples() != null &&
                        input.getSemTriples().size() > 0) {
                    List<Triple> sem_triple = input.getSemTriples();
                    LOGGER.info("Semantic parsing is successful and semantic triple exists");
                    if (sem_triple.get(0).predicate.contains(key.type)) {
                        LOGGER.info(" -> Semantic predicate " + key.type + " exits");
                        result = sem_triple.get(0).object.toLowerCase();
                        LOGGER.info("Retrieved object " + result);
                    } else {
                        LOGGER.warn("Semantic predicate " + key.type + " does not exit");
                    }
                } else {
                    LOGGER.warn("Semantic parsing failed or semantic triple does not exist");
                    if (input.getObjAnswer() != null) {
                        LOGGER.info("OBJ_ANSWER exits");
                        result = input.getObjAnswer().toLowerCase();
                        if (!result.equals("")) {
                            LOGGER.info("Retrieved OBJ_ANSWER result " + result);
                        } else {
                            LOGGER.warn("OBJ_ANSWER result is empty");
                        }
                    } else {
                        LOGGER.warn("OBJ_ANSWER does not exit");
                    }
                }
            }
        } else {
            LOGGER.warn(" -> The sentence type is NOT a STATEMENT");
        }
        return result;
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
        boolean uncertain = false;
        if(input.getSentence().contains("no idea") || input.getSentence().contains("do not know") || input.getSentence().contains("maybe")){
            return Linguistics.UtteranceSentiment.MAYBE;
        }
        List<String> tokens = input.getTokens();
        if (tokens != null && !tokens.isEmpty()) {
            for (String token : positiveTokens) {
                if (tokens.contains(token)) {
                    positive = true;
                    break;
                }
            }
            for (String token : negativeTokens) {
                if (tokens.contains(token)) {
                    negative = true;
                    break;
                }
            }
            for(String token : uncertaintyTokens){
                if(tokens.contains(token)){
                    uncertain = true;
                    break;
                }
            }
        }
        if (positive && !negative && uncertain) {
            return Linguistics.UtteranceSentiment.UNCERTAIN_POS;
        } else if (!positive && negative && uncertain) {
            return Linguistics.UtteranceSentiment.UNCERTAIN_NEG;
        } else if (positive && !negative) {
            return Linguistics.UtteranceSentiment.POSITIVE;
        } else if (!positive && negative) {
            return Linguistics.UtteranceSentiment.NEGATIVE;
        } else {
            return Linguistics.UtteranceSentiment.NEUTRAL;
        }
    }

    public List<String> inferSnapchatFilter(Interpretation input, Map<String,List<String>> existingFilterMap){

        List<String> tokens = input.getTokens();
        if(tokens != null && !tokens.isEmpty()) {
            List<String> desiredFilters = new ArrayList<String>();
            for(String token : tokens){
                for(Map.Entry<String, List<String>> entry  : existingFilterMap.entrySet()){
                    if(entry.getValue().contains(token)){
                        desiredFilters.add(entry.getKey());
                    }
                }
            }
            return desiredFilters;
        }
        return null;
    }

}
