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
		this.objectOfFocus.memorize("Person");
//		this.objectOfFocus.addAttribute("object_class", "Person");

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

		String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
		if("".equals(sentence))
		{
			return new Reaction(this,Lists.interpretationList()); // new Interpretation(SENTENCE_TYPE.GREETING)
		}

		// add to memory what was understood
		Triple triple = (Triple) input.getFeatures().get(Linguistics.TRIPLE);

		if (triple.patiens!=null)
		{
			objectOfFocus.addAttribute(this.currentIntention, triple.patiens);
		}

		return super.react(input);

	}
	
	@Override
	protected boolean determineSuccess(Interpretation input) {
		String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
		return ("".equals(sentence))?false:true;
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