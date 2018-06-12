package roboy.memory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Test;
import org.roboy.memory.models.*;

import java.lang.reflect.Type;
import java.util.Date;

import junit.framework.TestCase;

//REMOVE THESE IMPORTS
import org.roboy.memory.models.*;
import org.roboy.memory.util.Neo4j;

import java.util.Date;

/**
 * Basically this class is a mirror copy of Memory's Neo4JTest.
 * The exact same tests were used, only that instead of calling the functions in memory (few modifications), we call them via Neo4jMemoryOperations.
 */

public class MemoryIntegrationTest extends TestCase {

    Gson gson = new Gson();
    long timestamp = new Date().getTime();

    final String LUKAS = "{'label':'Person','properties':{'name':'Lucas_The_Int_Tester', 'sex':'male', 'timestamp_test':'" + timestamp + "'}}";
    final String TOBY  = "{'label':'Person','properties':{'name':'Tobias_The_Friend', 'sex':'male', 'timestamp_test':'" + timestamp + "'}}";
    final String ROBOY = "{'label':'Robot','properties':{'name':'Roboy_The_Test_Subject', 'timestamp_test':'" + timestamp + "'}}";

    @Test
    public void testCreateNode() {
        int id = gson.fromJson(Neo4jMemoryOperations.create(LUKAS), JsonObject.class).get("id").getAsInt();
        assertTrue("Node should have an ID greater 0. Possible Culprit: NEO4J not configured correctly, Debug MemoryOperations.create in Memory", id > 0);
    }
    @Test
     public void testUpdateNode() {
        int id = gson.fromJson(Neo4jMemoryOperations.create(LUKAS), JsonObject.class).get("id").getAsInt();
        int idRob = gson.fromJson(Neo4jMemoryOperations.create(ROBOY), JsonObject.class).get("id").getAsInt();
        //Update Sends back an item as an Answer, NOT a update object. This is where the test differs from NEO4jTest
        String updateResponse = Neo4jMemoryOperations.update("{'type':'node','label':'utest','id':" + id + ",'properties':{'surname':'Ki', 'xyz':'abc'}, 'relationships':{'FRIEND_OF':[" + idRob + "]}}");

         assertTrue("Answer returns an error. Possible Culprit: Neo4j configured incorrectly, Debug MemoryOptions.update in Memory", updateResponse.contains("status\":\"OK\""));
         assertTrue("Answer Message Part One is incorrect: Properties Updated should be true. Possible Culprit: JSON formatting or NEO4J misconfigured", updateResponse.contains("properties updated\":true"));
         assertTrue("Answer Message Part Two is incorrect: Should create at least one relationship (One Ideally). Likely Possible: NEO4J misconfigured", !updateResponse.contains("relationships created\":0}\"}"));
//         assertTrue("Answer Message Part Two is incorrect: Should only create one relationship", updateResponse.contains("relationships created\":1}\"}"));
    }
    @Test
    public void testGetNode() {
        Create create = gson.fromJson("{'label':'Person','properties':{'name':'Lucas_The_Int_Tester', 'sex':'male', 'timestamp_test':'" + timestamp + "'}}", Create.class);
        int id = gson.fromJson(Neo4jMemoryOperations.create(LUKAS), JsonObject.class).get("id").getAsInt();
        Get get = new Get();
        get.setProperties(create.getProperties());
        get.setLabel(create.getLabel());
        JsonObject node = gson.fromJson(Neo4j.getNode(get), JsonObject.class);
        assertEquals("ID from that is passed via the Create function does not match that of the Get Method. Debug Neo4j.getNode and MemoryOperations.get", id, node.get("id").getAsJsonArray().get(0).getAsInt());
    }
    @Test
    public void testRemove() {
        Create create = gson.fromJson("{'label':'Person','properties':{'name':'Lucas_The_Int_Tester', 'sex':'male', 'timestamp_test':'" + timestamp + "'}}", Create.class);
        int id = gson.fromJson(Neo4j.createNode(create), JsonObject.class).get("id").getAsInt();
        Create createFriend = gson.fromJson("{'label':'Person','properties':{'name':'Tobias_The_Friend', 'sex':'male', 'timestamp_test':'" + timestamp + "'}}", Create.class);
        int idFriend = gson.fromJson(Neo4j.createNode(createFriend), JsonObject.class).get("id").getAsInt();
        Update update = gson.fromJson("{'type':'node','id':" + id + ", 'relationships':{'FRIEND_OF':[" + idFriend + "]}}", Update.class);
        Neo4j.updateNode(update);
        Remove remove = gson.fromJson("{'type':'node','id':" + id + ",'properties_list': ['sex'], 'relationships':{'FRIEND_OF':[" + idFriend + "]}}", Remove.class);
        Neo4j.remove(remove);
        Node node = gson.fromJson(Neo4j.getNodeById(id), Node.class);
        assertEquals("Remove somehow altered our node's ID. Possible Culprits: Neo4J.getNodeById fails, likely NEO4J misconfigured or not started", id, (int)node.getId());
        assertEquals("Remove does not remove items on the properties list. Debug MemoryOperations.remove", null, node.getProperties().get("sex"));
        assertEquals("Remove does not remove relationships. Debug MemoryOperations.remove", null, node.getRelationships());
    }

    //Remove all things related to these tests
    @After
    public void tearDown() throws Exception {
        Neo4j.run("MATCH (n{timestamp_test:'" + timestamp + "'}) DETACH DELETE n");
    }
}
