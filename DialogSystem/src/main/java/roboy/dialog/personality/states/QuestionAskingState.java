package roboy.dialog.personality.states;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.Linguistics.SENTENCE_TYPE;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.PASInterpreter;
import roboy.memory.DBpediaMemory;
import roboy.util.Lists;
import roboy.util.Relation;
import roboy.util.Concept;


public class QuestionAskingState extends AbstractBooleanState
{

	private boolean first = true;
	private State inner;
	private State top;
	private Concept objectOfFocus;
	private String currentIntention;
	private Map<String, List<String>> questions;



	public QuestionAskingState(State inner, Map<String, List<String>> questions)
	{
		this.inner = inner; 
		this.top = this;
		this.questions = questions;
		this.objectOfFocus = new Concept();
		objectOfFocus.addAttribute("object_class", "Person");
	
	}

	@Override
	public List<Interpretation> act() 
	{
		// pick intent randomly from the list
		Random generator = new Random();
		
		List<String> intents = new ArrayList (this.questions.keySet());
		this.currentIntention = intents.get(generator.nextInt(intents.size()));
		
		if (!objectOfFocus.hasAttribute(this.currentIntention))
		{
			// pick question formulation randomly
			List<String> questionsToAsk = questions.get(this.currentIntention);
			String questionToAsk = questionsToAsk.get(generator.nextInt(questionsToAsk.size()));
			List<Interpretation> action = Lists.interpretationList(new Interpretation(questionToAsk)); 
			return action;
		}

		return Lists.interpretationList();

	}

	@Override
	public Reaction react(Interpretation input) 
	{

		// interpret the answer

		// add to memory what was understood
		Triple triple = (Triple) input.getFeatures().get(Linguistics.TRIPLE);
		
		objectOfFocus.addAttribute(this.currentIntention, triple.patiens);


		// check if RoboyMind contains any information on the extracted attributes
		// list people with the same attributes
		// check if DBpedia has any info on the attribute
		// say the fact from DBpedia
		// send to question answering state if couldn't answer
		String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
		if("".equals(sentence))
		{
			return new Reaction(this,Lists.interpretationList()); // new Interpretation(SENTENCE_TYPE.GREETING)
		}
		return super.react(input);
	}
	
	@Override
	protected boolean determineSuccess(Interpretation input) {
		int questionsAsked = 0;
		List<String> intents = new ArrayList (this.questions.keySet());
		for (String intent: intents)
		{
			if (objectOfFocus.getProperties().contains(intent))
			{
				questionsAsked++;
			}
		}
		System.out.println(objectOfFocus.getProperties());
		System.out.println(objectOfFocus.getValues());
		if (questionsAsked>=3)
		{
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Reaction innerReaction(Interpretation input,List<Interpretation> result)
	{
		return inner.react(input);

	}
	
	public void setTop(State top){
		this.top = top;
	}

}