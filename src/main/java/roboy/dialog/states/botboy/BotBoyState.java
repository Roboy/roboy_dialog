package roboy.dialog.states.botboy;

import org.apache.jena.atlas.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.talk.Verbalizer;

import java.util.HashMap;
import java.util.Map;

public class BotBoyState extends State {

    private final Logger logger = LogManager.getLogger();
    private final String TRANSITION_INITIALIZED = "initialized";
    private State next;

    public BotBoyState(String stateIdentifiers, StateParameters params){
        super(stateIdentifiers, params);

    }

    // first we wait for conversation to start
    @Override
    public Output act() {
        return Output.sayNothing();
    }


    @Override
    public Output react(Interpretation input) {
        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
        String chatID = (String) input.getFeature("chat-id");

        boolean inputOK = StatementInterpreter.isFromList(sentence, Verbalizer.greetings) ||
                StatementInterpreter.isFromList(sentence, Verbalizer.roboyNames) ||
                StatementInterpreter.isFromList(sentence, Verbalizer.triggers);

        if(inputOK){
            next = getTransition(TRANSITION_INITIALIZED);
            String response = Verbalizer.greetings.getRandomElement();
            Log.error(this, "produced response: "+response);
            //return Output.say(response);

            // add chat-id to attributes
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("chat-id", chatID);

            Interpretation output = new Interpretation(response, attributes);
            return Output.say(output);
        }

        return Output.sayNothing();
    }

    @Override
    public State getNextState() {
        return next;
    }


}
