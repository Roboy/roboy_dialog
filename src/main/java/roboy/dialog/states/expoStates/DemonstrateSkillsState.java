package roboy.dialog.states.expoStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jProperty;
import roboy.memory.nodes.Interlocutor;
import roboy.talk.PhraseCollection;
import roboy.util.RandomList;

enum SkillsSubintent {
    jokes,
    fun_facts,
    famous_entities,
    math
}

public class DemonstrateSkillsState extends State {
    public final static String INTENTS_HISTORY_ID = "RIS";

    private final String SELECTED_ABILITIES = "abilities";
    private final String SELECTED_ROBOY_QA = "roboy";
    private final String LEARN_ABOUT_PERSON = "newPerson";

    private final Logger LOGGER = LogManager.getLogger();

    private final RandomList<String> offerPhrases = new RandomList<>("Do you want me to %s?", "Would you like me to %s?", "Should I demonstrate you how I %s?");
    private final RandomList<String> connectingPhrases = PhraseCollection.SEGUE_CONNECTING_PHRASES;
    // private final RandomList<String> positivePhrases = new RandomList<>(" lets do it!", " behold me!", " I will give you the show of your life!", " that is the best decision!");
    private final RandomList<String> negativePhrases = new RandomList<>(" let me ask you a question then!", " was I not polite enough?", " lets switch gears!", " lets change the topic!");
    private final RandomList<String> roboyQAPhrases = new RandomList<>(" answer some questions about myself", " tell you more about myself");

    private State nextState;
    private SkillsSubintent currentSubintent = null;

    public DemonstrateSkillsState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        IntentValue intentValue = Context.getInstance().DIALOG_INTENTS.getLastValue();
        if (intentValue.getNeo4jPropertyValue() == Neo4jProperty.skills) {
            currentSubintent = detectSubintent(intentValue.getAttribute());
            return Output.say(getOfferSentence(intentValue.getAttribute()));
        } else {
            return Output.useFallback();
        }
    }

    @Override
    public Output react(Interpretation input) {
        Interlocutor person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();

        Linguistics.UtteranceSentiment inputSentiment = getInference().inferSentiment(input);
        LOGGER.info("The detected sentiment is " + inputSentiment);
        if (inputSentiment.toBoolean == Boolean.TRUE) {
            String answer = "";
            nextState = getRandomTransition();
            return Output.say(getConnectingPhrase(person.getName()) + answer);
        } else {
            nextState = getRandomTransition();
            return Output.say(getNegativeSentence(person.getName()));
        }
    }

    @Override
    public State getNextState() {
        return nextState;
    }

    private String getOfferSentence(String roboyOfferPhrase) {
        return String.format(offerPhrases.getRandomElement(), roboyOfferPhrase);
    }

    private String getConnectingPhrase(String name) {
        return String.format(connectingPhrases.getRandomElement(), name);
    }

    private String getNegativeSentence(String name) {
        return getConnectingPhrase(name) + negativePhrases.getRandomElement();
    }

    private SkillsSubintent detectSubintent(String attribute) {
        for (SkillsSubintent intent : SkillsSubintent.values()) {
            if (attribute.contains(intent.toString())) {
                return intent;
            }
        }
        return null;
    }

    private State getRandomTransition() {
        int dice = (int) (4 * Math.random() + 1);
        switch (dice) {
            case 1:
                return this;
            case 2:
                return getTransition(SELECTED_ABILITIES);
            case 3:
                return getTransition(SELECTED_ROBOY_QA);
            default:
                return getTransition(LEARN_ABOUT_PERSON);
        }
    }
}
