package roboy.memory;

import roboy.linguistics.Triple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV file memory. Can only be used for retrieving and not for storing.
 */
@Deprecated
public class PersistentKnowledge implements Memory<Triple>{

    private static PersistentKnowledge persistentKnowledge;
    private static List<Triple> memory;

    private PersistentKnowledge()
    {
        memory = new ArrayList<>(); //TODO: Refactor all that triples stuff to separate memory class
        ClassLoader cl = this.getClass().getClassLoader();
        File f = new File(cl.getResource("knowledgebase/triples.csv").getFile());
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            while(line!=null){
                String[] parts = line.split(",");
                memory.add(new Triple(parts[0], parts[1], parts[2]));
                line = br.readLine();
            }
            br.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static PersistentKnowledge getInstance()
    {
        if (memory == null)
        {
            persistentKnowledge = new PersistentKnowledge();
        }
        return persistentKnowledge;
    }

    public List<Triple> retrieve(Triple triple)
    {
        List<Triple> triples = new ArrayList<>();
        for(Triple t: memory){
            if(
                    (triple.predicate==null || triple.predicate.toLowerCase().equals(t.predicate)) &&
                            (triple.subject ==null || triple.subject.toLowerCase().equals(t.subject)) &&
                            (triple.object ==null || triple.object.toLowerCase().equals(t.object))
                    ){
                triples.add(t);
            }
        }
        return triples;
    }

    public boolean save(Triple triple)
    {
        return false; // TODO implement save method for persistant memory
    }
}
