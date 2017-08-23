package roboy.memory;

import roboy.ros.RosMainNode;

import java.io.IOException;
import java.util.*;

public class Neo4jMemory implements Memory<String>
{
    private RosMainNode rosMainNode;

    public Neo4jMemory (RosMainNode node){
        this.rosMainNode = node;
    }

    @Override
    public boolean save(String query) throws InterruptedException, IOException
    {
        return rosMainNode.CreateMemoryQuery(query)!=null;
    }

    @Override
    public List<String> retrieve(String query) throws InterruptedException, IOException
    {
        // TODO return the result object - needs to be implemented first
        boolean res = rosMainNode.CreateMemoryQuery(query)!=null;
        return new ArrayList<String>();
    }
}
