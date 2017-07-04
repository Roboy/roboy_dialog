package roboy.dialog.personality;

import java.util.*;

import roboy.dialog.action.Action;
import roboy.dialog.action.FaceAction;
import roboy.dialog.action.SpeechAction;
import roboy.dialog.personality.states.*;
import roboy.io.EmotionOutput;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.talk.Verbalizer;
import roboy.util.Lists;

/**
 * Currently Roboys main personality. It tries to engage with people in a general
 * small talk, remember what was said and answer questions. The small talk personality
 * is based on a state machine, where each input is interpreted in the context of the
 * state Roboy is currently in to determine respective answers.
 * 
 * Since this was used for the last demo, quite some refaktoring is needed here to 
 * tidy up the code again.
 */
public class SmallTalkPersonality implements Personality {

    private String name;

    private static final List<String> positive =
            Arrays.asList("enthusiastic", "awesome", "great", "very good",
                    "dope", "smashing", "happy", "cheerful", "good", "phantastic");
    private State state;
    private Verbalizer verbalizer;

    public SmallTalkPersonality(Verbalizer verbalizer) {
        this.verbalizer = verbalizer;


        List<String> nameQuestions = Arrays.asList(
                "How can I call you?",
                "What is your name?",
                "Who are you?");
        List<String> occupationQuestions = Arrays.asList(
                "What do you do?",
                "What is your profession?");
        List<String> originQuestions = Arrays.asList(
                "Where are you from?",
                "Where were you born?",
                "I have hard time guessing your home country. What is it?",
                "Which town do you call your home?");
        List<String> hobbyQuestions = Arrays.asList(
                "What is your favorive thing to do?",
                "What is your hobby?",
                "What do you do in your free time?",
                "How do you spend your free time?",
                "What is your favorive pasttime activity?");
        List<String> moviesQuestions = Arrays.asList(
                "What is your favourite movie?",
                "What was the last movie you saw in the cinema?",
                "What is your favorite TV show?",
                "Which comedy do you like the most?");

        Map<String, List<String>> questions = new HashMap<>();
        questions.put("name", nameQuestions);
        questions.put("origin", originQuestions);
        questions.put("hobby", hobbyQuestions);
        questions.put("movies", moviesQuestions);
        questions.put("occupation", occupationQuestions);

        // build state machine
        GreetingState greetings = new GreetingState();
        IntroductionState intro = new IntroductionState();
        FarewellState farewell = new FarewellState();
        WildTalkState wild = new WildTalkState();
        SegueState segue = new SegueState(wild);
        QuestionAnsweringState answer = new QuestionAnsweringState(segue);
        QuestionRandomizerState qa = new QuestionRandomizerState(answer);

        greetings.setNextState(intro);
        answer.setTop(qa);
        qa.setTop(qa);
        intro.setNextState(qa);
        wild.setNextState(qa);

        state = greetings;
    }

    /**
     * Reacts to inputs based on the corresponding state Roboy is in. Each state
     * returns a reaction to what was said and then proactively takes an action of
     * its own. Both are combined to return the list of output actions.
     */
    @Override
    public List<Action> answer(Interpretation input) {

        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
        List<Action> act = Lists.actionList();

        if (StatementInterpreter.isFromList(sentence, Verbalizer.farewells))
        {
            //if found stop conversation
            state = new FarewellState();
        }

        //check for profanity words
        if(sentence.contains("profanity"))
        {
            act.add(0, new FaceAction("angry"));
        }

        Reaction reaction = state.react(input);

        if (name != null && Math.random() < 0.3) { // TODO: this should go in the Verbalizer
            List<String> namePhrases = Arrays.asList("So %s, ", "Hey %s, ", "%s, listen to me, ", "oh well, %s, ", "%s, ");
            String phrase = String.format(namePhrases.get(new Random().nextInt(4)), name);
            act.add(0, new SpeechAction(phrase));
        }
        List<Interpretation> intentions = reaction.getReactions();
        for (Interpretation i : intentions) {
            act.add(verbalizer.verbalize(i));
        }
        state = reaction.getState();
        intentions = state.act();
        for (Interpretation i : intentions) {
            act.add(verbalizer.verbalize(i));
        }
        return act;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
