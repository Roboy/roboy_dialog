package roboy.memory;

import java.util.*;

import org.apache.jena.query.*;
import org.apache.jena.sparql.*;
import org.apache.jena.rdf.model.*;

/**
 * Represents a Protege lexicon.
 */
public class Lexicon
{

	private List<LexiconPredicate> predicateList;
	private List<LexiconLiteral> literalList;
	private Boolean predicateFilled;
	private Boolean literalFilled;
	private List<String> permutationList;

	public Lexicon()
	{
		predicateList = new ArrayList<LexiconPredicate>();
		literalList = new ArrayList<LexiconLiteral>();
		predicateFilled = false;
		literalFilled = false;
	}


	public List<LexiconLiteral> getLiterals(String question, int limit, int topN, String choiceOfQuestion)
			throws Exception
	{
		//String questionClass = "Place";
		String questionClass = choiceOfQuestion;
		List<LexiconLiteral> interLiteralList = new ArrayList<LexiconLiteral>();
		permutationList = new ArrayList<String>();
		permutationList.add(question);

		
		if ( literalFilled )
		{
			for (LexiconLiteral literal : this.literalList)
			{
				if ( Arrays.asList(permutationList).contains(literal.QuestionMatch) )
				{
					interLiteralList.add(literal);
				}
			}

			return interLiteralList;
		} else {
			String bifContainsValue = "";
			for (String permutation : permutationList)
			{
				bifContainsValue = "";
				bifContainsValue += "\'" + permutation + "\'";
				String queryString = "select distinct ?subject ?literal ?redirects ?typeOfOwner ?redirectsTypeOfOwner where {"
						+ "?subject <http://www.w3.org/2000/01/rdf-schema#label> ?literal ."
						+ "?subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?typeOfOwner ."
						+ "optional { ?subject <http://dbpedia.org/ontology/wikiPageRedirects> ?redirects ."
						+ "optional {?redirects <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?redirectsTypeOfOwner ."
						+ "}} Filter ( !bound(?typeOfOwner) || ( (?typeOfOwner = <http://dbpedia.org/ontology/"
						+ questionClass
						+ ">))) . "
						+ "?literal <bif:contains> '\""
						+ permutation
						+ "\"'. } limit "
						+ limit;
				//System.out.println(queryString);
				Query queryObj = QueryFactory.create(queryString);
				String sparqlEndpoint = "http://dbpedia.org/sparql";
				QueryExecution qe = QueryExecutionFactory.sparqlService(sparqlEndpoint, queryObj);
				try
				{
					ResultSet literalResults = qe.execSelect();
					while (literalResults.hasNext())
					{
						QuerySolution qsolution = literalResults.nextSolution();
						RDFNode literalURI;
						RDFNode literalLabel = qsolution.get("literal");
						String resultTypeOfOwner = "";
						String resultQuestionMatch = permutation;
						LexiconLiteral tmpLexiconLiteral = new LexiconLiteral();

						if ( qsolution.get("redirects") != null )
						{
							literalURI = qsolution.get("redirects");
						} else {
							literalURI = qsolution.get("subject");

						}

						Boolean exists = false; // URI + Label only Exists

						for (LexiconLiteral x : interLiteralList)
						{

							if ( x.URI.equals(literalURI.toString()) && x.QuestionMatch.equals(resultQuestionMatch))
							{
								exists = true;
								break;
							}
						}
						
						//Each Literal URI contains a list of labels. 
						//Selecting the best Label based on Levenshtein Score
						if ( exists )
						{
							Iterator<LexiconLiteral> it = interLiteralList.iterator();
							String uri1=literalURI.toString();
							String label1=literalLabel.toString();
							while(it.hasNext())
							{	
								LexiconLiteral obj = it.next();	
							    if( obj.URI.equals(uri1) && !obj.label.equals(label1) )
							    {
							    	obj.label=bestLabelOf(obj.label, label1, obj.QuestionMatch);
							    }

							}
						} else {
							tmpLexiconLiteral.URI = literalURI.toString();
							tmpLexiconLiteral.QuestionMatch = resultQuestionMatch;
							String tmplabel=literalLabel.toString();
							//Sanitizing the label
							if ( tmplabel.matches(".*@.*") || tmplabel.matches("\\(.*\\)") )
							{
								tmplabel = tmplabel.substring(0, tmplabel.length() - 3);
								if ( tmplabel.matches("\\(.*\\)") )
								{
									tmplabel = tmplabel.replace("\\(.*\\)", " ");
									tmplabel = tmplabel.replace("  ", " ");
									tmplabel = tmplabel.trim();
								}
							}
							
							tmpLexiconLiteral.label = tmplabel;	
							interLiteralList.add(tmpLexiconLiteral);
						}

					}
				}catch (Exception e)
				{
					System.out.println("Exception caught: " + e.toString());
				}

			}
			literalList = scoreLiterals(interLiteralList, topN);
			// adding typeOfOwner to the finally short listed literalList
			literalList = addTypeOfOwner(literalList);
			literalFilled = true;
//			long endTime = System.currentTimeMillis();
//			for (LexiconLiteral lexiconLiteral : literalList)
//			{
//				System.out.println("Literal: " + lexiconLiteral.URI + " SCore: " + lexiconLiteral.score);
//				
//			}
			return literalList;
		}
	}

	
	public List<LexiconPredicate> scoreThesePredicates(List<LexiconPredicate> results, String question)
			throws Exception
	{
		List<LexiconPredicate> predicateListToSend = new ArrayList<LexiconPredicate>();
		for (LexiconPredicate lexiconPredicate : results)
		{
			Boolean itContains = false;
			for (String string : permutationList)
			{
				if ( lexiconPredicate.URI.contains(string) )
				{
					itContains = true;
				}
			}
			if ( itContains )
			{
				predicateListToSend.add(lexiconPredicate);
			}
		}
		return predicateListToSend;
	}

	
	public List<LexiconLiteral> addTypeOfOwner(List<LexiconLiteral> results)
	{
		for (LexiconLiteral literal : results)
		{

			String queryString = "select distinct ?type where{" + "<" + literal.URI + ">"
					+ "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type.}";

			Query queryObj = QueryFactory.create(queryString);
			String sparqlEndpoint = "http://dbpedia.org/sparql";
			QueryExecution queryExec = QueryExecutionFactory.sparqlService(sparqlEndpoint, queryObj);
			try
			{
				ResultSet typeOfOwnerResults = queryExec.execSelect();
				while (typeOfOwnerResults.hasNext())
				{
					QuerySolution qsolution = typeOfOwnerResults.nextSolution();
					RDFNode typeOfOwner = qsolution.get("type");
					if ( typeOfOwner != null )
					{
						literal.typeOfOwner.add(typeOfOwner.toString());
					}
				}
			}catch (Exception e)
			{
				System.out.println("Exception caught: " + e.toString());
			}
		}
		return results;
	}

	public List<LexiconLiteral> scoreLiterals(List<LexiconLiteral> results, int n)
	{

		for (LexiconLiteral literal : results)
		{
			String tmplabel;

			if ( literal.label.matches(".*@.*") || literal.label.matches("\\(.*\\)") )
			{
				tmplabel = literal.label.substring(0, literal.label.length() - 3);
				if ( literal.label.matches("\\(.*\\)") )
				{
					tmplabel = tmplabel.replace("\\(.*\\)", " ");
					tmplabel = tmplabel.replace("  ", " ");
					tmplabel = tmplabel.trim();
				}

			} else {
				tmplabel = literal.label;
			}

			literal.score = Util.calculateLevenshteinDistance(literal.QuestionMatch, tmplabel);

		}

		HashSet<LexiconLiteral> dupRemovedResults = new LinkedHashSet<LexiconLiteral>();
		for (LexiconLiteral literal : results)
		{
			for (LexiconLiteral literal2 : results)
			{

				if ( literal.equals(literal2) )
				{
					dupRemovedResults.add(literal);

				}
				if ( literal.URI == literal2.URI && !literal.equals(literal2) )
				{

					dupRemovedResults.add((literal.score <= literal2.score) ? literal : literal2);

				}
			}
		}
		List<LexiconLiteral> resultToSend = new ArrayList<LexiconLiteral>();
		resultToSend.addAll(dupRemovedResults);
		Collections.sort(resultToSend);
		if ( resultToSend.size() < n )
		{
			n = resultToSend.size();
		}

		return resultToSend.subList(0, n);
	}

	private List<LexiconPredicate> addDomainAndRange(List<LexiconPredicate> predicateList)
	{

		for (LexiconPredicate lexiconPredicate : predicateList)
		{
			String queryString = "Select distinct ?domain ?range where { {" +

			"<" + lexiconPredicate.URI + ">" + "<http://www.w3.org/2000/01/rdf-schema#domain> ?domain.}" + "union { <"
					+ lexiconPredicate.URI + ">" + " <http://www.w3.org/2000/01/rdf-schema#range> ?range ." + "}}";

			Query queryObj = QueryFactory.create(queryString);
			String sparqlEndpoint = "http://dbpedia.org/sparql";
			QueryExecution qe = QueryExecutionFactory.sparqlService(sparqlEndpoint, queryObj);
			ResultSet domainAndRangeResults = qe.execSelect();

			while (domainAndRangeResults.hasNext())
			{
				QuerySolution domainAndRangeSolution = domainAndRangeResults.nextSolution();
				if ( domainAndRangeSolution.get("domain") != null )
				{
					if ( !lexiconPredicate.domains.contains(domainAndRangeSolution.get("domain").toString()) )
					{
						lexiconPredicate.domains.add(domainAndRangeSolution.get("domain").toString());
					}
				}
				if ( domainAndRangeSolution.get("range") != null )
				{
					if ( !lexiconPredicate.ranges.contains(domainAndRangeSolution.get("range").toString()) )
					{
						lexiconPredicate.ranges.add(domainAndRangeSolution.get("range").toString());
					}
				}
			}
		}
		return predicateList;
	}

	public List<String> getPermutations(String question) throws Exception
	{
		
		Set<String> permutationList = new LinkedHashSet<>();

		List<String> splitQuestion = new ArrayList<String>();
		splitQuestion = Arrays.asList(question.split(" "));
		String wordSpace = "";
		String wordNoSpace = "";

		int splitQuestionSize = splitQuestion.size();
		for (int i = 1; i < splitQuestionSize; i++)
		{
			for (int j = 0; j < (splitQuestionSize - (i - 1)); j++)
			{
				for (int j2 = j; j2 < (i + j); j2++)
				{
					wordSpace += splitQuestion.get(j2) + " ";
					wordNoSpace += splitQuestion.get(j2);
				}
				permutationList.add(wordSpace.trim());
				wordNoSpace = "";
				wordSpace = "";
			}
		}
		permutationList.add(question.trim());
		
		List<String> returningPermutationList = new ArrayList<String>();
		returningPermutationList.addAll(permutationList);
		return returningPermutationList;
	}
	
	String bestLabelOf(String objlabel, String label1, String permutation)
	{
		if ( label1.matches(".*@.*") || label1.matches("\\(.*\\)") )
		{
			label1 = label1.substring(0, label1.length() - 3);
			if ( label1.matches("\\(.*\\)") )
			{
				label1 = label1.replace("\\(.*\\)", " ");
				label1 = label1.replace("  ", " ");
				label1 = label1.trim();
			}
		}
		
		int objlabelScore = Util.calculateLevenshteinDistance(permutation, objlabel);
		int label1Score = Util.calculateLevenshteinDistance(permutation, label1);
		return (objlabelScore<label1Score) ? objlabel:label1;
	}
}
