package roboy.memory;

import java.io.IOException;
import java.util.List;

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
	public Relation retrieve(Relation object) throws InterruptedException, IOException {
		// TODO: Replace this with actual DBpedia call
		if(object!=null&&supportedRelations.contains(object.predicate)){
			return new Relation(object.subject,object.predicate,new Concept("Putin"));
		}
		return null;
	}

}
