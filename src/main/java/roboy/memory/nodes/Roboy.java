package roboy.memory.nodes;

import com.google.gson.Gson;
import roboy.memory.Neo4jMemoryInterface;
import roboy.memory.Neo4jRelationships;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Encapsulates a MemoryNodeModel and enables dialog states to easily store
 * and retrieve information about Roboy.
 */
public class Roboy extends MemoryNodeModel{

    /**
     * Initializer for the Roboy node
     */
    public Roboy(Neo4jMemoryInterface memory) {
        super(true, memory);
        this.InitializeRoboy();
    }

    /**
     * This method initializes the roboy property as a node that
     * is in sync with memory and represents the Roboy itself.
     *
     * If something  goes wrong during querying, Roboy stays empty
     * and soulless, and has to fallback
     */
    // TODO consider a fallback for the amnesia mode
    private void InitializeRoboy() {
        setProperty("name", "roboy");
        setLabel("Robot");

        //
            ArrayList<Integer> ids = new ArrayList<>();
            try {
                ids = memory.getByQuery(this);
            } catch (InterruptedException | IOException e) {
                logger.error("Cannot retrieve or find Roboy in the Memory. Go the amnesia mode");
                logger.error(e.getMessage());
            }
            // Pick first if matches found.
            if (ids != null && !ids.isEmpty()) {
                try {
                    MemoryNodeModel node = fromJSON(memory.getById(ids.get(0)), new Gson());
                    setId(node.getId());
                    setRelationships(node.getRelationships() != null ? node.getRelationships() : new HashMap<>());
                    setProperties(node.getProperties() != null ? node.getProperties() : new HashMap<>());
                } catch (InterruptedException | IOException e) {
                    logger.error("Unexpected memory error: provided ID not found upon querying. Go the amnesia mode");
                    logger.error(e.getMessage());
                }
            }
        }


    /**
     * Method to obtain the name of the Roboy node
     * @return String name - text containing the name as in the Memory
     */
    public String getName() {
        return (String) getProperty("name");
    }

    /**
     * Method to obtain the specific type relationships of the Roboy node
     * @return ArrayList<Integer> ids - list containing integer IDs of the nodes
     * related to the Roboy by specific relationship type as in the Memory
     */
    public ArrayList<Integer> getRelationships(Neo4jRelationships type) {
        return getRelationship(type.type);
    }

    /**
     * Adds a new relation to the Roboy node, updating memory.
     */
    public void addInformation(String relationship, String name) {

        ArrayList<Integer> ids = new ArrayList<>();
        // First check if node with given name exists by a matching query.
        MemoryNodeModel relatedNode = new MemoryNodeModel(true,memory);
        relatedNode.setProperty("name", name);
        //This adds a label type to the memory query depending on the relation.
        relatedNode.setLabel(Neo4jRelationships.determineNodeType(relationship));
        try {
            ids = memory.getByQuery(relatedNode);
        } catch (InterruptedException | IOException e) {
            logger.error("Exception while querying memory by template.");
            logger.error(e.getMessage());
        }
        // Pick first from list if multiple matches found.
        if(ids != null && !ids.isEmpty()) {
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
                logger.error("Unexpected memory error: creating node for new relation failed.");
                logger.error(e.getMessage());
            }
        }
        //Update the Roboy node in memory.
        try{
            memory.save(this);
        } catch (InterruptedException | IOException e) {
            logger.error("Unexpected memory error: updating person information failed.");
            logger.error(e.getMessage());
        }
    }
}
