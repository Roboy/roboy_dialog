package roboy.memory.nodes;

import roboy.dialog.Config;
import roboy.memory.Neo4jMemory;
import roboy.memory.Neo4jRelationships;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Encapsulates a MemoryNodeModel and enables dialog states to easily store
 * and retrieve information about its current conversation partner.
 */
public class Interlocutor {
    private MemoryNodeModel person;
    Neo4jMemory memory;
    public boolean FAMILIAR = false;
    // Memory is not queried in NOROS mode.
    private boolean memoryROS;

    public Interlocutor() {
        this.person = new MemoryNodeModel(true);
        this.memory = Neo4jMemory.getInstance();
        this.memoryROS = Config.MEMORY;
    }

    /**
     * After executing this method, the person field contains a node that
     * is in sync with memory and represents the interlocutor.
     *
     * Unless something goes wrong during querying, which would affect the
     * following communication severely.
     */
    public void addName(String name) {
        person.setProperty("name", name);
        person.setLabel("Person");

        if(memoryROS) {
            ArrayList<Integer> ids = new ArrayList<>();
            // Query memory for matching persons.
            try {
                ids = memory.getByQuery(person);
            } catch (InterruptedException | IOException e) {
                System.out.println("Exception while querying memory, assuming person unknown.");
                e.printStackTrace();
            }
            // Pick first if matches found.
            if (ids != null && !ids.isEmpty()) {
                //TODO Change from using first id to specifying if multiple matches are found.
                try {
                    this.person = memory.getById(ids.get(0));
                    FAMILIAR = true;
                } catch (InterruptedException | IOException e) {
                    System.out.println("Unexpected memory error: provided ID not found upon querying.");
                    e.printStackTrace();
                }
            }
            // Create new node if match is not found.
            else {
                try {
                    int id = memory.create(person);
                    // Need to retrieve the created node by the id returned by memory
                    person = memory.getById(id);
                } catch (InterruptedException | IOException e) {
                    System.out.println("Unexpected memory error: provided ID not found upon querying.");
                    e.printStackTrace();
                }
            }
        }
    }

    public String getName() {
        return (String) person.getProperty("name");
    }

    public boolean hasRelationship(Neo4jRelationships type) {
        return !(person.getRelationship(type.type) == null) && (!person.getRelationship(type.type).isEmpty());
    }

    public ArrayList<Integer> getRelationships(Neo4jRelationships type) {
        return person.getRelationship(type.type);
    }

    /**
     * Adds a new relation to the person node, updating memory.
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
            //TODO Change from using first id to specifying if multiple matches are found.
            person.setRelationship(relationship, ids.get(0));
        }
        // Create new node if match is not found.
        else {
            try {
                int id = memory.create(relatedNode);
                if(id != 0) { // 0 is default value, returned if Memory response was FAIL.
                    person.setRelationship(relationship, id);
                }
            } catch (InterruptedException | IOException e) {
                System.out.println("Unexpected memory error: creating node for new relation failed.");
                e.printStackTrace();
            }
        }
        //Update the person node in memory.
        try{
            memory.save(person);
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
