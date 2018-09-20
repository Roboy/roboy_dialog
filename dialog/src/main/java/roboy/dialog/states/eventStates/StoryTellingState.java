package roboy.dialog.states.eventStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yecht.ruby.Out;
import roboy.dialog.Segue;
import roboy.dialog.states.definitions.MonologState;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.emotions.RoboyEmotion;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.talk.Verbalizer;

import java.io.IOException;
import java.util.*;

import static roboy.util.UzupisIntents.*;


/**
 * A class that will replay a pre-recorded story as a sound file
 */
public class StoryTellingState extends State {

    private final Logger logger = LogManager.getLogger();
    private final String STORYFILES = "stories";

    // key = name, value = path
    private final HashMap<String, String> stories = new HashMap<>();
    private boolean askForFeedback = false;
    Map.Entry<String,String> storyToTell;

    public StoryTellingState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        String storiesRaw = params.getParameter(STORYFILES);
        String[] storiesArray = storiesRaw.split(";");
        for (int i=0; i<storiesArray.length; i++) {
            String[] kvs = storiesArray[i].split("=");
            stories.put(kvs[0],kvs[1]);
        }

        logger.info(" -> Available sounds: " + stories.keySet());
    }

    @Override
    public Output act() {
        if (askForFeedback)
            return Output.say(Verbalizer.askForFeedback.getRandomElement());
        // TODO ask what story if there're many
        if (stories.isEmpty()) {
            logger.error("StoryTellingState: no stories to tell");
            return Output.useFallback();
        }
        storyToTell = stories.entrySet().iterator().next();
        String phrase = "The story is called " + storyToTell.getKey() + ". Are you ready?";
        return Output.say(Verbalizer.startSomething.getRandomElement() + phrase);

    }

    @Override
    public Output react(Interpretation input) {
        if(askForFeedback) {
            askForFeedback = false;
            if (input.getSentiment()!=Linguistics.UtteranceSentiment.NEGATIVE) {
                return Output.say(Verbalizer.takePositiveFeedback.getRandomElement())
                        .setEmotion(RoboyEmotion.positive.getRandomElement());
            }
            else {
                return Output.say(Verbalizer.takeNegativeFeedback.getRandomElement());
            }
        }
        if(inferStoryWanted(input)) {
            askForFeedback = true;
            getRosMainNode().PlaySoundFile(storyToTell.getValue());
            return Output.say("That was it, you guys!");//.addSound(storyToTell.getValue());
        }

        return Output.say("oh well, next time then.");
    }

    private boolean inferStoryWanted(Interpretation input) {
        if (StatementInterpreter.isFromList(input.getSentence(), Verbalizer.denial)) {
            return false;
        }
        return true;
    }

    @Override
    public State getNextState() {

        if (askForFeedback) return this;
        return getTransition("next");

    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet(STORYFILES);
    }

}
