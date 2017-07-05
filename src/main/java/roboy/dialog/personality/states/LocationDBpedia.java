package roboy.dialog.personality.states;

import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.WorkingMemory;
import roboy.util.Lists;

import java.util.List;

/**
 * Created by roboy on 7/5/17.
 */
public class LocationDBpedia extends AbstractBooleanState {

    public LocationDBpedia()
    {

    }

    @Override
    public List<Interpretation> act() {
        List<Triple> names = WorkingMemory.getInstance().retrieve(new Triple("is", "name", null));
        if (names.isEmpty()) {
            return Lists.interpretationList();
        }
        String name = names.get(0).patiens;
        List<Triple> locations = WorkingMemory.getInstance().retrieve(new Triple("is from", name, null));
        if (locations.isEmpty()) {
            return Lists.interpretationList();
        }
        String location = locations.get(0).patiens;


    }

    @Override
    public Reaction react(Interpretation input) {
        return new Reaction(this.success);
    }

    }
}
