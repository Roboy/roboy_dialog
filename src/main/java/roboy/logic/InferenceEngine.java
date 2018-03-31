package roboy.logic;

import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jLabels;
import roboy.memory.Neo4jProperties;
import roboy.memory.Neo4jRelationships;

import java.util.HashMap;

public interface InferenceEngine<V> {

	// There lies the future of Roboy and the brilliance of his logic
    // TODO: Implement

    /**
     * Basic inference method
     * Infers the property inforamtion with regard to the requested keys,
     * tries to extract and ground the information from the available Interpretation
     *
     * @param keys
     * @return V value
     */
    public HashMap<Neo4jProperties, V> inferProperties(Neo4jProperties[] keys, Interpretation input);

    /**
     * Basic inference method
     * Infers the relationship inforamtion with regard to the requested keys,
     * tries to extract and ground the information from the available Interpretation
     *
     * @param keys
     * @return V value
     */
    public HashMap<Neo4jProperties, V> inferRelationships(Neo4jRelationships[] keys, Interpretation input);

    /**
     * Basic inference method
     * Infers the label inforamtion with regard to the requested keys,
     * tries to extract and ground the information from the available Interpretation
     *
     * @param keys
     * @return V value
     */
    public HashMap<Neo4jProperties, V> inferLabel(Neo4jLabels[] keys, Interpretation input);

}
