package roboy.logic;

import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jLabels;
import roboy.memory.Neo4jProperties;
import roboy.memory.Neo4jRelationships;

import java.util.HashMap;

public class Inference implements InferenceEngine {
    @Override
    public HashMap inferProperties(Neo4jProperties[] keys, Interpretation input) {
        return null;
    }

    @Override
    public HashMap inferRelationships(Neo4jRelationships[] keys, Interpretation input) {
        return null;
    }

    @Override
    public HashMap inferLabel(Neo4jLabels[] keys, Interpretation input) {
        return null;
    }

    public String inferName(Interpretation input) {

    }
}
