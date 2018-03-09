package roboy.memory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.ros.RosMainNode;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the high-level-querying tasks to the Memory services using RosMainNode.
 */
public class DummyMemory implements Neo4jMemoryInterface {
    private final static Logger logger = LogManager.getLogger();

    public DummyMemory() {
        logger.info("Using dummy memory");
    }

    /**
     * This function is a dummy to use without ROS connetion to Neo4jMemory
     *
     * @param node Node with a set ID, and other properties to be set or updated.
     * @return true for success, false for fail
     */
    @Override
    public boolean save(MemoryNodeModel node) throws InterruptedException, IOException
    {
        return true;
    }

    /**
     * This function is a dummy to use without ROS connetion to Neo4jMemory
     *
     * @param  id the ID of requested
     * @return Node representation of the result.
     */
    public String getById(int id) throws InterruptedException, IOException
    {
        JsonObject dummy = new JsonObject();
        dummy.addProperty("id", "9");
        return dummy.toString();
    }

    /**
     *This function is a dummy to use without ROS connection to Neo4jMemory
     *
     * @param  query the ID of requested
     * @return Array of  IDs (all nodes which correspond to the pattern).
     */
    public ArrayList<Integer> getByQuery(MemoryNodeModel query) throws InterruptedException, IOException
    {
       return new ArrayList<>();
    }

    public int create(MemoryNodeModel query) throws InterruptedException, IOException {
        return 0;
    }

    /**
     * This function is a dummy to use without ROS connetion to Neo4jMemory
     *
     * @param query StrippedQuery avoids accidentally deleting other fields than intended.
     */
    public boolean remove(MemoryNodeModel query) throws InterruptedException, IOException
    {
        return true;
    }


}
