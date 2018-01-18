package roboy.dialog.personality.states;

import java.io.IOException;
import java.util.*;

import roboy.dialog.personality.SmallTalkPersonality;
import roboy.io.Vision;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.*;
import roboy.memory.DBpediaMemory;
import roboy.memory.PersistentKnowledge;
import roboy.memory.RoboyMind;
import roboy.util.Lists;
import roboy.util.Concept;
import roboy.util.Relation;

/**
 * Is asking the other person questions about things that we can store in the Protege
 * memory.
 */
public class QuestionAskingState implements State
{

//	private boolean first = true;
	//is used to change the name
    //smallTalkPersonality.setName();
//    private SmallTalkPersonality smallTalkPersonality;
	private Concept objectOfFocus;
	private String currentIntent;
	private static final int TOASK = 2;
	private int questionsCount;
	private Map<String, List<String>> questions;
	private Random generator;
	private Map<String, State> children;
	private SmallTalkPersonality personality;

	private static final SimpleTokenizer tokenizer = new SimpleTokenizer();
	private static final OpenNLPPPOSTagger pos = new OpenNLPPPOSTagger();
	private static final OpenNLPParser parser = new OpenNLPParser();
	private static final AnswerAnalyzer answer = new AnswerAnalyzer();

	public QuestionAskingState(Map<String, List<String>> questions, Map<String,State> children, SmallTalkPersonality personality)
	{

		this.questions = questions;
		this.objectOfFocus = new Concept();
		this.questionsCount = 0;
		this.generator = new Random();
		this.children = children;
		this.personality = personality;

	}

	/**
	 * Asks first about the name of the other person and if called another time randomly
	 * about another possible other information.
	 */
	@Override
	public List<Interpretation> act() 
	{
		List<Interpretation> action = Lists.interpretationList();
		if(questionsCount==0)
		{
			//first question is always name
			if (questions.containsKey("name"))
			{
				currentIntent = "name";
				List<String> questionsOptions = questions.get(currentIntent);
				String questionToAsk = questionsOptions.get(generator.nextInt(questionsOptions.size()));
				action = Lists.interpretationList(new Interpretation(questionToAsk));
				questionsCount++;
			}

		}
		else
		{
			// pick intent randomly from the list
			List<String> intents = new ArrayList (questions.keySet());
			currentIntent = intents.get(generator.nextInt(intents.size()));

			if (!objectOfFocus.hasAttribute(currentIntent))
			{
				// pick question formulation randomly
				List<String> questionsToAsk = questions.get(currentIntent);
				String questionToAsk = questionsToAsk.get(generator.nextInt(questionsToAsk.size()));
				action = Lists.interpretationList(new Interpretation(questionToAsk));
				questionsCount++;
			}
		}

		return action;

	}

	@Override
	public Reaction react(Interpretation input) 
	{

		String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);


		// add to memory what was understood
		String answer = "";
		if (currentIntent.contains("occupation") || currentIntent.contains("hobby"))
		{
			answer = analyzePredicate(sentence);
		}
		else
		{
			answer = analyzeObject(sentence);
			if (currentIntent == "name")
			{
				personality.setName(answer);
			}
		}

		if (!"".equals(answer))
		{
			objectOfFocus.addAttribute(this.currentIntent, answer);
//			objectOfFocus.updateInMemory();
		}

		// react to the answer of the person
		List<Interpretation> reply = checkOwnMemory(input); // if the question was about Roboy


		reply.add(checkRoboyMind()); // in case Roboy knows a person with same name, hobby, etc.
		return new Reaction(determineNextState(input), reply);

	}

	protected State determineNextState(Interpretation input)
	{
		String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);

		if (TOASK==questionsCount)
		{
			return children.get("answer");
		}
		else if (sentence.isEmpty())
		{
			return children.get("idle");
		}
		return this;

	}

	private Interpretation checkRoboyMind()
	{
		Map<String, List<Concept>> matches = RoboyMind.getInstance().match(objectOfFocus);

		if ( matches.containsKey(currentIntent))
		{
			return new Interpretation("I know " + (matches.get(currentIntent).size()) + " people with the same " + currentIntent);
		}

		return new Interpretation("");
	}

	private Interpretation checkDBpedia() throws IOException, InterruptedException
	{

		List<Interpretation> replies = Lists.interpretationList();
		if (currentIntent == "name")
		{
			//request face recognition
			String recognizedFace = Vision.getInstance().recognizeFace();
			if (!recognizedFace.isEmpty())
			{
				replies.add(new Interpretation("Wow " + objectOfFocus.getAttribute("name") + " you actually look like" + recognizedFace));
			}

			Map<String, String> possibleQuestions = new HashMap<>();
			possibleQuestions.put("Who is " + recognizedFace, recognizedFace + " is ");
			possibleQuestions.put("when was " + recognizedFace + " born", recognizedFace + " was born in ");
			possibleQuestions.put("where was " + recognizedFace + " born", recognizedFace + " was born in ");
			possibleQuestions.put("what does " + recognizedFace + " do", recognizedFace + " is ");
			possibleQuestions.put("where did " + recognizedFace + " study", recognizedFace + " studied at ");


			String currentQuestion = possibleQuestions.keySet().toArray()[generator.nextInt(possibleQuestions.size())].toString();
			Interpretation interpretation = new Interpretation (currentQuestion);
			interpretation = parser.analyze(interpretation);
			Map<String,Object> pas = (Map<String,Object>) interpretation.getFeature(Linguistics.PAS);
			Concept object = new Concept((String) pas.get(Linguistics.SEMANTIC_ROLE.PATIENT));
			Concept subject = new Concept((String) pas.get(Linguistics.SEMANTIC_ROLE.AGENT));
			String predicate = (String) pas.get(Linguistics.SEMANTIC_ROLE.PREDICATE);
			List<Relation> retrieved = new ArrayList<>();
			try
			{
				retrieved = DBpediaMemory.getInstance().retrieve(new Relation(subject,predicate,object));
			}
			catch (Exception e)
 			{
				e.printStackTrace();
			}
			if (!retrieved.isEmpty())
			{
				replies.add(new Interpretation(possibleQuestions.get(currentQuestion) + retrieved.get(generator.nextInt(retrieved.size()))));
			}
		}
		return replies.get(generator.nextInt(replies.size()));
	}

	private List<Interpretation> checkOwnMemory(Interpretation input)
	{
		Triple triple = (Triple) input.getFeatures().get(Linguistics.TRIPLE);

		// reverse you <-> I
		if(triple.agens!=null && "you".equals(triple.agens.toLowerCase())) triple.agens = "i";
		if(triple.patiens!=null && "you".equals(triple.patiens.toLowerCase())) triple.patiens = "i";
		if(triple.predicate!=null && "are".equals(triple.predicate.toLowerCase())) triple.predicate = "am";

		//TODO remove this ugly parsing!
		List<Interpretation> result = new ArrayList<>();
		if(input.getSentenceType() == Linguistics.SENTENCE_TYPE.DOES_IT || input.getSentenceType() == Linguistics.SENTENCE_TYPE.IS_IT){
			List<Triple> t = PersistentKnowledge.getInstance().retrieve(new Triple(triple.predicate, triple.agens, null));
			if(!t.isEmpty()){
				result.add(new Interpretation("Yes. "));
				for(int i=0; i<t.size(); i++){
					String prefix = (i>0 && i==t.size()-1) ? "also, " : "";
					result.add(new Interpretation(prefix+t.get(i).agens+" "+t.get(i).predicate+" "+t.get(i).patiens));
				}
			}
		} else if(input.getSentenceType() == Linguistics.SENTENCE_TYPE.WHO){
			List<Triple> t = PersistentKnowledge.getInstance().retrieve(new Triple(triple.predicate, triple.agens, triple.patiens));
			if(!t.isEmpty()){
				for(int i=0; i<t.size(); i++){
					String prefix = (i>0 && i==t.size()-1) ? "also, " : "";
					result.add(new Interpretation(prefix+t.get(i).agens+" "+t.get(i).predicate+" "+t.get(i).patiens));
				}
			}
		} else if(input.getSentenceType() == Linguistics.SENTENCE_TYPE.WHAT){
			List<Triple> t = PersistentKnowledge.getInstance().retrieve(new Triple(triple.predicate, triple.agens, triple.patiens));
			if(!t.isEmpty()){

				for(int i=0; i<t.size(); i++){
					String prefix = (i>0 && i==t.size()-1) ? "also, " : "";
					result.add(new Interpretation(prefix+t.get(i).agens+" "+t.get(i).predicate+" "+t.get(i).patiens));
				}
			}
		} else if(input.getSentenceType() == Linguistics.SENTENCE_TYPE.HOW_DO){
			List<Triple> t = PersistentKnowledge.getInstance().retrieve(new Triple(triple.predicate, triple.agens, null));
			if(!t.isEmpty())
			{
				for(int i=0; i<t.size(); i++){
					String prefix = (i>0 && i==t.size()-1) ? "also, " : "";
					result.add(new Interpretation(prefix+t.get(i).agens+" "+t.get(i).predicate+" "+t.get(i).patiens));
				}
			}
		}
		return result;
	}

	private String analyzeObject(String sentence){ //TODO move analyzeObject to sentence analysis
		List<Analyzer> analyzers = new ArrayList<Analyzer>();
		analyzers.add(tokenizer);
		analyzers.add(pos);
		analyzers.add(parser);
		analyzers.add(answer);
		Interpretation interpretation = new Interpretation(sentence);
		for (Analyzer a : analyzers) interpretation = a.analyze(interpretation);
		return (String) interpretation.getFeature(Linguistics.OBJ_ANSWER);
	}

	private String analyzePredicate(String sentence){ //TODO move analyzePredicate to sentence analysis
		List<Analyzer> analyzers = new ArrayList<Analyzer>();
		analyzers.add(tokenizer);
		analyzers.add(pos);
		analyzers.add(parser);
		analyzers.add(answer);
		Interpretation interpretation = new Interpretation(sentence);
		for (Analyzer a : analyzers) interpretation = a.analyze(interpretation);
		return (String) interpretation.getFeature(Linguistics.PRED_ANSWER);
	}

}