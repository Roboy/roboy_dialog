package roboy.memory.nodes;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.memory.*;
import roboy.util.UzupisIntents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Encapsulates a MemoryNodeModel and enables dialog states to easily store
 * and retrieve information about its current conversation partner.
 */
public class Interlocutor extends MemoryNodeModel {
    public boolean FAMILIAR = false;
    private HashMap<UzupisIntents,String> uzupisInfo = new HashMap<>();

    private final Logger LOGGER = LogManager.getLogger();

    public Interlocutor(Neo4jMemoryInterface memory) {
        super(memory);
    }

    /**
     * After executing this method, the person field contains a node that
     * is in sync with memory and represents the interlocutor.
     *
     * Unless something goes wrong during querying, which would affect the
     * following communication severely.
     */
    public void addName(String name) {
        setProperty(Neo4jProperty.name, name);
        setLabel(Neo4jLabel.Person);

            ArrayList<Integer> ids = new ArrayList<>();
            // Query memory for matching persons.
            try {
                ids = memory.getByQuery(this);
            } catch (InterruptedException | IOException e) {
                LOGGER.info("Exception while querying memory, assuming person unknown.");
                e.printStackTrace();
            }
            // Pick first if matches found.
            if (ids != null && !ids.isEmpty()) {
                //TODO Change from using first id to specifying if multiple matches are found.
                try {
                    MemoryNodeModel node = fromJSON(memory.getById(ids.get(0)), new Gson());
                    setId(node.getId());
                    setRelationships(node.getRelationships() != null ? node.getRelationships() : new HashMap<>());
                    setProperties(node.getProperties() != null ? node.getProperties() : new HashMap<>());
                    FAMILIAR = true;
                } catch (InterruptedException | IOException e) {
                    LOGGER.warn("Unexpected memory error: provided ID not found upon querying.");
                    e.printStackTrace();
                }
            }
            // Create new node if match is not found.
            else {
                if(!(memory instanceof DummyMemory)) {
                    try {
                        int id = memory.create(this);
                        // Need to retrieve the created node by the id returned by memory
                        MemoryNodeModel node = fromJSON(memory.getById(id), new Gson());
                        setId(node.getId());
                        setRelationships(node.getRelationships() != null ? node.getRelationships() : new HashMap<>());
                        setProperties(node.getProperties() != null ? node.getProperties() : new HashMap<>());
                        FAMILIAR = false;
                    } catch (InterruptedException | IOException e) {
                        LOGGER.warn("Unexpected memory error: provided ID not found upon querying.");
                        e.printStackTrace();
                    }
                }
            }
        }


    public String getName() {
        return (String) getProperty(Neo4jProperty.name);
    }

    public boolean hasRelationship(Neo4jRelationship relationship) {
        return !(getRelationship(relationship) == null) && (!getRelationship(relationship).isEmpty());
    }

    public ArrayList<Integer> getRelationships(Neo4jRelationship relationship) {
        return getRelationship(relationship);
    }


    public void saveUzupisProperty(UzupisIntents intent, String value) {
        uzupisInfo.put(intent, value);
    }

    public HashMap<UzupisIntents, String> getUzupisInfo() {
        return uzupisInfo;
    }

    /**
     * Adds a new relation to the person node, updating memory.
     */
    public void addInformation(Neo4jRelationship relationship, String name) {
        ArrayList<Integer> ids = new ArrayList<>();
        // First check if node with given name exists by a matching query.
        MemoryNodeModel relatedNode = new MemoryNodeModel(true, memory);
        relatedNode.setProperty(Neo4jProperty.name, name);
        //This adds a label type to the memory query depending on the relation.
        relatedNode.setLabel(Neo4jRelationship.determineNodeType(relationship));
        try {
            ids = memory.getByQuery(relatedNode);
        } catch (InterruptedException | IOException e) {
            LOGGER.error("Exception while querying memory by template.");
            e.printStackTrace();
        }
        // Pick first from list if multiple matches found.
        if(ids != null && !ids.isEmpty()) {
            //TODO Change from using first id to specifying if multiple matches are found.
            setRelationship(relationship, ids.get(0));
        }
        // Create new node if match is not found.
        else {
            try {
                int id = memory.create(relatedNode);
                if(id != 0) { // 0 is default value, returned if Memory response was FAIL.
                    setRelationship(relationship, id);
                }
            } catch (InterruptedException | IOException e) {
                LOGGER.error("Unexpected memory error: creating node for new relation failed.");
                e.printStackTrace();
            }
        }
        //Update the person node in memory.
        try{
            memory.save(this);
        } catch (InterruptedException | IOException e) {
            LOGGER.error("Unexpected memory error: updating person information failed.");
            e.printStackTrace();
        }
    }

    public enum RelationshipAvailability {
        ALL_AVAILABLE, SOME_AVAILABLE, NONE_AVAILABLE
    }

    /**
     * Checks if predicates from the input array are available for this interlocutor.
     * @param rels array of predicates to check
     * @return one of three: all, some or none available
     */
    public RelationshipAvailability checkRelationshipAvailability(Neo4jRelationship[] rels) {
        boolean atLeastOneAvailable = false;
        boolean allAvailable = true;

        for (Neo4jRelationship predicate : rels) {
            if (this.hasRelationship(predicate)) {
                atLeastOneAvailable = true;
            } else {
                allAvailable = false;
            }
        }
        if (allAvailable) return RelationshipAvailability.ALL_AVAILABLE;
        if (atLeastOneAvailable) return RelationshipAvailability.SOME_AVAILABLE;
        return RelationshipAvailability.NONE_AVAILABLE;
    }

    public HashMap<Boolean, ArrayList<Neo4jRelationship>> getPurityRelationships(Neo4jRelationship[] predicates) {
        HashMap<Boolean, ArrayList<Neo4jRelationship>> pureImpureValues = new HashMap<>();
        pureImpureValues.put(false, new ArrayList<>());
        pureImpureValues.put(true, new ArrayList<>());

        for (Neo4jRelationship predicate : predicates) {
            pureImpureValues.get(this.hasRelationship(predicate)).add(predicate);
        }

        return pureImpureValues;
    }
}