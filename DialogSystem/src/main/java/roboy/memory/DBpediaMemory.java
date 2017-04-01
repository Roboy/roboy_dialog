package roboy.memory;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import roboy.util.Concept;
import roboy.util.Lists;
import roboy.util.Relation;

public class DBpediaMemory implements Memory<Relation>{

	@Override
	public boolean save(Relation object) throws InterruptedException, IOException {
		return false;
	}

	private static final List<String> supportedRelations = Lists.stringList(
			"areaCode",
			"birthDate",
			"birthPlace",
			"birthYear",
			"deathDate",
			"deathPlace",
			"deathYear",
			"elevation",
			"family",
			"genre",
			"kingdom",
			"location",
			"order",
			"populationTotal",
			"postalCode",
			"birthDate",
			"deathDate",
			"birthPlace",
			"deathPlace",
			"careerStation",
			"elevation",
			"elevation",
			"populationTotal");

	
	@Override
	public List<Relation> retrieve(Relation object) throws InterruptedException, IOException {
		// TODO: Replace this with actual DBpedia call
		List<Relation> result = new ArrayList();
		if(object!=null&&supportedRelations.contains(object.predicate)){
			result.add(new Relation(object.subject,object.predicate,new Concept("Putin")));
		}
		return result;
	}

}
