package roboy.dialog.personality.states;

import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import org.json.JSONObject;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;
import roboy.util.Ros;
import roboy.util.RosMainNode;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The generative model talking wildly.
 */
public class WildTalkState implements State{


    private State next = this;
    private RosMainNode rosMainNode;

    public WildTalkState(RosMainNode node)
    {
        this.rosMainNode = node;
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
            String reaction = rosMainNode.GenerateAnswer(sentence);
            return new Reaction(next, Lists.interpretationList(new Interpretation(reaction)));
        }
        return new Reaction(next,Lists.interpretationList(new Interpretation("I am out of words.")));
    }
    
    public void setNextState(State next){
    	this.next = next;
    }
}
