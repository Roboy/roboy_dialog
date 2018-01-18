package roboy.dialog.personality.states;

import org.junit.Test;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.WorkingMemory;

import java.util.List;

/**
 * Created by roboy on 7/5/17.
 */
public class LocationDBpediaStateTest {
    @Test
    public void testCity() {
        WorkingMemory m = WorkingMemory.getInstance();
        m.save(new Triple("is", "name", "Laura"));
        m.save(new Triple("is from", "Laura", "Nuremberg"));
        LocationDBpedia locationDBpedia = new LocationDBpedia();
        List<Interpretation> result = locationDBpedia.act();
        System.out.print(result);
    }

    @Test
    public void testCountry() {
        WorkingMemory m = WorkingMemory.getInstance();
        m.save(new Triple("is", "name", "Laura"));
        m.save(new Triple("is from", "Laura", "Germany"));
        LocationDBpedia locationDBpedia = new LocationDBpedia();
        List<Interpretation> result = locationDBpedia.act();
        System.out.print(result);
    }
}
