package roboy.dialog.personality.states;

import roboy.dialog.action.FaceAction;
import roboy.io.EmotionOutput;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.logic.StatementInterpreter;
import roboy.talk.Verbalizer;
import roboy.util.Lists;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import org.json.*;
import roboy.util.Ros;

import java.util.List;

public abstract class AbstractBooleanState implements State {

    protected State success;
    protected State failure;

    public State getSuccess() {
        return success;
    }

    public void setSuccess(State success) {
        this.success = success;
    }

    public State getFailure() {
        return failure;
    }

    public void setFailure(State failure) {
        this.failure = failure;
    }

    public void setNextState(State state) {
        this.success = this.failure = state;
    }

    @Override
    public Reaction react(Interpretation input) {
        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);

        //check for stop key words
        if (StatementInterpreter.isFromList(sentence, Verbalizer.farewells)) {
            //if found stop conversation
            return new Reaction(new FarewellState());
        }

        //check for profanity words
        if(sentence.contains("profanity")) {
            EmotionOutput emotion = new EmotionOutput();
            emotion.act(new FaceAction("angry"));
        }

        boolean successful = determineSuccess(input);
        if (successful) {
            return new Reaction(success);
        } else {
            return new Reaction(failure, Lists.interpretationList(new Interpretation(callGenerativeModel(sentence))));
        }
    }

    abstract protected boolean determineSuccess(Interpretation input);

    protected String callGenerativeModel(String sentence) {


        Service GenerativeModel = new Service(Ros.getInstance(), "/roboy/gnlp_predict", "generative_nlp/seq2seq_predict");
        System.out.println(sentence);
        ServiceRequest request = new ServiceRequest("{\"text_input\": " + "\"" + sentence + "\"}");
        String response = GenerativeModel.callServiceAndWait(request).toString();

        JSONObject obj = new JSONObject(response);
        String text = obj.getString("text_output");
        return text;
    }
}
