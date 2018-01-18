package roboy.memory.nodes;

import roboy.dialog.Config;
import roboy.memory.Neo4jMemory;
import roboy.memory.Neo4jRelationships;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Encapsulates a MemoryNodeModel and enables dialog states to easily store
 * and retrieve information about Roboy.
 */
public class Roboy {
    private MemoryNodeModel roboy;
    Neo4jMemory memory;
    // Memory is not queried in NOROS mode.
    private boolean memoryROS;

    /**
     * Initializer for the Roboy node
     */
    public Roboy() {
        this.roboy = new MemoryNodeModel(true);
        this.memory = Neo4jMemory.getInstance();
        this.memoryROS = Config.MEMORY;
        this.InitializeRoboy("roboy"); // May be "roboy junior"
    }

    /**
     * This method initializes the roboy property as a node that
     * is in sync with memory and represents the Roboy itself.
     *
     * If something  goes wrong during querying, Roboy stays empty
     * and soulless, and has to fallback
     */
    // TODO consider a fallback for the amnesia mode
    private void InitializeRoboy(String name) {
        roboy.setProperty("name", name);
        roboy.setLabel("Robot");

        if(memoryROS) {
            ArrayList<Integer> ids = new ArrayList<>();
            try {
                ids = memory.getByQuery(roboy);
            } catch (InterruptedException | IOException e) {
                System.out.println("Cannot retrieve or find Roboy in the Memory. Go the amnesia mode");
                e.printStackTrace();
            }
            // Pick first if matches found.
            if (ids != null && !ids.isEmpty()) {
                try {
                    this.roboy = memory.getById(ids.get(0));
                } catch (InterruptedException | IOException e) {
                    System.out.println("Unexpected memory error: provided ID not found upon querying. Go the amnesia mode");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Method to obtain the name of the Roboy node
     * @return String name - text containing the name as in the Memory
     */
    public String getName() {
        return (String) roboy.getProperty("name");
    }

    /**
     * Method to obtain the specific type relationships of the Roboy node
     * @return ArrayList<Integer> ids - list containing integer IDs of the nodes
     * related to the Roboy by specific relationship type as in the Memory
     */
    public ArrayList<Integer> getRelationships(Neo4jRelationships type) {
        return roboy.getRelationship(type.type);
    }

    /**
     * Adds a new relation to the Roboy node, updating memory.
     */
    public void addInformation(String relationship, String name) {
        if(!memoryROS) return;
        ArrayList<Integer> ids = new ArrayList<>();
        // First check if node with given name exists by a matching query.
        MemoryNodeModel relatedNode = new MemoryNodeModel(true);
        relatedNode.setProperty("name", name);
        //This adds a label type to the memory query depending on the relation.
        relatedNode.setLabel(determineNodeType(relationship));
        try {
            ids = memory.getByQuery(relatedNode);
        } catch (InterruptedException | IOException e) {
            System.out.println("Exception while querying memory by template.");
            e.printStackTrace();
        }
        // Pick first from list if multiple matches found.
        if(ids != null && !ids.isEmpty()) {
            roboy.setRelationship(relationship, ids.get(0));
        }
        // Create new node if match is not found.
        else {
            try {
                int id = memory.create(relatedNode);
                if(id != 0) { // 0 is default value, returned if Memory response was FAIL.
                    roboy.setRelationship(relationship, id);
                }
            } catch (InterruptedException | IOException e) {
                System.out.println("Unexpected memory error: creating node for new relation failed.");
                e.printStackTrace();
            }
        }
        //Update the Roboy node in memory.
        try{
            memory.save(roboy);
        } catch (InterruptedException | IOException e) {
            System.out.println("Unexpected memory error: updating person information failed.");
            e.printStackTrace();
        }
    }

    private String determineNodeType(String relationship) {
        // TODO expand list as new Node types are added.
        if(relationship.equals(Neo4jRelationships.HAS_HOBBY.type)) return "Hobby";
        if(relationship.equals(Neo4jRelationships.FROM.type)) return "Country";
        if(relationship.equals(Neo4jRelationships.WORK_FOR.type)) return "Organization";
        if(relationship.equals(Neo4jRelationships.STUDY_AT.type)) return "Organization";
        if(relationship.equals(Neo4jRelationships.OCCUPIED_AS.type)) return "Occupation";
        if(relationship.equals(Neo4jRelationships.OTHER.type)) return "Other";
        else return "";
    }

}
