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
public class WildTalkState implements State{

//    private boolean talking = false;
    private Service generativeModel;
    private State next = this;

    public WildTalkState() {
    	edu.wpi.rail.jrosbridge.Ros ros = Ros.getInstance();
    	if(ros.isConnected()){
        	generativeModel = new Service(Ros.getInstance(), "/roboy/gnlp_predict", "generative_nlp/seq2seq_predict");
    	} else {
    		generativeModel = null;
    	}
	}
    
    @Override
    public List<Interpretation> act() {
        return Lists.interpretationList();
        //no need to act if wild is called more then once
//        if(talking) {
//            return Lists.interpretationList();
//        } else {
//            this.talking = true;
//            return Lists.interpretationList(new Interpretation("Oh, man, some deep stuff you're asking"));
//
//        }
    }

    @Override
    public Reaction react(Interpretation input) {
        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
        if(!sentence.isEmpty()) {
            return new Reaction(next, Lists.interpretationList(new Interpretation(callGenerativeModel(sentence))));
        }
        return new Reaction(next,Lists.interpretationList(new Interpretation("I am out of words.")));
    }

    protected String callGenerativeModel(String sentence) {
    	if(generativeModel==null){
    		return "I don't know what to say.";
    	} else {
            ServiceRequest request = new ServiceRequest("{\"text_input\": " + "\"" + sentence + "\"}");
            String response = generativeModel.callServiceAndWait(request).toString();

            JSONObject obj = new JSONObject(response);
            String text = obj.getString("text_output");
            return text;
    	}
    }
    
    public void setNextState(State next){
    	this.next = next;
    }

}
