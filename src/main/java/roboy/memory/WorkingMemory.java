package roboy.memory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboy.linguistics.Triple;
@Deprecated
public class WorkingMemory implements Memory<Triple>{
	
	private static WorkingMemory memory;

	private Map<String,List<Triple>> agensTripleMap = new HashMap<>();
	private Map<String,List<Triple>> patiensTripleMap = new HashMap<>();
	private Map<String,List<Triple>> predicateTripleMap = new HashMap<>();
	
    public static WorkingMemory getInstance()
    {
        if (memory == null)
        {
            memory = new WorkingMemory();
        }
        return memory;
    }
    
    private WorkingMemory(){}
	
	@Override
	public boolean save(Triple object) {
		addToMap(agensTripleMap,object.agens.toLowerCase(), object);
		addToMap(patiensTripleMap,object.patiens.toLowerCase(), object);
		addToMap(predicateTripleMap,object.predicate.toLowerCase(), object);
		return true;
	}
	
	@Override
	public String toString(){
		return predicateTripleMap.toString();
	}
	
	private void addToMap(Map<String,List<Triple>> list, String s, Triple t){
		if(!list.containsKey(s)){
			list.put(s, new ArrayList<Triple>());
		}
		list.get(s).add(t);
	}


	public List<Triple> retrieve(Triple object) {
		List<Triple> agensTriples = null;
		List<Triple> patiensTriples = null;
		List<Triple> predicateTriples = null;
		if(object.agens!=null){
			agensTriples = agensTripleMap.get(object.agens.toLowerCase());
			if(agensTriples==null) return new ArrayList<>();
		}
		if(object.patiens!=null){
			patiensTriples = patiensTripleMap.get(object.patiens.toLowerCase());
			if(patiensTriples==null) return new ArrayList<>();
		}
		if(object.predicate!=null){
			predicateTriples = predicateTripleMap.get(object.predicate.toLowerCase());
			if(predicateTriples==null) return new ArrayList<>();
		}
		List<Triple> results = new ArrayList<>();
		if(agensTriples!=null){
			for(Triple t: agensTriples){
				if(		
						(patiensTriples==null || patiensTriples.contains(t)) &&
						(predicateTriples==null || predicateTriples.contains(t))
						) results.add(t);
			}
		}
		if(patiensTriples!=null){
			for(Triple t: patiensTriples){
				if(		
						!results.contains(t) &&
						(agensTriples==null || agensTriples.contains(t)) &&
						(predicateTriples==null || predicateTriples.contains(t))
						) results.add(t);
			}
		}
		if(predicateTriples!=null){
			for(Triple t: predicateTriples){
				if(		
						!results.contains(t) &&
						(agensTriples==null || agensTriples.contains(t)) &&
						(patiensTriples==null || patiensTriples.contains(t))
						) results.add(t);
			}
		}
		return results;
	}

}
