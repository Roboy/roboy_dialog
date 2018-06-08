package roboy.memory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import junit.framework.TestCase;
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

    public void testCreateNode() {
        int id = gson.fromJson(Neo4jMemoryOperations.create(LUKAS), JsonObject.class).get("id").getAsInt();
        assertTrue(id > 0);
    }

     public void testUpdateNode() {
        int id = gson.fromJson(Neo4jMemoryOperations.create(LUKAS), JsonObject.class).get("id").getAsInt();
        int idRob = gson.fromJson(Neo4jMemoryOperations.create(ROBOY), JsonObject.class).get("id").getAsInt();
        String updateResponse = Neo4jMemoryOperations.update("{'type':'node','id':" + id + ",'properties':{'surname':'Ki', 'xyz':'abc'}, 'relationships':{'FRIEND_OF':[" + idRob + "]}}");
        assertTrue(updateResponse.contains("properties updated\":true"));
        assertTrue(updateResponse.contains("relationships created\":1}\"}"));
    }


    public void testGetNode() {
        Create create = gson.fromJson("{'label':'Person','properties':{'name':'Lucas_The_Int_Tester', 'sex':'male', 'timestamp_test':'" + timestamp + "'}}", Create.class);
        int id = gson.fromJson(Neo4jMemoryOperations.create(LUKAS), JsonObject.class).get("id").getAsInt();
        Get get = new Get();
        get.setProperties(create.getProperties());
        get.setLabel(create.getLabel());
        JsonObject node = gson.fromJson(Neo4j.getNode(get), JsonObject.class);
        assertEquals(id, node.get("id").getAsJsonArray().get(0).getAsInt());
    }

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
        assertEquals(id, (int)node.getId());
        assertEquals(null, node.getProperties().get("sex"));
        assertEquals(null, node.getRelationships());
    }

    public void tearDown() throws Exception {
        Neo4j.run("MATCH (n{timestamp_test:'" + timestamp + "'}) DETACH DELETE n");
    }
}
