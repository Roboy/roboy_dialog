package roboy.dialog.personality.states;

import java.util.*;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;
import roboy.util.Concept;


public class QuestionAskingState extends AbstractBooleanState
{

//	private boolean first = true;
	//is used to change the name
    //smallTalkPersonality.setName();
//    private SmallTalkPersonality smallTalkPersonality;
//    private State top;
	private Concept objectOfFocus;
	private String currentIntent;
	private static final int TOASK = 3;
	private int questionsCount;
	private Map<String, List<String>> questions;
	private Random generator;



	public QuestionAskingState(Map<String, List<String>> questions)
	{
//        this.smallTalkPersonality = smallTalkPersonality;
		this.questions = questions;
		this.objectOfFocus = new Concept();
		this.questionsCount = 0;
		this.generator = new Random();


	}

	@Override
	public List<Interpretation> act() 
	{
		List<Interpretation> action = Lists.interpretationList();
		if(questionsCount==0)
		{
			//fist question is always name
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
		if("".equals(sentence))
		{
			return new Reaction(this,Lists.interpretationList()); // new Interpretation(SENTENCE_TYPE.GREETING)
		}

		// add to memory what was understood
		Triple triple = (Triple) input.getFeatures().get(Linguistics.TRIPLE); //TODO: improve triples formation

		if (triple.patiens!="")
		{
			objectOfFocus.addAttribute(this.currentIntent, triple.patiens);
			objectOfFocus.updateInMemory();
		}
		HashMap<String,Object> features = new HashMap<>();
		features.put("intent", currentIntent);
		return super.react(new Interpretation(sentence, features));

	}
	
	@Override
	protected boolean determineSuccess(Interpretation input)
	{
		// all questions are asked
		return TOASK==questionsCount;
	}

}