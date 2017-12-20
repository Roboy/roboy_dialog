package roboy.dialog.personality;

import java.util.*;

import roboy.dialog.action.Action;
import roboy.dialog.action.FaceAction;
import roboy.dialog.action.SpeechAction;
import roboy.dialog.personality.states.*;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.memory.WorkingMemory;
import roboy.memory.nodes.Interlocutor;
import roboy.talk.Verbalizer;
import roboy.util.Lists;
import roboy.ros.RosMainNode;

/**
 * Currently, Roboys main personality. It tries to engage with people in a general
 * small talk, remembers what was said and answers questions. The small talk personality
 * is based on a state machine, where each input is interpreted in the context of the
 * state Roboy is currently in to determine respective answers.
 * 
 * The current state machine looks like this:
 * 
 * Greeting state
 *      |
 *      V
 * Introduction state     
 *      |
 *      V
 * Question Randomizer state
 *  |_Question Answering state
 *    |_Segue state
 *      |_Wild talk state
 *      
 * The Question Randomizer, Question Answering, Segue and Wilk talk states are stacked. If one
 * cannot give an appropriate reaction to the given utterance, the utterance is passed on to the
 * next one. The Wild talk state will always answer.
 *      
 * If a farewell is uttered the personality re-initializes to the Greeting state.
 * 
 * What the states do:
 * Greeting: 			Utters a greating
 * Introduction: 		Introduces himself and asks for the others name. Reacts differently  
 * 						depending on whether the other person is known.
 * Question Randomizer: Asks the other one questions about himself and stores the answers
 * 						in Roboy's memory.
 * Question Answering:	Answers questions if they are asked and Roboy knows the answer.
 * Segue:				Tells anecdotes from Today-I-Learneds from Reddit if corresponding
 * 						keywords are mentioned.
 * Wild talk:			Talks according to a neural network model trained on chat logs.
 * 
 */
public class SmallTalkPersonality implements Personality {

    private String name;

    private static final List<String> positive =
            Arrays.asList("enthusiastic", "awesome", "great", "very good",
                    "dope", "smashing", "happy", "cheerful", "good", "phantastic");
    private State state;
    private Verbalizer verbalizer;
    private RosMainNode rosMainNode;

    // The class saving information about the conversation partner.
    private Interlocutor person;

    public SmallTalkPersonality(Verbalizer verbalizer, RosMainNode node) {
        this.verbalizer = verbalizer;
        this.rosMainNode = node;
        this.initialize();
    }

    /**
     * Reacts to inputs based on the corresponding state Roboy is in. Each state
     * returns a reaction to what was said and then proactively takes an action of
     * its own. Both are combined to return the list of output actions.
     */
    @Override
    public List<Action> answer(Interpretation input) {

        try {

            if (Verbalizer.farewells.contains(input.getFeature(Linguistics.SENTENCE))) {
                this.initialize();
                return Lists.actionList(new SpeechAction(Verbalizer.farewells.get((int) (Math.random()*Verbalizer.farewells.size()))));
            }

            // Name to add into the connecting phrases later.
            //TODO remove old memory usage when new memory integration done
//            List<Triple> names = WorkingMemory.getInstance().retrieve(new Triple("is", "name", null));
//            if (!names.isEmpty()) {
//                name = names.get(0).patiens;
//            }
            String name = null;
            if(person!= null && person.getName() != null) {
                name = person.getName();
            }

            String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
            List<Action> act = Lists.actionList();

            // TODO Is this redundant compared to line 58?
            if (StatementInterpreter.isFromList(sentence, Verbalizer.farewells)) {
                //if found stop conversation
                state = new FarewellState();
            }

            //check for profanity words
            if (sentence.contains("profanity")) {
                act.add(0, new FaceAction("angry"));
            }

            // Get state-dependent reaction.
            Reaction reaction = state.react(input);
            // Add connecting phrases.
            if (name != null && Math.random() < 0.3) { // TODO: this should go in the Verbalizer
                List<String> namePhrases = Arrays.asList("So %s, ", "Hey %s, ", "%s, listen to me, ", "oh well, %s, ", "%s, ");
                String phrase = String.format(namePhrases.get(new Random().nextInt(4)), name);
                act.add(0, new SpeechAction(phrase));
            }
            // Build list of reactions.
            List<Interpretation> intentions = reaction.getReactions();
            for (Interpretation i : intentions) {
                act.add(verbalizer.verbalize(i));
            }

            try {
                // Move on to the next state and execute the intentions stage.
                state = reaction.getState();
                intentions = state.act();
                for (Interpretation i : intentions) {
                    act.add(verbalizer.verbalize(i));
                }
                if (input.getFeatures().containsKey(Linguistics.EMOTION)) {
                    act.add(new FaceAction((String) input.getFeatures().get(Linguistics.EMOTION)));
                }
                return act;
            }

            catch (NullPointerException e) {
                this.initialize();
                e.printStackTrace();
                return Lists.actionList(new FaceAction("shy"), new SpeechAction("Oopsie, got an exception just now. Recovering"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return Lists.actionList(new FaceAction("shy"), new SpeechAction("Oopsie, got an exception just now. Recovering"));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void initialize()
    {
        // initialize new conversation partner
        person = new Interlocutor();
        // build state machine
        GreetingState greetings = new GreetingState();
        IntroductionState intro = new IntroductionState(person);
        FarewellState farewell = new FarewellState();
        WildTalkState wild = new WildTalkState(rosMainNode);
        SegueState segue = new SegueState(wild);
        QuestionAnsweringState answer = new QuestionAnsweringState(segue);
        QuestionRandomizerState qa = new QuestionRandomizerState(answer, person);

        greetings.setNextState(intro);
        intro.setNextState(qa);
        qa.setTop(qa);
        answer.setTop(qa);
        wild.setNextState(qa);

        state = greetings;
    }
}
