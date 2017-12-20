package roboy.memory;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import roboy.dialog.Config;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.ros.RosMainNode;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Implements the high-level-querying tasks to the Memory services using RosMainNode.
 */
public class Neo4jMemory implements Memory<MemoryNodeModel>
{
    private static Neo4jMemory memory;
    private static RosMainNode rosMainNode;
    private Gson gson = new Gson();

    private Neo4jMemory (RosMainNode node){
        Neo4jMemory.rosMainNode = node;
    }

    public static Neo4jMemory getInstance(RosMainNode node)
    {
        if (memory==null) {
            memory = new Neo4jMemory(node);
        }
        return memory;

    }

    public static Neo4jMemory getInstance()
    {
        if (memory == null) {
            System.out.println("Memory wasn't initialized correctly. Use public static Neo4jMemory getInstance(RosMainNode node) instead.");
        }
        return memory;
    }

    /**
     * Updating information in the memory for an EXISTING node with known ID.
     *
     * @param node Node with a set ID, and other properties to be set or updated.
     * @return true for success, false for fail
     */
    @Override
    public boolean save(MemoryNodeModel node) throws InterruptedException, IOException
    {
        if(!Config.MEMORY) return false;
        String response = rosMainNode.UpdateMemoryQuery(node.toJSON(gson));
        if(response == null) return false;
        return(response.contains("OK"));
    }

    /**
     * This query retrieves a a single node by its ID.
     *
     * @param  id the ID of requested
     * @return Node representation of the result.
     */
    public MemoryNodeModel getById(int id) throws InterruptedException, IOException
    {
        if(!Config.MEMORY) return new MemoryNodeModel();
        String result = rosMainNode.GetMemoryQuery("{'id':"+id+"}");
        if(result == null || result.contains("FAIL")) return null;
        return gson.fromJson(result, MemoryNodeModel.class);
    }

    /**
     * This is a classical database query which finds all matching nodes.
     *
     * @param  query the ID of requested
     * @return Array of  IDs (all nodes which correspond to the pattern).
     */
    public ArrayList<Integer> getByQuery(MemoryNodeModel query) throws InterruptedException, IOException
    {
        if(!Config.MEMORY) return new ArrayList<>();
        String result = rosMainNode.GetMemoryQuery(query.toJSON(gson));
        if(result == null || result.contains("FAIL")) return null;
        Type type = new TypeToken<HashMap<String, List<Integer>>>() {}.getType();
        HashMap<String, ArrayList<Integer>> list = gson.fromJson(result, type);
        return list.get("id");
    }

    public int create(MemoryNodeModel query) throws InterruptedException, IOException
    {
        if(!Config.MEMORY) return 0;
        String result = rosMainNode.CreateMemoryQuery(query.toJSON(gson));
        // Handle possible Memory error message.
        if(result == null || result.contains("FAIL")) return 0;
        Type type = new TypeToken<Map<String,Integer>>() {}.getType();
        Map<String,Integer> list = gson.fromJson(result, type);
        return list.get("id");
    }

    /**
     * IF ONLY THE ID IS SET, THE NODE IN MEMORY WILL BE DELETED ENTIRELY.
     * Otherwise, the properties present in the query will be deleted.
     *
     * @param query StrippedQuery avoids accidentally deleting other fields than intended.
     */
    public boolean remove(MemoryNodeModel query) throws InterruptedException, IOException
    {
        if(!Config.MEMORY) return false;
        //Remove all fields which were not explicitly set, for safety.
        query.setStripQuery(true);
        String response = rosMainNode.DeleteMemoryQuery(query.toJSON(gson));
        return response == null ? false : response.contains("OK");
    }

    /**
     * //TODO Deprecated due to interface incompatibility, use getById or getByMatch
     *
     * @param query a GetByIDQuery instance
     * @return Array with a single node
     */
    @Override
    @Deprecated
    public List<MemoryNodeModel> retrieve(MemoryNodeModel query) throws InterruptedException, IOException
    {
        return null;
    }

}
