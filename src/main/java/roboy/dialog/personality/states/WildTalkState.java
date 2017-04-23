package roboy.dialog.personality.states;

import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import org.json.JSONObject;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;
import roboy.util.Ros;

import java.util.List;

/**
 * The generative model talking wildly.
 */
public class WildTalkState extends AbstractBooleanState {

    private boolean talking = false;

    @Override
    public List<Interpretation> act() {
        //no need to act if wild is called more then once
        if(talking) {
            return Lists.interpretationList();
        } else {
            this.talking = true;
            return Lists.interpretationList(new Interpretation("Oh, man, i don't even know what to say"));

        }
    }

    @Override
    public Reaction react(Interpretation input) {
        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
        if(!sentence.isEmpty()) {
            return new Reaction(this, Lists.interpretationList(new Interpretation(callGenerativeModel(sentence))));
        }
        //get out of the talking mode when leave the state
        talking = false;
        return super.react(input);
    }

    @Override
    protected boolean determineSuccess(Interpretation input) {
        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
        return !sentence.isEmpty();
    }


    protected String callGenerativeModel(String sentence) {
        Service GenerativeModel = new Service(Ros.getInstance(), "/roboy/gnlp_predict", "generative_nlp/seq2seq_predict");
        ServiceRequest request = new ServiceRequest("{\"text_input\": " + "\"" + sentence + "\"}");
        String response = GenerativeModel.callServiceAndWait(request).toString();

        JSONObject obj = new JSONObject(response);
        String text = obj.getString("text_output");
        return text;
    }
}
