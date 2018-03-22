package roboy.dialog.personality.states;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.DBpediaMemory;
import roboy.memory.WorkingMemory;
import roboy.util.Concept;
import roboy.util.Lists;
import roboy.util.Relation;

import java.util.*;

/**
 * Created by roboy on 7/5/17.
 */
public class LocationDBpedia extends AbstractBooleanState {


    @Override
    public List<Interpretation> act() {
        Random generator = new Random();
        List<Triple> names = WorkingMemory.getInstance().retrieve(new Triple("is", "name", null));
        if (names.isEmpty()) {
            return Lists.interpretationList();
        }
        String name = names.get(0).object;
        List<Triple> locations = WorkingMemory.getInstance().retrieve(new Triple("is from", name, null));
        if (locations.isEmpty()) {
            return Lists.interpretationList();
        }
        String location = locations.get(0).object;
        DBpediaMemory memory = DBpediaMemory.getInstance();
        Map<String, Relation> queries = new HashMap<>();new ArrayList<Relation>();
        queries.put(location + " has population of ", new Relation(new Concept(location), "populationTotal", new Concept()));
//        queries.put("The elevation of " + location + " above sea level in meters is " , new Relation(new Concept(location), "elevation", new Concept()));
        queries.put("The capital of " + location + " is " , new Relation(new Concept(location), "capital", new Concept()));
        queries.put("Here's the thing I know about " + location, new Relation(new Concept(location), "location", new Concept()));
        String toQuery = queries.keySet().toArray()[generator.nextInt(queries.size())].toString();
        try {
            List<Relation> queryAnswers = memory.retrieve(queries.get(toQuery));
            if (!queryAnswers.isEmpty()) {
                Relation queryAnswer = queryAnswers.get(0);
                String answer = (String) queryAnswer.object.getAttribute(Linguistics.NAME);
                return Lists.interpretationList(new Interpretation(toQuery + " " + answer)) ;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return Lists.interpretationList();


    }

     @Override
     public boolean determineSuccess(Interpretation interpretation) {
        return true;
    }

    @Override
    public Reaction react(Interpretation input) {
        return new Reaction(this.success);
    }

    }

