package roboy.logic;

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
    final static List<String> positiveTokens = Arrays.asList("yes", "yep", "yeah", "ok", "sure", "do",
            "of course", "go ahead", "okay");
    final static List<String> negativeTokens = Arrays.asList("no", "nope", "later", "not", "dont", "do not");
    //TODO: add uncertaintyTokens + the rest of akinator answers
    //maybe search existing library

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
        }
        if (positive && !negative) {
            return Linguistics.UtteranceSentiment.POSITIVE;
        } else if (!positive && negative) {
            return Linguistics.UtteranceSentiment.NEGATIVE;
        } else {
            return Linguistics.UtteranceSentiment.NEUTRAL;
        }
    }

    @Override
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

    @Override
    public String inferGame(Interpretation input, Map<String,List<String>> existingGamesMap){

        List<String> tokens = input.getTokens();
        if(tokens != null && !tokens.isEmpty()) {
            String desiredGame = null;
            for(String token : tokens){
                for(Map.Entry<String, List<String>> entry  : existingGamesMap.entrySet()){
                    if(entry.getValue().contains(token)){
                        desiredGame = entry.getKey();
                    }
                }
            }
            return desiredGame;
        }
        return null;
    }
}
