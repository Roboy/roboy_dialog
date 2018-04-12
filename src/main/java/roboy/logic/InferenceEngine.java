package roboy.logic;

import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jLabels;
import roboy.memory.Neo4jProperties;
import roboy.memory.Neo4jRelationships;

import java.util.ArrayList;
import java.util.HashMap;

public interface InferenceEngine {

	// There lies the future of Roboy and the brilliance of his logic
    // TODO: Implement

    /**
     * Basic inference method
     * Infers the property inforamtion with regard to the requested keys,
     * tries to extract and ground the information from the available Interpretation
     *
     * @param keys
     * @return HashMap containing properties and inferred data/null if NA
     */
    HashMap<Neo4jProperties, String> inferProperties(ArrayList<Neo4jProperties> keys, Interpretation input);

    /**
     * Basic inference method
     * Infers the relationship inforamtion with regard to the requested keys,
     * tries to extract and ground the information from the available Interpretation
     *
     * @param keys
     * @return HashMap containing relationships and inferred data/null if NA
     */
    HashMap<Neo4jRelationships, String> inferRelationships(ArrayList<Neo4jRelationships> keys, Interpretation input);

    /**
     * Basic inference method
     * Infers the label inforamtion with regard to the requested keys,
     * tries to extract and ground the information from the available Interpretation
     *
     * @param keys
     * @return HashMap containing labels and inferred data/null if NA
     */
    HashMap<Neo4jLabels, String> inferLabel(ArrayList<Neo4jLabels> keys, Interpretation input);

}
