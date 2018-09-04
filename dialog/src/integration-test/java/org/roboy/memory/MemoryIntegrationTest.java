package org.roboy.memory;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboy.memory.interfaces.Neo4jMemoryOperations;
import org.roboy.memory.models.*;

import java.util.ArrayList;
import java.util.Date;

import org.roboy.memory.util.NativeNeo4j;
import org.roboy.memory.util.Neo4j;
import org.roboy.ontology.Neo4jLabel;
import org.roboy.ontology.Neo4jProperty;
import org.roboy.ontology.Neo4jRelationship;

/**
 * Basically this class is a mirror copy of Memory's Neo4JTest.
 * The exact same tests were used, only that instead of calling the functions in memory (few modifications), we call them via Neo4jMemoryOperations.
 */

public class MemoryIntegrationTest extends TestCase {

    private long timestamp;
    private static Neo4jMemory memory = new Neo4jMemory();
    private MemoryNodeModel LUKAS;
    private MemoryNodeModel TOBY;
    private MemoryNodeModel ROBOY;

    @Before
    public void setUp() {
        timestamp = new Date().getTime();

        LUKAS = new MemoryNodeModel(memory);
        LUKAS.setLabel(Neo4jLabel.Person);
        LUKAS.setProperties(Neo4jProperty.name, "Lucas_The_Int_Tester");
        LUKAS.setProperties(Neo4jProperty.sex, "male");
        LUKAS.setProperties(Neo4jProperty.timestamp, timestamp);

        TOBY  = new MemoryNodeModel(memory);
        TOBY.setLabel(Neo4jLabel.Person);
        TOBY.setProperties(Neo4jProperty.name, "Tobias_The_Friend");
        TOBY.setProperties(Neo4jProperty.sex, "male");
        TOBY.setProperties(Neo4jProperty.timestamp, timestamp);

        ROBOY = new MemoryNodeModel(memory);
        ROBOY.setLabel(Neo4jLabel.Robot);
        ROBOY.setProperties(Neo4jProperty.name, "Roboy_The_Test_Subject");
        ROBOY.setProperties(Neo4jProperty.timestamp, timestamp);
    }

    @Test
    public void testCreateNode() {
        int id = Neo4jMemoryOperations.create(LUKAS);
        assertTrue("Node should have an ID greater than 0. Possible Culprit: NEO4J not configured correctly, Debug MemoryOperations.create in Memory", id > 0);
    }

    @Test
     public void testUpdateNode() {
        int id = Neo4jMemoryOperations.create(LUKAS);
        int idRob = Neo4jMemoryOperations.create(ROBOY);
        //Update Sends back an item as an Answer, NOT a update object. This is where the test differs from NEO4jTest
        MemoryNodeModel node = new MemoryNodeModel(memory);
        node.setId(id);
        node.setLabel(Neo4jLabel.Person);
        node.setProperties(Neo4jProperty.full_name, "Ki");
        node.setProperties(Neo4jProperty.abilities, "xyz");
        node.setRelationships(Neo4jRelationship.FRIEND_OF, idRob);
        Boolean updateResponse = Neo4jMemoryOperations.update(node);

        assertTrue("Answer returns an error. Possible Culprit: Neo4j configured incorrectly, Debug MemoryOptions.update in Memory", updateResponse);
    }

    @Test
    public void testGetNode() {
        int id = Neo4jMemoryOperations.create(LUKAS);
        MemoryNodeModel get = new MemoryNodeModel(memory);
        get.setProperties(LUKAS.getProperties());
        get.setLabel(LUKAS.getLabel());
        ArrayList<MemoryNodeModel> node = NativeNeo4j.getNode(get, memory);
        assertEquals("ID from that is passed via the Create function does not match that of the Get Method. Debug Neo4j.getNode and MemoryOperations.get", id, node.get(0).getId());
    }

    @Test
    public void testRemove() {
        int id = Neo4jMemoryOperations.create(LUKAS);
        int idFriend = Neo4jMemoryOperations.create(TOBY);

        MemoryNodeModel update = new MemoryNodeModel(memory);
        update.setId(id);
        update.setRelationships(Neo4jRelationship.FRIEND_OF, idFriend);
        NativeNeo4j.updateNode(update);
        MemoryNodeModel remove = new MemoryNodeModel();
        remove.setId(id);
        remove.setProperties(Neo4jProperty.sex, "");
        remove.setRelationships(Neo4jRelationship.FRIEND_OF, idFriend);
        NativeNeo4j.remove(remove);
        MemoryNodeModel node = NativeNeo4j.getNodeById(id, memory);
        assertEquals("Remove somehow altered our node's ID. Possible Culprits: Neo4J.getNodeById fails, likely NEO4J misconfigured or not started", id, node.getId());
        assertNull("Remove does not remove items on the properties list. Debug MemoryOperations.remove", node.getProperties().get(Neo4jProperty.sex));
        assertNull("Remove does not remove relationships. Debug MemoryOperations.remove", node.getRelationships());
    }

    //Remove all things related to these tests
    @After
    public void tearDown() {
        Neo4j.run("MATCH (n{timestamp_test:'" + timestamp + "'}) DETACH DELETE n");
    }
}
