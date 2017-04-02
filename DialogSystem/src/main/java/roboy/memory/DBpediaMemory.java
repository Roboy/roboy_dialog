package roboy.memory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

import opennlp.tools.util.HashList;
import roboy.memory.LexiconLiteral;
import roboy.util.Concept;
import roboy.util.Lists;
import roboy.util.Maps;
import roboy.util.Relation;

public class DBpediaMemory implements Memory<Relation>{

//	static Concept  sub = new Concept("Berlin");
//	static Concept pre = new Concept("");
//	static Relation rel = new Relation(sub,"populationTotal",pre);
//	
//	public static void main(String[] args) throws InterruptedException, IOException{
//		
//		DBpediaMemory shit = new DBpediaMemory();
//		shit.retrieve(rel);
//	}
	
	
	@Override
	public boolean save(Relation object) throws InterruptedException, IOException {
		return false;
	}

//	private static final List<String> supportedRelations = Lists.stringList(
//			"areaCode",
//			"birthDate",
//			"birthPlace",
//			"birthYear",
//			"deathDate",
//			"deathPlace",
//			"deathYear",
//			"elevation",
//			"family",
//			"genre",
//			"kingdom",
//			"location",
//			"order",
//			"populationTotal",
//			"postalCode",
//			"birthDate",
//			"deathDate",
//			"birthPlace",
//			"deathPlace",
//			"careerStation",
//			"elevation",
//			"elevation",
//			"populationTotal");
private static final Map<String, String> supportedRelations = Maps.stringMap(
	 "areaCode","Place" ,
	 "birthDate","Person" ,
	 "birthPlace","Person" ,
	 "birthYear","Person" ,
	 "deathDate","Person" ,
	 "deathPlace","Person" ,
	 "deathYear","Person" ,
	 "elevation","Place" ,
	 "family","Person" ,
	 "genre","Thing" ,
	 "kingdom","Place" ,
	 "location","Place" ,
	 "order","Species" ,
	 "populationTotal","Place" ,
	 "postalCode","Place" ,
	 "birthDate","Person" ,
	 "deathDate","Person" ,
	 "birthPlace","Person" ,
	 "deathPlace","Person" ,
	 "careerStation","Person" ,
	 	"elevation","Place" ,
	 "elevation","Place" ,
	 "populationTotal","Place",
	"nationalAnthem","Place",
		"capital","Place",
		"officialLanguage","Place",
		"demonym","Place",
		"residence","Place",
		"currency","Place",
		 "occupation","Person",
		"spouse","Person",
		"almaMater","Person"
		);
	


private Map<String,String> forms;
	
	@Override
	public List<Relation> retrieve(Relation object) throws InterruptedException, IOException {
		LinkedHashSet<String> queries;
		List<Relation> result = new ArrayList();
		List<String> answersFinal = new ArrayList<String>();
		String objectToReturn = "";
		try {
			queries = buildQueries(object);
			int numberOfAnswersToSend = 5;
			int i = 0;
			for (String query : queries)
			{
				try
				{
					Query queryObj = QueryFactory.create(query);
					String sparqlEndpoint = "http://dbpedia.org/sparql";
					QueryExecution qe = QueryExecutionFactory.sparqlService(sparqlEndpoint, queryObj);
					ResultSet answersList = qe.execSelect();
					while (answersList.hasNext())
					{
						QuerySolution answer = answersList.nextSolution();
						RDFNode ans = answer.get("x");
						String answerStr = ans.toString();
						RDFNode label = answer.get("label");
						
							
						if ( ans != null )
						{
							if(label != null)
								answerStr = label.toString();
							if ( i < numberOfAnswersToSend )
							{						
								answersFinal.add(answerStr);
								if ( answerStr.matches(".*@.*") )
								{
									answerStr = answerStr.substring(0, answerStr.length() - 3);
									if ( answerStr.matches("\\(.*\\)") || answerStr.matches("\\(.*\\)") )
									{
										answerStr = answerStr.replace("\\(.*\\)", " ");
										answerStr = answerStr.replace("  ", " ");
										answerStr = answerStr.trim();
									}
								}
								
								result.add(new Relation(object.subject,object.predicate,new Concept(answerStr)));
								//System.out.println("Answer:"+object.subject.getValues()+":"+answerStr);
								i++;
							}		
						}
					}
				}
				catch (Exception e)
				{
					System.out.println("Houston, we have a problem! " + e.getMessage());
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if(object!=null&&supportedRelations.containsKey(object.predicate)){
			return result;
		}
		result.add(new Relation(object.subject,object.predicate,new Concept("")));
		return result;
	}
	

	public static LinkedHashSet<String> buildQueries(Relation object) throws Exception{
		LinkedHashSet<String> queries = new LinkedHashSet<String>();
		Lexicon lexicon = new Lexicon();
		List<LexiconLiteral> literalList = lexicon.getLiterals(object.subject.getValues(), 10, 5, supportedRelations.get(object.predicate));
		String predicate = object.predicate;
		List<String> predicateList = new ArrayList<String>();
		predicateList.add("http://dbpedia.org/ontology/"+predicate);
		predicateList.add("http://dbpedia.org/property/"+predicate);
		String query = "";
		for (LexiconLiteral lexiconLiteral : literalList)
		{
			for (String predicateURI: predicateList)
			{
				query = "Select * where {<"
						+ lexiconLiteral.URI
						+ "> <"
						+ predicateURI
						+ "> ?x. "
						+ "OPTIONAL { ?x <http://www.w3.org/2000/01/rdf-schema#label> ?label FILTER ( lang(?label) = 'en' )}}";
				queries.add(query);
			}
				
			
		}
		return queries;
	}

}
