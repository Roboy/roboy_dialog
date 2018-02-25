package roboy.dialog.personality.states;

import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;
import roboy.ros.RosMainNode;

import java.util.List;

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
        if(sentence.isEmpty()) {
            return new Reaction(next,Lists.interpretationList(new Interpretation("I am out of words.")));
        }
        else {
            String reaction = rosMainNode.GenerateAnswer(sentence);
            return new Reaction(next, Lists.interpretationList(new Interpretation(reaction)));
        }
    }
    
    public void setNextState(State next){
    	this.next = next;
    }
}
