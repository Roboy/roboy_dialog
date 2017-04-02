package roboy.logic;

import java.util.Map;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SEMANTIC_ROLE;
import roboy.util.Concept;
import roboy.util.Maps;
import roboy.util.Relation;

public class PASInterpreter {
	
	private static final Map<String, String> dbpediaRelations =
			Maps.stringMap(
					"the area code","areaCode",
					"the birth date","birthDate",
					"the birth place","birthPlace",
					"the birth year","birthYear",
					"the death date","deathDate",
					"the death place","deathPlace",
					"the death year","deathYear",
					"the elevation","elevation",
					"the family","family",
					"the genre","genre",
					"the kingdom","kingdom",
					"the location","location",
					"the order","order",
					"the population","populationTotal",
					"the postal code","postalCode",
					"when-born","birthDate",
					"when-die","deathDate",
					"where-born","birthPlace",
					"where-die","deathPlace",
					"where-work","careerStation",
					"high","elevation",
					"tall","elevation",
					"how many people-live","populationTotal",
					"national anthem","nationalAnthem",
					"capital","capital",
					"official language","officialLanguage",
					"demonym","demonym",
					"residence","residence",
					"currency","currency",
					"occupation", "occupation",
					"spouse","spouse",
					"wife","spouse",
					"husband","spouse",
					"alma mater","almaMater"
					);
	
	public static Relation pas2DBpediaRelation(Map<String,Object> pas){

		Object predicate = pas.get(SEMANTIC_ROLE.PREDICATE);
		Object agent = pas.get(SEMANTIC_ROLE.AGENT);
		Object patient = pas.get(SEMANTIC_ROLE.PATIENT);
		Object time = pas.get(SEMANTIC_ROLE.TIME);
		Object location = pas.get(SEMANTIC_ROLE.LOCATION);
		Object manner = pas.get(SEMANTIC_ROLE.MANNER);
		
		if(
				agent != null && 
				predicate != null &&
				patient != null &&
				((String)agent).toLowerCase().startsWith("what") &&
				Linguistics.beMod.contains(((String)predicate).toLowerCase()) &&
				((String)patient).contains(" of ")
				){
			String[] parts = ((String)patient).split("of");
			if(dbpediaRelations.containsKey(parts[0].trim())){
				return new Relation(new Concept(parts[1].trim()),dbpediaRelations.get(parts[0].trim()),null);
			}
		} else if(
				time != null && 
				predicate != null &&
				patient != null &&
				((String)time).toLowerCase().startsWith("when") &&
				dbpediaRelations.containsKey("when-"+predicate)
				){
			return new Relation(new Concept((String)patient),dbpediaRelations.get("when-"+predicate),null);
		} else if(
				time != null && 
				predicate != null &&
				agent != null &&
				((String)time).toLowerCase().startsWith("when") &&
				dbpediaRelations.containsKey("when-"+predicate)
				){
			return new Relation(new Concept((String)agent),dbpediaRelations.get("when-"+predicate),null);
		} else if(
				location != null && 
				predicate != null &&
				patient != null &&
				((String)location).toLowerCase().startsWith("where") &&
				dbpediaRelations.containsKey("where-"+predicate)
				){
			return new Relation(new Concept((String)patient),dbpediaRelations.get("where-"+predicate),null);
		} else if(
				location != null && 
				predicate != null &&
				agent != null &&
				((String)location).toLowerCase().startsWith("where") &&
				dbpediaRelations.containsKey("where-"+predicate)
				){
			return new Relation(new Concept((String)agent),dbpediaRelations.get("where-"+predicate),null);
		} else if(
				manner != null && 
				predicate != null &&
				patient != null &&
				agent != null && 
				Linguistics.beMod.contains(((String)predicate).toLowerCase()) &&
				((String)manner).toLowerCase().startsWith("how") &&
				dbpediaRelations.containsKey(agent)
				){
			return new Relation(new Concept((String)patient),dbpediaRelations.get(agent),null);
		} else if(
				predicate != null &&
				location != null &&
				agent != null && 
				((String)agent).toLowerCase().startsWith("how") &&
				dbpediaRelations.containsKey(agent.toString().toLowerCase()+"-"+predicate)
				){
			return new Relation(new Concept((String)location),dbpediaRelations.get(agent.toString().toLowerCase()+"-"+predicate),null);
		}
			
		return null;
	}

}
