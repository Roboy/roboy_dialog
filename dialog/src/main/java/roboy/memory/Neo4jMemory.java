package roboy.memory;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.memory.nodes.MemoryNodeModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Implements the high-level-querying tasks to the Memory services.
 */
public class Neo4jMemory implements Neo4jMemoryInterface {

    private Gson gson = new Gson();
    private final static Logger logger = LogManager.getLogger();

    public Neo4jMemory (){
        logger.info("Using Neo4jMemory");
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
        String response = Neo4jMemoryOperations.update(node.toJSON());
        return response != null && (response.contains("OK"));
    }

    /**
     * This query retrieves a a single node by its ID.
     *
     * @param  id the ID of requested
     * @return Node representation of the result.
     */
    public String getById(int id) throws InterruptedException, IOException
    {
        String result = Neo4jMemoryOperations.get("{'id':"+id+"}");
        if(result == null || result.contains("FAIL")) return null;

        return result;
    }

    /**
     * This is a classical database query which finds all matching nodes.
     *
     * @param  query the ID of requested
     * @return Array of  IDs (all nodes which correspond to the pattern).
     */
    public ArrayList<Integer> getByQuery(MemoryNodeModel query) throws InterruptedException, IOException
    {
        String result = Neo4jMemoryOperations.get(query.toJSON());
        if(result == null || result.contains("FAIL")) return null;
        Type type = new TypeToken<HashMap<String, List<Integer>>>() {}.getType();
        HashMap<String, ArrayList<Integer>> list = gson.fromJson(result, type);
        return list.get("id");
    }

    public int create(MemoryNodeModel query) throws InterruptedException, IOException
    {
        String result = Neo4jMemoryOperations.create(query.toJSON());
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
        query.setStripQuery(true);
        String response = Neo4jMemoryOperations.delete(query.toJSON());
        return response != null && response.contains("OK");
    }
}